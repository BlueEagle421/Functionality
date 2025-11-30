package com.blueeagle421.functionality.worldgen.custom;

import com.blueeagle421.functionality.block.ModBlocks;
import com.mojang.serialization.Codec;
import java.util.OptionalInt;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.levelgen.Column;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class RiverChestFeature extends Feature<NoneFeatureConfiguration> {

    public RiverChestFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @SuppressWarnings("removal")
    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel world = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        int floorSearchRange = 32;

        OptionalInt maybeFloorY = getFloorY(world, origin, floorSearchRange);
        if (maybeFloorY.isEmpty())
            return false;

        int floorY = maybeFloorY.getAsInt();
        BlockPos floorPos = origin.atY(floorY);

        BlockPos chestPos = floorPos.below();

        BlockPos torchPos = floorPos.above();

        BlockState newChestBelowState = world.getBlockState(chestPos);
        if (newChestBelowState.is(Blocks.BEDROCK) || newChestBelowState.is(Blocks.CHEST)) {
            return false;
        }

        int genFlag = 3;

        ServerLevel level = context.level().getLevel();
        if (level == null || level.isClientSide) {
            return false;
        }

        world.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), genFlag);

        BlockEntity chestEntity = world.getBlockEntity(chestPos);
        if (chestEntity instanceof RandomizableContainerBlockEntity rcbe) {
            rcbe.setLootTable(new ResourceLocation("minecraft", "chests/simple_dungeon"), random.nextLong());
        }
        BlockState topState = world.getBlockState(torchPos);
        if (topState.is(Blocks.WATER) || topState.isAir()) {

            BlockState torchState = ModBlocks.GLOW_TORCH.get()
                    .defaultBlockState()
                    .setValue(BlockStateProperties.WATERLOGGED, Boolean.TRUE);

            world.setBlock(torchPos, torchState, genFlag);
        }

        return true;
    }

    private static OptionalInt getFloorY(WorldGenLevel world, BlockPos pos, int range) {
        Predicate<BlockState> isWater = (bs) -> bs.is(Blocks.WATER);
        Predicate<BlockState> notWater = (bs) -> !bs.is(Blocks.WATER);
        return Column.scan(world, pos, range, isWater, notWater).map(Column::getFloor).orElseGet(OptionalInt::empty);
    }
}
