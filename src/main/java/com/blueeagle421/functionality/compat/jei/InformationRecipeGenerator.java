package com.blueeagle421.functionality.compat.jei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.config.categories.FeaturesCategory;
import com.blueeagle421.functionality.item.ModItems;
import com.blueeagle421.functionality.recipe.InformationRecipe;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.network.chat.Component;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings({ "removal", "deprecation" })
public class InformationRecipeGenerator {

    public static final Map<ResourceLocation, InformationRecipe> GENERATED = new HashMap<>();

    public static void generateRecipes() {
        GENERATED.clear();

        if (config().hastePotionHarvesting.enabled.get())
            generateHasteHarvestingRecipe();

        if (config().treasureSacks.limitEnabled.get())
            generateTreasureSacksRecipe();

        if (config().infernalSacks.limitEnabled.get())
            generateInfernalSacksRecipe();

        if (config().infiniteWaterCauldron.enabled.get())
            generateInfiniteCauldronRecipe();

        if (config().throwableDiscs.enabled.get())
            generateThrowableDiscsRecipe();

        if (config().thunderRitual.enabled.get())
            generateThunderRitualRecipe();
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
            ItemStack egg = getSpawnEggForEntity(entityRL);

            if (!egg.isEmpty())
                inputs.set(index++, Ingredient.of(egg));
        }

        ResourceLocation recipeId = new ResourceLocation(FunctionalityMod.MOD_ID,
                "information/generated/treasure_sacks");
        Component desc = Component.translatable("information.functionality.treasure_sacks");

        InformationRecipe recipe = new InformationRecipe(inputs, desc, recipeId);
        GENERATED.put(recipeId, recipe);
    }

    private static void generateInfernalSacksRecipe() {
        Set<ResourceLocation> entityRLs = config().infernalSacks.getEntitiesAsResourceLocations();

        NonNullList<Ingredient> inputs = NonNullList.withSize(1 + entityRLs.size(), Ingredient.EMPTY);

        inputs.set(0, Ingredient.of(ModItems.INFERNAL_SACK.get()));

        int index = 1;
        for (ResourceLocation entityRL : entityRLs) {
            ItemStack egg = getSpawnEggForEntity(entityRL);

            if (!egg.isEmpty())
                inputs.set(index++, Ingredient.of(egg));
        }

        ResourceLocation recipeId = new ResourceLocation(FunctionalityMod.MOD_ID,
                "information/generated/infernal_sacks");
        Component desc = Component.translatable("information.functionality.infernal_sacks");

        InformationRecipe recipe = new InformationRecipe(inputs, desc, recipeId);
        GENERATED.put(recipeId, recipe);
    }

    private static ItemStack getSpawnEggForEntity(ResourceLocation entityRL) {
        EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(entityRL);

        if (type == null)
            return ItemStack.EMPTY;

        SpawnEggItem egg = net.minecraftforge.common.ForgeSpawnEggItem.fromEntityType(type);

        if (egg == null)
            return ItemStack.EMPTY;

        return new ItemStack(egg);
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

    private static void generateThunderRitualRecipe() {
        String ritualItemStr = config().thunderRitual.ritualItem.get();
        ResourceLocation ritualRL = null;
        try {
            ritualRL = new ResourceLocation(ritualItemStr);
        } catch (Exception e) {
            ritualRL = null;
        }

        ItemStack ritualStack = ItemStack.EMPTY;
        if (ritualRL != null && ForgeRegistries.ITEMS.containsKey(ritualRL)) {
            ritualStack = new ItemStack(ForgeRegistries.ITEMS.getValue(ritualRL));
        }

        NonNullList<Ingredient> inputs = NonNullList.withSize(3, Ingredient.EMPTY);
        inputs.set(0, Ingredient.of(new ItemStack(Items.COPPER_BLOCK)));
        inputs.set(1, Ingredient.of(ritualStack));
        inputs.set(2, Ingredient.of(new ItemStack(Items.TORCH)));

        ResourceLocation recipeId = new ResourceLocation(FunctionalityMod.MOD_ID,
                "information/generated/thunder_ritual");
        Component desc = Component.translatable("information.functionality.thunder_ritual");

        InformationRecipe recipe = new InformationRecipe(inputs, desc, recipeId);
        GENERATED.put(recipeId, recipe);
    }

    private static void generateHasteHarvestingRecipe() {
        NonNullList<Ingredient> inputs = NonNullList.withSize(3, Ingredient.EMPTY);
        inputs.set(0, Ingredient.of(new ItemStack(ModItems.BLOOM_LICHEN.get())));
        inputs.set(1, Ingredient.of(new ItemStack(Items.GLASS_BOTTLE)));

        ItemStack hastePotionStack = new ItemStack(Items.POTION);
        PotionUtils.setPotion(hastePotionStack, Potions.WATER);
        List<MobEffectInstance> effects = new ArrayList<>();
        effects.add(new MobEffectInstance(MobEffects.DIG_SPEED,
                config().hastePotionHarvesting.ticksDurationPerHarvest.get()));
        CompoundTag tag = hastePotionStack.getOrCreateTag();
        ListTag listTag = new ListTag();
        for (MobEffectInstance inst : effects) {
            listTag.add(inst.save(new CompoundTag()));
        }
        tag.put("CustomPotionEffects", listTag);
        hastePotionStack.setHoverName(Component.translatable("item.functionality.potion.effect.haste"));

        inputs.set(2, Ingredient.of(hastePotionStack));

        ResourceLocation recipeId = new ResourceLocation(FunctionalityMod.MOD_ID,
                "information/generated/haste_harvesting");
        Component desc = Component.translatable("information.functionality.haste_harvesting");

        InformationRecipe recipe = new InformationRecipe(inputs, desc, recipeId);
        GENERATED.put(recipeId, recipe);
    }

    private static FeaturesCategory config() {
        return FunctionalityConfig.COMMON.features;
    }
}
