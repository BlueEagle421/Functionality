package com.blueeagle421.functionality.config.subcategories.features;

import net.minecraftforge.common.ForgeConfigSpec;

public class HastePotionHarvesting {

    public final ForgeConfigSpec.BooleanValue enabled;
    public final ForgeConfigSpec.IntValue ticksDurationPerHarvest;
    public final ForgeConfigSpec.IntValue maxPotionDuration;
    public final ForgeConfigSpec.BooleanValue amplificationEnabled;

    public HastePotionHarvesting(ForgeConfigSpec.Builder builder) {
        builder.push("haste potion harvesting");

        enabled = builder
                .comment(
                        "If true, right clicking on bloom lichen with empty bottle will fill up the bottle with potion effect.")
                .define("enabled", true);

        ticksDurationPerHarvest = builder
                .comment(
                        "The amount of potion duration in ticks that will be added per lichen harvest (20 ticks per second).")
                .defineInRange("ticksDurationPerHarvest", 20 * 8, 1, Integer.MAX_VALUE);

        maxPotionDuration = builder
                .comment(
                        "The max duration of potion effect you can get from harvesting (20 ticks per second).")
                .defineInRange("maxPotionDuration", 1600, 1, Integer.MAX_VALUE);

        amplificationEnabled = builder
                .comment(
                        "If true, right clicking on overgrown lichen with will amplify haste potion effects.")
                .define("amplificationEnabled", true);

        builder.pop();
    }
}
