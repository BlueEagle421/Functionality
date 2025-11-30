package com.blueeagle421.functionality.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import com.blueeagle421.functionality.block.custom.FishTrapBlock;

public class FishTrapEntity extends BlockEntity {

    private NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);

    private int ticksInWater = 0;
    private int targetTicks = 0;
    private boolean triggered = false;

    public FishTrapEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public FishTrapEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FISH_TRAP.get(), pos, state);
    }

    public void drops() {
        if (this.level == null || this.level.isClientSide)
            return;

        for (ItemStack stack : items) {
            if (stack != null && !stack.isEmpty()) {

                Containers.dropItemStack(this.level, this.worldPosition.getX(),
                        this.worldPosition.getY(), this.worldPosition.getZ(),
                        stack);
            }
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FishTrapEntity be) {
        if (level.isClientSide)
            return;

        int stage = state.getValue(FishTrapBlock.STAGE);
        if (stage >= 1)
            return;

        if (be.targetTicks == 0) {
            RandomSource rand = level.getRandom();
            // be.targetTicks = 24000 + rand.nextInt(24001);
            be.targetTicks = 30;
        }

        if (level.getFluidState(pos).getType() == Fluids.WATER) {
            be.ticksInWater++;
            if (be.ticksInWater >= be.targetTicks) {
                be.onFinish(level, pos, state);
            } else {
                if (be.ticksInWater % 1000 == 0)
                    be.setChanged();
            }
        }
    }

    private void onFinish(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide)
            return;

        int count = 1 + level.getRandom().nextInt(3);
        for (int i = 0; i < count; i++) {
            if (level instanceof ServerLevel serverLevel) {
                ItemStack loot = rollFishingLoot(serverLevel, Vec3.atCenterOf(pos), ItemStack.EMPTY, 0.0f);
                addToInventory(loot);
            }
        }

        BlockState newState = state.setValue(FishTrapBlock.STAGE, 1);
        level.setBlock(pos, newState, 3);

        level.updateNeighborsAt(pos, newState.getBlock());
        level.updateNeighborsAt(pos.below(), newState.getBlock());

        triggered = true;
        setChanged();
    }

    public ItemStack rollFishingLoot(ServerLevel level, Vec3 origin, ItemStack tool, float luck) {
        if (level == null)
            return ItemStack.EMPTY;

        LootParams.Builder builder = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, origin)
                .withParameter(LootContextParams.TOOL, tool == null ? ItemStack.EMPTY : tool)
                .withLuck(luck);

        LootParams lootParams = builder.create(LootContextParamSets.FISHING);

        LootTable lootTable = level.getServer().getLootData().getLootTable(BuiltInLootTables.FISHING);
        List<ItemStack> generated = lootTable.getRandomItems(lootParams);

        if (generated == null || generated.isEmpty())
            return ItemStack.EMPTY;

        return generated.get(level.getRandom().nextInt(generated.size()));
    }

    private void addToInventory(ItemStack stack) {
        if (stack.isEmpty())
            return;

        for (int i = 0; i < items.size(); i++) {
            ItemStack slot = items.get(i);
            if (!slot.isEmpty() && ItemStack.isSameItem(slot, stack) && slot.getCount() < slot.getMaxStackSize()) {
                int transferable = Math.min(stack.getCount(), slot.getMaxStackSize() - slot.getCount());
                slot.grow(transferable);
                stack.shrink(transferable);
                items.set(i, slot);
                if (stack.isEmpty()) {
                    setChanged();
                    return;
                }
            }
        }

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isEmpty()) {
                items.set(i, stack.copy());
                setChanged();
                return;
            }
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        items = NonNullList.withSize(9, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items);
        this.ticksInWater = tag.getInt("TicksInWater");
        this.targetTicks = tag.getInt("TargetTicks");
        this.triggered = tag.getBoolean("Triggered");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, items);
        tag.putInt("TicksInWater", ticksInWater);
        tag.putInt("TargetTicks", targetTicks);
        tag.putBoolean("Triggered", triggered);
    }
}
