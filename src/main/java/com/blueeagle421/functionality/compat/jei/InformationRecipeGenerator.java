package com.blueeagle421.functionality.compat.jei;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.config.categories.FeaturesCategory;
import com.blueeagle421.functionality.item.ModItems;
import com.blueeagle421.functionality.recipe.InformationRecipe;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.network.chat.Component;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings({ "removal", "deprecation" })
public class InformationRecipeGenerator {

    public static final Map<ResourceLocation, InformationRecipe> GENERATED = new HashMap<>();

    public static void generateRecipes() {
        GENERATED.clear();

        if (config().treasureSacks.limitEnabled.get())
            generateTreasureSacksRecipe();

        if (config().infiniteWaterCauldron.enabled.get())
            generateInfiniteCauldronRecipe();

        if (config().throwableDiscs.enabled.get())
            generateThrowableDiscsRecipe();
    }

    private static void generateInfiniteCauldronRecipe() {
        Set<ResourceLocation> blockRLs = config().infiniteWaterCauldron.getBlocksAsResourceLocations();

        NonNullList<Ingredient> inputs = NonNullList.withSize(1 + blockRLs.size(), Ingredient.EMPTY);

        inputs.set(0, Ingredient.of(Items.CAULDRON));

        int index = 1;
        for (ResourceLocation blockRL : blockRLs) {
            ItemStack blockItem = ItemStack.EMPTY;
            if (ForgeRegistries.ITEMS.containsKey(blockRL))
                blockItem = new ItemStack(ForgeRegistries.ITEMS.getValue(blockRL));

            inputs.set(index++, Ingredient.of(blockItem));
        }

        ResourceLocation recipeId = new ResourceLocation(FunctionalityMod.MOD_ID,
                "information/generated/infinite_water_cauldron");
        Component desc = Component.translatable("information.functionality.infinite_water_cauldron");

        InformationRecipe recipe = new InformationRecipe(inputs, desc, recipeId);
        GENERATED.put(recipeId, recipe);
    }

    private static void generateTreasureSacksRecipe() {
        Set<ResourceLocation> entityRLs = config().treasureSacks.getEntitiesAsResourceLocations();

        NonNullList<Ingredient> inputs = NonNullList.withSize(1 + entityRLs.size(), Ingredient.EMPTY);

        inputs.set(0, Ingredient.of(ModItems.TREASURE_SACK.get()));

        int index = 1;
        for (ResourceLocation entityRL : entityRLs) {
            ItemStack egg = ItemStack.EMPTY;

            if (ForgeRegistries.ITEMS.containsKey(entityRL)) {
                egg = new ItemStack(ForgeRegistries.ITEMS.getValue(entityRL));
            } else {
                egg = ForgeRegistries.ITEMS.getValues().stream()
                        .filter(i -> i.getDescriptionId() != null
                                && i.getDescriptionId().toString().endsWith("_spawn_egg")
                                && i.getDescriptionId().contains(entityRL.getPath()))
                        .findFirst()
                        .map(i -> new ItemStack(i))
                        .orElse(ItemStack.EMPTY);
            }

            inputs.set(index++, Ingredient.of(egg));
        }

        ResourceLocation recipeId = new ResourceLocation(FunctionalityMod.MOD_ID,
                "information/generated/treasure_sacks");
        Component desc = Component.translatable("information.functionality.treasure_sacks");

        InformationRecipe recipe = new InformationRecipe(inputs, desc, recipeId);
        GENERATED.put(recipeId, recipe);
    }

    private static void generateThrowableDiscsRecipe() {

        var discs = ForgeRegistries.ITEMS.getValues().stream()
                .filter(item -> item.builtInRegistryHolder()
                        .is(net.minecraft.tags.ItemTags.MUSIC_DISCS))
                .toList();

        NonNullList<Ingredient> inputs = NonNullList.withSize(discs.size(), Ingredient.EMPTY);

        int index = 0;
        for (var disc : discs) {
            inputs.set(index++, Ingredient.of(new ItemStack(disc)));
        }

        ResourceLocation recipeId = new ResourceLocation(
                FunctionalityMod.MOD_ID,
                "information/generated/throwable_discs");

        Component desc = Component.translatable(
                "information.functionality.throwable_discs");

        InformationRecipe recipe = new InformationRecipe(inputs, desc, recipeId);
        GENERATED.put(recipeId, recipe);
    }

    private static FeaturesCategory config() {
        return FunctionalityConfig.COMMON.features;
    }
}
