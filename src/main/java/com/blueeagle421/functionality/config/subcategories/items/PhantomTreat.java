package com.blueeagle421.functionality.config.subcategories.items;

import com.blueeagle421.functionality.item.ModItems;

import net.minecraftforge.common.ForgeConfigSpec;

public class PhantomTreat {

    public final ForgeConfigSpec.BooleanValue enabled;
    public final ForgeConfigSpec.IntValue maxUses;
    public final ForgeConfigSpec.DoubleValue speedIncreasePerUse;

    public PhantomTreat(ForgeConfigSpec.Builder builder) {
        builder.push(ModItems.PHANTOM_HERB.getId().getPath());

        enabled = builder
                .comment("If false, the item won't be craftable and present in tabs effectively disabling it.")
                .define("enabled", true);

        maxUses = builder
                .comment("The max amount of times the item can be applied on an entity.")
                .defineInRange("maxUses", 7, 1, Integer.MAX_VALUE);

        speedIncreasePerUse = builder
                .comment("Movement speed multiplier applied per use (0.05 = +5% per use).")
                .defineInRange("speedIncreasePerUse", 0.05, 0.01, 4f);

        builder.pop();
    }
}