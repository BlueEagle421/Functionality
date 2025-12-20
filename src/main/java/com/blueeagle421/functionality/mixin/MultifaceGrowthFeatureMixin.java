package com.blueeagle421.functionality.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.MultifaceGrowthFeature;
import net.minecraft.world.level.levelgen.feature.configurations.MultifaceGrowthConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blueeagle421.functionality.block.ModBlocks;
import com.blueeagle421.functionality.config.FunctionalityConfig;

import java.util.List;

@Mixin(MultifaceGrowthFeature.class)
public class MultifaceGrowthFeatureMixin {

    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void beforePlace(FeaturePlaceContext<MultifaceGrowthConfiguration> context,
            CallbackInfoReturnable<Boolean> cir) {
        MultifaceGrowthConfiguration config = context.config();
        RandomSource random = context.random();
        var modConfig = FunctionalityConfig.COMMON.features.betterLichens;

        if (modConfig.enabled.get() && config.placeBlock == Blocks.GLOW_LICHEN) {
            if (random.nextFloat() > modConfig.lichenFeatureGenChance.get()) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "placeGrowthIfPossible", at = @At("RETURN"))
    private static void afterPlaceGrowth(
            WorldGenLevel level,
            BlockPos pos,
            BlockState state,
            MultifaceGrowthConfiguration config,
            RandomSource random,
            List<Direction> directions,
            CallbackInfoReturnable<Boolean> cir) {

        var modConfig = FunctionalityConfig.COMMON.features.betterLichens;

        if (!modConfig.enabled.get())
            return;

        if (config.placeBlock != Blocks.GLOW_LICHEN)
            return;

        if (!cir.getReturnValue())
            return;

        replaceVanillaLichen(level, pos);
        placeSurroundingLichen(level, pos, state, config, random, directions);
    }

    private static void replaceVanillaLichen(WorldGenLevel level,
            BlockPos pos) {
        BlockState vanillaState = level.getBlockState(pos);

        if (vanillaState.is(Blocks.GLOW_LICHEN)) {

            BlockState modState = ModBlocks.GLOW_LICHEN.get().defaultBlockState();

            for (Direction dir : Direction.values()) {
                if (vanillaState.hasProperty(MultifaceBlock.getFaceProperty(dir))
                        && vanillaState.getValue(MultifaceBlock.getFaceProperty(dir))) {
                    modState = modState.setValue(
                            MultifaceBlock.getFaceProperty(dir),
                            true);
                }
            }

            level.setBlock(pos, modState, 3);
        }
    }

    private static void placeSurroundingLichen(WorldGenLevel level,
            BlockPos pos,
            BlockState state,
            MultifaceGrowthConfiguration config,
            RandomSource random,
            List<Direction> directions) {

        var modConfig = FunctionalityConfig.COMMON.features.betterLichens;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0)
                    continue;

                BlockPos targetPos = pos.offset(dx, 0, dz);
                BlockState targetState = level.getBlockState(targetPos);

                if (random.nextFloat() > modConfig.surroundingLichenGenChance.get())
                    continue;

                if (!targetState.isAir() && !targetState.is(Blocks.WATER))
                    continue;

                MultifaceBlock lichen = random.nextFloat() < modConfig.dryLichenGenChance.get()
                        ? (MultifaceBlock) ModBlocks.DRY_LICHEN.get()
                        : (MultifaceBlock) ModBlocks.LICHEN.get();

                for (Direction dir : directions) {
                    BlockPos supportPos = targetPos.relative(dir);
                    BlockState supportState = level.getBlockState(supportPos);

                    if (!supportState.is(config.canBePlacedOn))
                        continue;

                    BlockState placedState = lichen.getStateForPlacement(targetState, level, targetPos, dir);
                    if (placedState == null)
                        continue;

                    level.setBlock(targetPos, placedState, 3);
                    level.getChunk(targetPos).markPosForPostprocessing(targetPos);

                    if (random.nextFloat() < config.chanceOfSpreading) {
                        lichen.getSpreader().spreadFromFaceTowardRandomDirection(
                                placedState, level, targetPos, dir, random, true);

                        break;
                    }
                }
            }
        }
    }
}