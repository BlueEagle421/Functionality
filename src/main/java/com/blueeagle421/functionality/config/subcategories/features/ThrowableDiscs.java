package com.blueeagle421.functionality.config.subcategories.features;

import net.minecraftforge.common.ForgeConfigSpec;

public class ThrowableDiscs {
    public final ForgeConfigSpec.BooleanValue enabled;
    public final ForgeConfigSpec.IntValue defaultDamage;
    public final ForgeConfigSpec.DoubleValue maxTravelDistance;
    public final ForgeConfigSpec.BooleanValue repairWithSelf;

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

        builder.pop();
    }
}