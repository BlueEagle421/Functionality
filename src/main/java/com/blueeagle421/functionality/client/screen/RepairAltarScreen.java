package com.blueeagle421.functionality.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.menu.RepairAltarMenu;

@OnlyIn(Dist.CLIENT)
public class RepairAltarScreen extends ItemCombinerScreen<RepairAltarMenu> {

    @SuppressWarnings("removal")
    private static final ResourceLocation ANVIL_LOCATION = new ResourceLocation(FunctionalityMod.MOD_ID,
            "textures/gui/container/repair_altar.png");

    public RepairAltarScreen(RepairAltarMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, ANVIL_LOCATION);
        this.titleLabelX = 60;
    }

    @Override
    protected void subInit() {
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(graphics, partialTick, mouseX, mouseY);

        // Arrow between input slots (same as anvil)
        graphics.blit(
                ANVIL_LOCATION,
                this.leftPos + 59,
                this.topPos + 20,
                0,
                this.imageHeight + (this.menu.getSlot(0).hasItem() ? 0 : 16),
                110,
                16);
    }

    @Override
    protected void renderErrorIcon(GuiGraphics graphics, int x, int y) {
        if ((this.menu.getSlot(0).hasItem() || this.menu.getSlot(1).hasItem())
                && !this.menu.getSlot(this.menu.getResultSlot()).hasItem()) {

            graphics.blit(
                    ANVIL_LOCATION,
                    x + 99 + 8,
                    y + 45,
                    this.imageWidth,
                    0,
                    28,
                    21);
        }
    }
}
