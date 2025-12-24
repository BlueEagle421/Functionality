package com.blueeagle421.functionality.block.entity.custom;

import com.blueeagle421.functionality.block.entity.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RepairAltarEntity extends BlockEntity {

    public RepairAltarEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public RepairAltarEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REPAIR_ALTAR.get(), pos, state);
    }

}
