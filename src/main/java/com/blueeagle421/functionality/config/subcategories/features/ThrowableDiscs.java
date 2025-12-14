package com.blueeagle421.functionality.config.subcategories.features;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

public class ThrowableDiscs {
    public final ForgeConfigSpec.BooleanValue enabled;
    public final ForgeConfigSpec.IntValue defaultDamage;
    public final ForgeConfigSpec.DoubleValue maxTravelDistance;
    public final ForgeConfigSpec.BooleanValue repairWithSelf;
    public final ForgeConfigSpec.ConfigValue<String> repairItem;

    public ThrowableDiscs(ForgeConfigSpec.Builder builder) {
        builder.push("throwable music discs");

        enabled = builder
                .comment("If true, music discs will be throwable.")
                .define("enabled", true);

        defaultDamage = builder
                .comment("The default amount of damage the disc deals to entities.")
                .defineInRange("defaultDamage", 6, 0, Integer.MAX_VALUE);

        maxTravelDistance = builder
                .comment("The default amount of damage the disc deals to entities.")
                .defineInRange("maxTravelDistance", 15d, 0d, Double.MAX_VALUE);

        repairWithSelf = builder
                .comment("If true, music discs will be repairable by combining two discs.")
                .define("repairWithSelf", true);

        repairItem = builder
                .comment("Item or tag used to repair music discs.")
                .define("repairItem", "functionality:disc_shards");

        builder.pop();
    }

    @SuppressWarnings("removal")
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
}