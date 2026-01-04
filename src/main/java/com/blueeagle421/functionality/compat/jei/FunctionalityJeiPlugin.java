package com.blueeagle421.functionality.compat.jei;

import com.blueeagle421.functionality.FunctionalityMod;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;

@JeiPlugin
public class FunctionalityJeiPlugin implements IModPlugin {

    @SuppressWarnings("removal")
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(FunctionalityMod.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new InformationCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        IVanillaRecipeFactory factory = registration.getVanillaRecipeFactory();

        // Information recipes
        InformationRecipeGenerator.generateRecipes();
        registration.addRecipes(
                InformationCategory.INFORMATION_TYPE,
                new ArrayList<>(InformationRecipeGenerator.GENERATED.values()));

        // Anvil recipes
        registration.addRecipes(
                RecipeTypes.ANVIL,
                AnvilRecipeGenerator.generate(factory));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Blocks.ANVIL), RecipeTypes.ANVIL);
    }
}
