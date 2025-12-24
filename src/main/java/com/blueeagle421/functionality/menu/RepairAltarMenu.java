package com.blueeagle421.functionality.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

import com.blueeagle421.functionality.block.ModBlocks;

public class RepairAltarMenu extends ItemCombinerMenu {
    public static final int INPUT_SLOT = 0;
    public static final int ADDITIONAL_SLOT = 1;
    public static final int COPPER_BLOCK_SLOT = 2;
    public static final int RESULT_SLOT = 3;

    public int repairItemCountCost;

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
    }

    @Override
    protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create()
                .withSlot(INPUT_SLOT, 27 - 10, 47, (stack) -> true)
                .withSlot(ADDITIONAL_SLOT, 76 - 10, 47, (stack) -> true)
                .withSlot(COPPER_BLOCK_SLOT, 76 + 8, 47, (stack) -> stack.is(Items.COPPER_BLOCK))
                .withResultSlot(RESULT_SLOT, 134 + 8, 47)
                .build();
    }

    @Override
    protected boolean mayPickup(Player player, boolean hasStack) {
        return player.getAbilities().instabuild || hasStack;
    }

    @Override
    protected void onTake(Player player, ItemStack resultStack) {
        this.inputSlots.setItem(0, ItemStack.EMPTY);

        if (this.repairItemCountCost > 0) {
            ItemStack mat = this.inputSlots.getItem(ADDITIONAL_SLOT);
            if (!mat.isEmpty() && mat.getCount() > this.repairItemCountCost) {
                mat.shrink(this.repairItemCountCost);
                this.inputSlots.setItem(1, mat);
            } else {
                this.inputSlots.setItem(1, ItemStack.EMPTY);
            }
        } else {
            this.inputSlots.setItem(1, ItemStack.EMPTY);
        }

        this.access.execute((world, pos) -> world.levelEvent(1030, pos, 0));
    }

    @Override
    public void createResult() {
        ItemStack input = this.inputSlots.getItem(INPUT_SLOT);
        this.repairItemCountCost = 0;

        if (input.isEmpty()) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            return;
        }

        if (this.inputSlots.getItem(COPPER_BLOCK_SLOT).isEmpty()) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            return;
        }

        ItemStack output = input.copy();
        ItemStack other = this.inputSlots.getItem(ADDITIONAL_SLOT);

        if (!other.isEmpty()) {
            boolean usedRepairMaterial = false;

            if (output.isDamageableItem() && output.getItem().isValidRepairItem(input, other)) {
                int repairPerUnit = Math.min(output.getDamageValue(), output.getMaxDamage() / 4);
                if (repairPerUnit <= 0) {
                    this.resultSlots.setItem(0, ItemStack.EMPTY);
                    return;
                }

                int used = 0;
                while (repairPerUnit > 0 && used < other.getCount()) {
                    int newDamage = output.getDamageValue() - repairPerUnit;
                    output.setDamageValue(newDamage);
                    ++used;
                    repairPerUnit = Math.min(output.getDamageValue(), output.getMaxDamage() / 4);
                }

                this.repairItemCountCost = used;
                usedRepairMaterial = true;
            }

            if (!usedRepairMaterial) {
                if (!output.is(other.getItem()) || !output.isDamageableItem()) {
                    this.resultSlots.setItem(0, ItemStack.EMPTY);
                    return;
                }

                int remainingDur1 = input.getMaxDamage() - input.getDamageValue();
                int remainingDur2 = other.getMaxDamage() - other.getDamageValue();
                int bonus = other.getMaxDamage() * 12 / 100; // 12% bonus like vanilla
                int combinedRemaining = remainingDur1 + remainingDur2 + bonus;
                int newDamageValue = input.getMaxDamage() - combinedRemaining;
                if (newDamageValue < 0)
                    newDamageValue = 0;

                if (newDamageValue < input.getDamageValue()) {
                    output.setDamageValue(newDamageValue);
                }
            }
        }

        this.resultSlots.setItem(0, output);
        this.broadcastChanges();
    }

    @Override
    protected boolean isValidBlock(BlockState pState) {
        return pState.is(ModBlocks.REPAIR_ALTAR.get());
    }
}
