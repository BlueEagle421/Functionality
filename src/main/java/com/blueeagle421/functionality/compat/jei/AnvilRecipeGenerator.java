package com.blueeagle421.functionality.compat.jei;

import com.blueeagle421.functionality.compat.CurioCompat;
import com.blueeagle421.functionality.compat.ModCompatManager;
import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.item.ModItems;

import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.RecordItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class AnvilRecipeGenerator {

    private static Map<Item, ItemStack> BASE_REPAIRABLE_ITEMS() {
        return Map.of(
                ModItems.HARPOON.get(), new ItemStack(Items.FLINT),
                ModItems.BIDENT.get(), new ItemStack(Items.PRISMARINE_SHARD),
                ModItems.GLOW_CROWN.get(), new ItemStack(Items.KELP));
    }

    private static Map<Item, ItemStack> CURIO_REPAIRABLE_ITEMS() {
        return Map.of(
                CurioCompat.FINS.get(), new ItemStack(Items.SCUTE),
                CurioCompat.OBSIDIAN_FINS.get(), new ItemStack(Items.OBSIDIAN),
                CurioCompat.INFERNO_GEAR.get(), new ItemStack(Items.OBSIDIAN));
    }

    public static List<IJeiAnvilRecipe> generate(IVanillaRecipeFactory factory) {
        List<IJeiAnvilRecipe> recipes = new ArrayList<>();

        generateItemRepairRecipes(factory, recipes);
        generateThrowableDiscRecipes(factory, recipes);

        return recipes;
    }

    private static void generateItemRepairRecipes(
            IVanillaRecipeFactory factory,
            List<IJeiAnvilRecipe> recipes) {

        for (Map.Entry<Item, ItemStack> entry : BASE_REPAIRABLE_ITEMS().entrySet()) {
            addStandardRepairRecipes(
                    factory,
                    recipes,
                    new ItemStack(entry.getKey()),
                    entry.getValue());
        }

        if (!ModCompatManager.curiosPresent)
            return;

        for (Map.Entry<Item, ItemStack> entry : CURIO_REPAIRABLE_ITEMS().entrySet()) {
            addStandardRepairRecipes(
                    factory,
                    recipes,
                    new ItemStack(entry.getKey()),
                    entry.getValue());
        }
    }

    private static void generateThrowableDiscRecipes(
            IVanillaRecipeFactory factory,
            List<IJeiAnvilRecipe> recipes) {

        if (!FunctionalityConfig.COMMON.features.throwableDiscs.enabled.get())
            return;

        ItemStack fragment = FunctionalityConfig.COMMON.features.throwableDiscs
                .getDiscRepairIngredient()
                .getItems()[0];

        BuiltInRegistries.ITEM.stream()
                .filter(item -> item instanceof RecordItem)
                .map(ItemStack::new)
                .forEach(disc -> addStandardRepairRecipes(factory, recipes, disc, fragment));
    }

    private static void addStandardRepairRecipes(
            IVanillaRecipeFactory factory,
            List<IJeiAnvilRecipe> recipes,
            ItemStack item,
            ItemStack repairMaterial) {

        if (!item.isDamageableItem())
            return;

        int maxDamage = item.getMaxDamage();

        ItemStack dmg100 = withDamage(item, maxDamage);
        ItemStack dmg75 = withDamage(item, maxDamage * 3 / 4);
        ItemStack dmg50 = withDamage(item, maxDamage / 2);

        recipes.add(factory.createAnvilRecipe(
                dmg100,
                List.of(repairMaterial),
                List.of(dmg75)));

        recipes.add(factory.createAnvilRecipe(
                dmg75,
                List.of(dmg75.copy()),
                List.of(dmg50)));
    }

    private static ItemStack withDamage(ItemStack stack, int damage) {
        ItemStack copy = stack.copy();
        copy.setDamageValue(damage);
        return copy;
    }
}
