package com.blueeagle421.functionality.config.subcategories.features;

import com.blueeagle421.functionality.config.data.DiscSpec;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

@SuppressWarnings("removal")
public class ThrowableDiscs {

    // impossible to configure from what I know unfortunately. Read by mixin
    public static final int DURABILITY = 48;

    public final ForgeConfigSpec.BooleanValue enabled;
    public final ForgeConfigSpec.DoubleValue defaultDamage;
    public final ForgeConfigSpec.DoubleValue defaultTravelDistance;
    public final ForgeConfigSpec.BooleanValue repairWithSelf;
    public final ForgeConfigSpec.ConfigValue<String> repairItem;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> perItemStats;

    private final Map<ResourceLocation, DiscSpec> perItemStatsMap = new HashMap<>();

    public ThrowableDiscs(ForgeConfigSpec.Builder builder) {
        builder.push("throwable music discs");

        enabled = builder
                .comment("If true, music discs will be throwable.")
                .define("enabled", true);

        defaultDamage = builder
                .comment("The default amount of damage the disc deals to entities. Overwritten by defaultStats list.")
                .defineInRange("defaultDamage", 5d, 0d, Double.MAX_VALUE);

        defaultTravelDistance = builder
                .comment("The default max travel distance (range) for thrown discs. Overwritten by defaultStats list.")
                .defineInRange("defaultTravelDistance", 10d, 0d, Double.MAX_VALUE);

        repairWithSelf = builder
                .comment("If true, music discs will be repairable by combining two discs.")
                .define("repairWithSelf", true);

        repairItem = builder
                .comment("Item or tag used to repair music discs.")
                .define("repairItem", "functionality:disc_shards");

        List<String> defaultStats = Arrays.asList(
                "minecraft:music_disc_pigstep;8;8.0",
                "minecraft:music_disc_5;8;8.0",
                "minecraft:music_disc_relic;8;8.0",
                "minecraft:music_disc_otherside;7;12.0",
                "minecraft:music_disc_11;7;12.0",
                "minecraft:music_disc_wait;7;12.0");

        perItemStats = builder
                .comment(
                        "Per-item stats for thrown discs. Format: 'namespace:item;damage;range'. Range optional (defaults to maxTravelDistance).")
                .defineList("perItemStats", defaultStats, o -> o instanceof String);

        builder.pop();
    }

    public Ingredient getDiscRepairIngredient() {
        String value = repairItem.get();
        if (value.startsWith("#")) {
            ResourceLocation tagId = new ResourceLocation(value.substring(1));
            TagKey<Item> tag = TagKey.create(Registries.ITEM, tagId);
            return Ingredient.of(tag);
        }

        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(value));
        return item != null ? Ingredient.of(item) : Ingredient.EMPTY;
    }

    public void reloadStats() {
        perItemStatsMap.clear();
        List<? extends String> list = perItemStats.get();
        if (list == null)
            return;

        for (String line : list) {
            DiscSpec.parse(line, defaultTravelDistance.get())
                    .ifPresent(spec -> perItemStatsMap.put(new ResourceLocation(spec.itemId),
                            spec));
        }
    }

    public float getDamage(ItemStack stack) {
        if (stack == null)
            return defaultDamage.get().floatValue();
        Item item = stack.getItem();
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
        if (id != null && perItemStatsMap.containsKey(id)) {
            return perItemStatsMap.get(id).damage;
        }
        return defaultDamage.get().floatValue();
    }

    public double getRange(ItemStack stack) {
        if (stack == null)
            return defaultTravelDistance.get();
        Item item = stack.getItem();
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);

        if (id != null && perItemStatsMap.containsKey(id))
            return perItemStatsMap.get(id).range;

        return defaultTravelDistance.get();
    }
}
