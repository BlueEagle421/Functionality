package com.blueeagle421.functionality.utils;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class CauldronUtils {

    private static final List<Block> INFINITE_SOURCE_BLOCKS = List.of(
            Blocks.DARK_PRISMARINE,
            Blocks.SEA_LANTERN,
            Blocks.PRISMARINE);

    public static boolean isInfiniteWaterSource(Level level, BlockPos pos) {
        BlockState belowState = level.getBlockState(pos.below());

        return INFINITE_SOURCE_BLOCKS.stream().anyMatch(b -> belowState.is(b));
    }
}
