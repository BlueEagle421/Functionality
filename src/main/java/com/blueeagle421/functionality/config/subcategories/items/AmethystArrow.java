package com.blueeagle421.functionality.config.subcategories.items;

import com.blueeagle421.functionality.item.ModItems;

import net.minecraftforge.common.ForgeConfigSpec;

public class AmethystArrow {

    public final ForgeConfigSpec.BooleanValue enabled;
    public final ForgeConfigSpec.DoubleValue damageMultiplier;

    public AmethystArrow(ForgeConfigSpec.Builder builder) {
        builder.push(ModItems.AMETHYST_ARROW.getId().getPath());

        enabled = builder
                .comment("If false, the item won't be craftable and present in tabs effectively disabling it.")
                .define("enabled", true);

        damageMultiplier = builder
                .comment("The damage multiplier that will be applied when attacking with the arrow.")
                .defineInRange("damageMultiplier", 1.2, 1.0, 64.0);

        builder.pop();
    }
}