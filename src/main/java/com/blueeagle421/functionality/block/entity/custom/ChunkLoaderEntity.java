package com.blueeagle421.functionality.block.entity.custom;

import com.blueeagle421.functionality.block.entity.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ChunkLoaderEntity extends BlockEntity {

    public ChunkLoaderEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public ChunkLoaderEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CHUNK_LOADER.get(), pos, state);
    }

}
