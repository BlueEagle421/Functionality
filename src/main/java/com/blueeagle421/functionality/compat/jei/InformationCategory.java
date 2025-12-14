package com.blueeagle421.functionality.compat.jei;

import java.util.List;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.item.ModItems;
import com.blueeagle421.functionality.recipe.InformationRecipe;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("removal")
public class InformationCategory implements IRecipeCategory<InformationRecipe> {

    public static final ResourceLocation UID = new ResourceLocation(FunctionalityMod.MOD_ID, "information");
    public static final RecipeType<InformationRecipe> INFORMATION_TYPE = new RecipeType<>(UID, InformationRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    private final int BG_WIDTH = 160;

    private final int SLOT_SIZE = 16;
    private final int SLOT_PADDING = 2;

    private final int DESCRIPTION_HEIGHT = 32;
    private final int TEXT_PADDING = 2;

    private final int bgWidth;
    private final int bgHeight;

    public InformationCategory(IGuiHelper helper) {
        this.bgWidth = BG_WIDTH;
        this.bgHeight = SLOT_SIZE + DESCRIPTION_HEIGHT;

        this.background = helper.createBlankDrawable(bgWidth, bgHeight);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.INFORMATION.get()));
    }

    @Override
    public RecipeType<InformationRecipe> getRecipeType() {
        return INFORMATION_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("category.functionality.information");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, InformationRecipe recipe, IFocusGroup focuses) {

        int maxSlots = 9;
        int slotCount = Math.min(recipe.getOutputs().size(), maxSlots);

        int rowWidth = slotCount * SLOT_SIZE +
                (slotCount - 1) * SLOT_PADDING;

        int startX = (bgWidth - rowWidth) / 2;
        int y = 0;

        for (int index = 0; index < slotCount; index++) {
            ItemStack stack = recipe.getOutputs().get(index);

            int x = startX + index * (SLOT_SIZE + SLOT_PADDING);

            builder.addSlot(RecipeIngredientRole.OUTPUT, x, y)
                    .addItemStack(stack)
                    .setSlotName("thing_" + index);
        }
    }

    @Override
    public void draw(InformationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX,
            double mouseY) {
        Component desc = recipe.getDescription();
        if (desc != null && !desc.equals(Component.empty())) {
            Font font = Minecraft.getInstance().font;

            int maxTextWidth = bgWidth - 2 * TEXT_PADDING;

            List<net.minecraft.util.FormattedCharSequence> lines = font.split(desc, maxTextWidth);

            int lineCount = Math.min(lines.size(), 4);

            int textY = SLOT_SIZE + 2;

            int textColor = 0x1F1F1F;

            for (int i = 0; i < lineCount; i++) {
                net.minecraft.util.FormattedCharSequence line = lines.get(i);
                int textWidth = font.width(line);
                int textX = (bgWidth - textWidth) / 2;
                graphics.drawString(font, line, textX, textY, textColor, false);
                textY += font.lineHeight;
            }
        }
    }
}
