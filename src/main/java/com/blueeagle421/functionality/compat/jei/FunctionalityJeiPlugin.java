package com.blueeagle421.functionality.compat.jei;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.item.ModItems;
import com.blueeagle421.functionality.recipe.InformationRecipe;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@JeiPlugin
public class FunctionalityJeiPlugin implements IModPlugin {

    private static Map<Item, ItemStack> REPAIRABLE_ITEMS() {
        return Map.of(
                ModItems.HARPOON.get(), new ItemStack(Items.FLINT),
                ModItems.GLOW_CROWN.get(), new ItemStack(Items.KELP),
                ModItems.FINS.get(), new ItemStack(Items.SCUTE),
                ModItems.OBSIDIAN_FINS.get(), new ItemStack(Items.OBSIDIAN),
                ModItems.INFERNO_GEAR.get(), new ItemStack(Items.OBSIDIAN));
    }

    @SuppressWarnings("removal")
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(FunctionalityMod.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new InformationCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        IVanillaRecipeFactory factory = registration.getVanillaRecipeFactory();
        List<IJeiAnvilRecipe> recipes = new ArrayList<>();

        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<InformationRecipe> infoRecipes = recipeManager.getAllRecipesFor(InformationRecipe.Type.INSTANCE);
        registration.addRecipes(InformationCategory.INFORMATION_TYPE, infoRecipes);

        for (Map.Entry<Item, ItemStack> entry : REPAIRABLE_ITEMS().entrySet()) {
            ItemStack itemStack = new ItemStack(entry.getKey());
            ItemStack repairMaterial = entry.getValue();

            if (!itemStack.isDamageableItem())
                continue;

            ItemStack dmg100 = itemStack.copy();
            dmg100.setDamageValue(dmg100.getMaxDamage());

            ItemStack dmg75 = itemStack.copy();
            dmg75.setDamageValue(itemStack.getMaxDamage() * 3 / 4);

            ItemStack dmg50 = itemStack.copy();
            dmg50.setDamageValue(itemStack.getMaxDamage() / 2);

            // repair material
            recipes.add(factory.createAnvilRecipe(
                    dmg100,
                    Collections.singletonList(repairMaterial),
                    Collections.singletonList(dmg75)));

            // repair itself
            recipes.add(factory.createAnvilRecipe(
                    dmg75,
                    Collections.singletonList(dmg75.copy()),
                    Collections.singletonList(dmg50)));
        }

        registration.addRecipes(RecipeTypes.ANVIL, recipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Blocks.ANVIL), RecipeTypes.ANVIL);
    }
}
