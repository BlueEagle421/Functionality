package com.blueeagle421.functionality.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.MultifaceGrowthFeature;
import net.minecraft.world.level.levelgen.feature.configurations.MultifaceGrowthConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blueeagle421.functionality.block.ModBlocks;

import java.util.List;

@Mixin(MultifaceGrowthFeature.class)
public class MultifaceGrowthFeatureMixin {

    @Inject(method = "placeGrowthIfPossible", at = @At("RETURN"))
    private static void afterPlaceGrowth(
            WorldGenLevel level,
            BlockPos pos,
            BlockState state,
            MultifaceGrowthConfiguration config,
            RandomSource random,
            List<Direction> directions,
            CallbackInfoReturnable<Boolean> cir) {

        if (!cir.getReturnValue())
            return;

        if (config.placeBlock != Blocks.GLOW_LICHEN)
            return;

        MultifaceBlock dryLichen = (MultifaceBlock) ModBlocks.DRY_LICHEN.get();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0)
                    continue;

                BlockPos targetPos = pos.offset(dx, 0, dz);
                BlockState targetState = level.getBlockState(targetPos);

                if (!targetState.isAir() && !targetState.is(Blocks.WATER))
                    continue;

                for (Direction dir : directions) {
                    BlockPos supportPos = targetPos.relative(dir);
                    BlockState supportState = level.getBlockState(supportPos);

                    if (!supportState.is(config.canBePlacedOn))
                        continue;

                    BlockState placedState = dryLichen.getStateForPlacement(targetState, level, targetPos, dir);
                    if (placedState == null)
                        continue;

                    level.setBlock(targetPos, placedState, 3);
                    level.getChunk(targetPos).markPosForPostprocessing(targetPos);

                    if (random.nextFloat() < config.chanceOfSpreading) {
                        dryLichen.getSpreader().spreadFromFaceTowardRandomDirection(
                                placedState, level, targetPos, dir, random, true);

                        break;
                    }
                }
            }
        }
    }
}