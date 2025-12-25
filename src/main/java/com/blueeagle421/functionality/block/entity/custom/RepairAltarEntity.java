package com.blueeagle421.functionality.block.entity.custom;

import com.blueeagle421.functionality.block.custom.RepairAltarBlock;
import com.blueeagle421.functionality.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class RepairAltarEntity extends BlockEntity {
    private boolean active = false;

    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            RepairAltarEntity.this.setChanged();
        }
    };

    public RepairAltarEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REPAIR_ALTAR.get(), pos, state);
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        if (this.active == active)
            return;
        this.active = active;

        if (!active) {
            dropContents();
        }

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

    public void dropContents() {
        if (level == null || level.isClientSide)
            return;

        ItemStackHandler inv = this.getInventory();
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                Block.popResource(level, worldPosition, stack.copy());
                inv.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("Active", this.active);
        tag.put("inventory", inventory.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.active = tag.getBoolean("Active");
        if (tag.contains("inventory"))
            inventory.deserializeNBT(tag.getCompound("inventory"));
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
