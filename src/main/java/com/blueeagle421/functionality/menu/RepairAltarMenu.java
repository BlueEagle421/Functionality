package com.blueeagle421.functionality.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

import com.blueeagle421.functionality.block.ModBlocks;
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

        clearInputSlot();
        consumeAdditionalMaterial(cost);
        consumeCopperBlock();
        repairCost.set(0);

        access.execute((level, pos) -> {
            if (!(level instanceof ServerLevel server))
                return;

            playSound(server, pos);
            spawnParticles(server, pos);
        });
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
        if (!copper.isEmpty()) {
            copper.shrink(1);
            if (copper.isEmpty()) {
                inputSlots.setItem(COPPER_BLOCK_SLOT, ItemStack.EMPTY);
            }
        }
    }

    private void playSound(ServerLevel server, BlockPos pos) {
        server.playSound(
                null,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                ModSounds.REPAIR_ALTAR_USE.get(),
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

        repairCost.set(0);

        if (input.isEmpty()
                || inputSlots.getItem(COPPER_BLOCK_SLOT).isEmpty()
                || material.isEmpty()) {
            resultSlots.setItem(0, ItemStack.EMPTY);
            return;
        }

        ItemStack output = input.copy();

        // Repair with material
        if (output.isDamageableItem()
                && output.getItem().isValidRepairItem(input, material)) {

            int repairPerUnit = Math.min(
                    output.getDamageValue(),
                    output.getMaxDamage() / 4);

            if (repairPerUnit <= 0) {
                resultSlots.setItem(0, ItemStack.EMPTY);
                return;
            }

            int used = 0;
            while (repairPerUnit > 0 && used < material.getCount()) {
                output.setDamageValue(output.getDamageValue() - repairPerUnit);
                used++;
                repairPerUnit = Math.min(
                        output.getDamageValue(),
                        output.getMaxDamage() / 4);
            }

            repairCost.set(used);
        } else {
            // Same-item repair
            if (!output.is(material.getItem()) || !output.isDamageableItem()) {
                resultSlots.setItem(0, ItemStack.EMPTY);
                return;
            }

            int dur1 = input.getMaxDamage() - input.getDamageValue();
            int dur2 = material.getMaxDamage() - material.getDamageValue();
            int bonus = output.getMaxDamage() * 12 / 100;

            int newDamage = output.getMaxDamage() - (dur1 + dur2 + bonus);
            output.setDamageValue(Math.max(0, newDamage));

            repairCost.set(1);
        }

        if (repairCost.get() <= 0) {
            resultSlots.setItem(0, ItemStack.EMPTY);
            return;
        }

        resultSlots.setItem(0, output);
        broadcastChanges();
    }

    @Override
    protected boolean isValidBlock(BlockState pState) {
        return pState.is(ModBlocks.REPAIR_ALTAR.get());
    }
}
