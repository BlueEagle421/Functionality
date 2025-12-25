package com.blueeagle421.functionality.block.entity.custom;

import com.blueeagle421.functionality.block.custom.RepairAltarBlock;
import com.blueeagle421.functionality.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;

public class RepairAltarEntity extends BlockEntity {
    private boolean active = false;

    public RepairAltarEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REPAIR_ALTAR.get(), pos, state);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        if (this.active == active)
            return;
        this.active = active;

        if (this.level != null) {
            BlockState current = level.getBlockState(worldPosition);
            if (current.getBlock() instanceof RepairAltarBlock) {
                BlockState next = current.setValue(RepairAltarBlock.ACTIVE, active);
                level.setBlock(worldPosition, next, 3);
            }
            setChanged();
            level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition),
                    level.getBlockState(worldPosition), 3);
        } else {
            setChanged();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("Active", this.active);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.active = tag.getBoolean("Active");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
    }
}
