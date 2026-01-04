package com.blueeagle421.functionality.config.subcategories.features;

import net.minecraftforge.common.ForgeConfigSpec;

public class VexEssenceDrops {

    public final ForgeConfigSpec.BooleanValue limitEnabled;
    public final ForgeConfigSpec.IntValue maxPerOwner;

    public VexEssenceDrops(ForgeConfigSpec.Builder builder) {
        builder.push("vex essence drops");

        limitEnabled = builder
                .comment("If true, the vex essence drops are limited per owner entity to avoid farming them.")
                .define("limitEnabled", true);

        maxPerOwner = builder
                .comment("Max amount of vex essence a single owner can store.")
                .defineInRange("maxPerOwner", 6, 1, Integer.MAX_VALUE);

        builder.pop();
    }
}
