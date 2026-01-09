package com.blueeagle421.functionality.mixin.betterLichens;

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
import com.blueeagle421.functionality.config.subcategories.features.BetterLichens;

import java.util.List;

@Mixin(MultifaceGrowthFeature.class)
public class MultifaceGrowthFeatureMixin {

    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void beforePlace(
            FeaturePlaceContext<MultifaceGrowthConfiguration> context,
            CallbackInfoReturnable<Boolean> cir) {

        BetterLichens cfg = FunctionalityConfig.COMMON.features.betterLichens;

        if (!cfg.isEnabled())
            return;

        if (context.config().placeBlock != Blocks.GLOW_LICHEN)
            return;

        if (!cfg.rollFeature(context.random())) {
            cir.setReturnValue(false);
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

        replaceVanillaLichen(level, pos, random);
        placeSurroundingLichen(level, pos, state, config, random, directions);
    }

    private static void replaceVanillaLichen(
            WorldGenLevel level, BlockPos pos, RandomSource random) {

        BetterLichens cfg = FunctionalityConfig.COMMON.features.betterLichens;

        if (!cfg.rollOverride(random))
            return;

        BlockState vanilla = level.getBlockState(pos);
        if (!vanilla.is(Blocks.GLOW_LICHEN))
            return;

        boolean canBeOvergrown = pos.getY() <= cfg.overgrownMaxHeight.get()
                && cfg.rollOvergrown(random);

        BlockState replacement = canBeOvergrown
                ? ModBlocks.OVERGROWN_LICHEN.get().defaultBlockState()
                : ModBlocks.BLOOM_LICHEN.get().defaultBlockState();

        for (Direction dir : Direction.values()) {
            var prop = MultifaceBlock.getFaceProperty(dir);
            if (vanilla.hasProperty(prop) && vanilla.getValue(prop)) {
                replacement = replacement.setValue(prop, true);
            }
        }

        level.setBlock(pos, replacement, 3);
    }

    private static void placeSurroundingLichen(
            WorldGenLevel level,
            BlockPos pos,
            BlockState state,
            MultifaceGrowthConfiguration config,
            RandomSource random,
            List<Direction> directions) {

        BetterLichens cfg = FunctionalityConfig.COMMON.features.betterLichens;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {

                if (dx == 0 && dz == 0)
                    continue;

                if (!cfg.rollSurrounding(random))
                    continue;

                BlockPos targetPos = pos.offset(dx, 0, dz);
                BlockState targetState = level.getBlockState(targetPos);

                if (!targetState.isAir() && !targetState.is(Blocks.WATER))
                    continue;

                MultifaceBlock lichen = cfg.rollDry(random)
                        ? (MultifaceBlock) ModBlocks.DRY_LICHEN.get()
                        : (MultifaceBlock) ModBlocks.LICHEN.get();

                for (Direction dir : directions) {
                    BlockPos supportPos = targetPos.relative(dir);
                    if (!level.getBlockState(supportPos).is(config.canBePlacedOn))
                        continue;

                    BlockState placed = lichen.getStateForPlacement(
                            targetState, level, targetPos, dir);

                    if (placed == null)
                        continue;

                    level.setBlock(targetPos, placed, 3);
                    level.getChunk(targetPos).markPosForPostprocessing(targetPos);

                    if (random.nextFloat() < config.chanceOfSpreading) {
                        lichen.getSpreader().spreadFromFaceTowardRandomDirection(
                                placed, level, targetPos, dir, random, true);
                    }

                    break;
                }
            }
        }
    }
}