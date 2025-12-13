package com.blueeagle421.functionality.utils;

import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.config.subcategories.features.InfiniteWaterCauldron;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CauldronUtils {

    private static Set<Block> INFINITE_SOURCE_BLOCKS = Collections.emptySet();

    @SuppressWarnings("deprecation")
    public static void reloadConfig() {
        Set<Block> blocks = new HashSet<>();

        for (ResourceLocation rl : config().getBlocksAsResourceLocations()) {

            Block block = BuiltInRegistries.BLOCK.getOptional(rl).orElse(null);

            if (block == null)
                continue;

            blocks.add(block);
        }

        INFINITE_SOURCE_BLOCKS = blocks;
    }

    public static boolean isInfiniteWaterSource(Level level, BlockPos pos) {
        if (!config().enabled.get())
            return false;

        if (INFINITE_SOURCE_BLOCKS.isEmpty())
            return false;

        BlockState belowState = level.getBlockState(pos.below());

        return INFINITE_SOURCE_BLOCKS.stream().anyMatch(belowState::is);
    }

    private static InfiniteWaterCauldron config() {
        return FunctionalityConfig.COMMON.features.infiniteWaterCauldron;
    }
}
