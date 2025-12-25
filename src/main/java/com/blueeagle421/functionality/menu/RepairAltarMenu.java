package com.blueeagle421.functionality.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.ItemStackHandler;

import com.blueeagle421.functionality.block.ModBlocks;
import com.blueeagle421.functionality.block.custom.RepairAltarBlock;
import com.blueeagle421.functionality.block.entity.custom.RepairAltarEntity;
import com.blueeagle421.functionality.sound.ModSounds;

public class RepairAltarMenu extends ItemCombinerMenu {
    public static final int INPUT_SLOT = 0;
    public static final int ADDITIONAL_SLOT = 1;
    public static final int COPPER_BLOCK_SLOT = 2;
    public static final int RESULT_SLOT = 3;

    private final DataSlot repairCost = DataSlot.standalone();

    public RepairAltarMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public RepairAltarMenu(int containerId, Inventory inventory, FriendlyByteBuf buf) {
        this(
                containerId,
                inventory,
                ContainerLevelAccess.create(inventory.player.level(), buf.readBlockPos()));
    }

    public RepairAltarMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(ModMenus.REPAIR_ALTAR_MENU.get(), containerId, playerInventory, access);
        this.addDataSlot(repairCost);

        access.execute((level, pos) -> {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof RepairAltarEntity altar) {
                ItemStackHandler inv = altar.getInventory();
                if (!inv.getStackInSlot(0).isEmpty()) {
                    this.inputSlots.setItem(COPPER_BLOCK_SLOT, inv.getStackInSlot(0).copy());
                }
            }
        });
    }

    @Override
    protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create()
                .withSlot(INPUT_SLOT, 17, 47, stack -> true)
                .withSlot(ADDITIONAL_SLOT, 66, 47, stack -> true)
                .withSlot(COPPER_BLOCK_SLOT, 84, 47, stack -> stack.is(Items.COPPER_BLOCK))
                .withResultSlot(RESULT_SLOT, 142, 47)
                .build();
    }

    @Override
    protected boolean mayPickup(Player player, boolean hasStack) {
        return hasStack && repairCost.get() > 0;
    }

    @Override
    protected void onTake(Player player, ItemStack stack) {
        if (player.level().isClientSide)
            return;

        int cost = repairCost.get();

        float breakChance = ForgeHooks.onAnvilRepair(
                player, stack, this.inputSlots.getItem(0), this.inputSlots.getItem(1));

        clearInputSlot();
        consumeAdditionalMaterial(cost);
        consumeCopperBlock();
        repairCost.set(0);

        access.execute((level, pos) -> {
            if (!(level instanceof ServerLevel server))
                return;

            playRepairSound(server, pos);
            spawnParticles(server, pos);

            // "breaking" checks
            BlockState state = level.getBlockState(pos);
            if (!player.getAbilities().instabuild && state.is(BlockTags.ANVIL)
                    && player.getRandom().nextFloat() < breakChance) {

                if (player instanceof ServerPlayer serverPlayer)
                    serverPlayer.closeContainer();

                setInactive(level, state, pos);
            }
        });
    }

    private void setInactive(Level level, BlockState state, BlockPos pos) {
        BlockState newState = state;

        if (state.hasProperty(RepairAltarBlock.ACTIVE)) {
            newState = state.setValue(RepairAltarBlock.ACTIVE, false);
            level.setBlock(pos, newState, 3);
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof RepairAltarEntity altar) {
            altar.setActive(false);
        }

        playBreakSound((ServerLevel) level, pos);
    }

    private void clearInputSlot() {
        inputSlots.setItem(INPUT_SLOT, ItemStack.EMPTY);
    }

    private void consumeAdditionalMaterial(int cost) {
        if (cost <= 0)
            return;

        ItemStack material = inputSlots.getItem(ADDITIONAL_SLOT);
        if (!material.isEmpty() && material.getCount() > cost) {
            material.shrink(cost);
            inputSlots.setItem(ADDITIONAL_SLOT, material);
        } else {
            inputSlots.setItem(ADDITIONAL_SLOT, ItemStack.EMPTY);
        }
    }

    private void consumeCopperBlock() {
        ItemStack copper = inputSlots.getItem(COPPER_BLOCK_SLOT);
        if (copper.isEmpty())
            return;

        copper.shrink(1);
        if (copper.isEmpty())
            inputSlots.setItem(COPPER_BLOCK_SLOT, ItemStack.EMPTY);
        else
            inputSlots.setItem(COPPER_BLOCK_SLOT, copper);

        access.execute((level, pos) -> {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof RepairAltarEntity altar) {
                ItemStackHandler inv = altar.getInventory();
                inv.setStackInSlot(0, inputSlots.getItem(COPPER_BLOCK_SLOT).copy());
            }
        });
    }

    private void playRepairSound(ServerLevel server, BlockPos pos) {
        server.playSound(
                null,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                ModSounds.REPAIR_ALTAR_USE.get(),
                net.minecraft.sounds.SoundSource.BLOCKS,
                0.8f,
                1.0f);
    }

    private void playBreakSound(ServerLevel server, BlockPos pos) {
        server.playSound(
                null,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                SoundEvents.ITEM_BREAK,
                net.minecraft.sounds.SoundSource.BLOCKS,
                1.0f,
                1.0f);
    }

    private static void spawnParticles(ServerLevel server, BlockPos pos) {
        int count = 12;
        double spread = 0.25d;
        double speed = 0.08d;

        double px = pos.getX() + 0.5;
        double py = pos.getY() + 0.95;
        double pz = pos.getZ() + 0.5;

        ItemParticleOption particle = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.COPPER_BLOCK));
        server.sendParticles(particle, px, py, pz, count, spread, spread, spread, speed);
    }

    @Override
    public void createResult() {
        ItemStack input = inputSlots.getItem(INPUT_SLOT);
        ItemStack material = inputSlots.getItem(ADDITIONAL_SLOT);
        ItemStack copper = inputSlots.getItem(COPPER_BLOCK_SLOT);

        repairCost.set(0);

        if (!isValidRepair(input, material, copper)) {
            resultSlots.setItem(0, ItemStack.EMPTY);
            return;
        }

        ItemStack output = input.copy();
        int originalDurability = getDurability(input);

        if (canRepairWithMaterial(output, material)) {
            repairWithMaterial(output, material, originalDurability);
        } else {
            sameItemRepair(output, material, originalDurability);
        }

        if (repairCost.get() <= 0) {
            resultSlots.setItem(0, ItemStack.EMPTY);
            return;
        }

        resultSlots.setItem(0, output);
        broadcastChanges();
    }

    private boolean isValidRepair(ItemStack input, ItemStack material, ItemStack copper) {
        return !input.isEmpty() && !material.isEmpty() && !copper.isEmpty();
    }

    private int getDurability(ItemStack stack) {
        return stack.getMaxDamage() - stack.getDamageValue();
    }

    private boolean canRepairWithMaterial(ItemStack output, ItemStack material) {
        return output.isDamageableItem() && output.getItem().isValidRepairItem(output, material);
    }

    private void repairWithMaterial(ItemStack output, ItemStack material, int originalDurability) {
        int repairPerUnit = Math.min(output.getDamageValue(), output.getMaxDamage() / 4);
        if (repairPerUnit <= 0)
            return;

        int used = 0;
        while (repairPerUnit > 0 && used < material.getCount()) {
            output.setDamageValue(output.getDamageValue() - repairPerUnit);
            used++;
            repairPerUnit = Math.min(output.getDamageValue(), output.getMaxDamage() / 4);
        }

        int newDurability = getDurability(output);
        if (newDurability <= originalDurability)
            return;

        repairCost.set(used);
    }

    private void sameItemRepair(ItemStack output, ItemStack material, int originalDurability) {
        if (!output.is(material.getItem()) || !output.isDamageableItem())
            return;

        int dur1 = getDurability(output);
        int dur2 = getDurability(material);
        int bonus = output.getMaxDamage() * 12 / 100;

        int newDamage = output.getMaxDamage() - (dur1 + dur2 + bonus);
        output.setDamageValue(Math.max(0, newDamage));

        int newDurability = getDurability(output);
        if (newDurability <= originalDurability)
            return;

        repairCost.set(1);
    }

    @Override
    public void slotsChanged(Container inventoryIn) {
        super.slotsChanged(inventoryIn);

        this.access.execute((level, pos) -> {
            if (level == null || level.isClientSide)
                return;

            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof RepairAltarEntity altar) {
                BlockState state = level.getBlockState(pos);
                level.sendBlockUpdated(pos, state, state, 3);
            }
        });
    }

    @Override
    public void removed(Player player) {

        if (!player.level().isClientSide) {
            this.access.execute((level, pos) -> {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof RepairAltarEntity altar) {

                    ItemStack copperInSlot = this.inputSlots.getItem(COPPER_BLOCK_SLOT);
                    if (copperInSlot == null)
                        copperInSlot = ItemStack.EMPTY;
                    altar.getInventory().setStackInSlot(0, copperInSlot.copy());
                    altar.setChanged();

                    this.inputSlots.setItem(COPPER_BLOCK_SLOT, ItemStack.EMPTY);
                }
            });
        }

        super.removed(player);
    }

    @Override
    protected boolean isValidBlock(BlockState pState) {
        return pState.is(ModBlocks.REPAIR_ALTAR.get());
    }
}
