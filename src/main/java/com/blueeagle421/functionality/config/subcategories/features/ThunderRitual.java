package com.blueeagle421.functionality.config.subcategories.features;

import net.minecraftforge.common.ForgeConfigSpec;

public class ThunderRitual {

    public final ForgeConfigSpec.BooleanValue enabled;
    public final ForgeConfigSpec.IntValue weatherDurationPerCopperBlock;
    public final ForgeConfigSpec.IntValue maxColumnHeight;
    public final ForgeConfigSpec.ConfigValue<String> ritualItem;

    public ThunderRitual(ForgeConfigSpec.Builder builder) {
        builder.push("thunder ritual");

        enabled = builder
                .comment(
                        "If true, the weather will turn to a thunderstorm if a configured item is dropped on a copper block surrounded by four torches.")
                .define("enabled", true);

        weatherDurationPerCopperBlock = builder
                .comment(
                        "Ticks duration for thunderstorm per copper block in the ritual column. Default: 60 seconds * 20 ticks per second = 600 ticks")
                .defineInRange("weatherDurationPerCopperBlock", 1200, 1, Integer.MAX_VALUE);

        maxColumnHeight = builder
                .comment(
                        "The max amount of copper blocks that can be stacked in a column for the ritual. Only the top block needs to be surrounded by torches.")
                .defineInRange("maxColumnHeight", 5, 1, Integer.MAX_VALUE);

        ritualItem = builder
                .comment(
                        "The item that needs to be dropped on the copper block to trigger the ritual in format (namespace:path).")
                .define("ritualItem", "minecraft:heart_of_the_sea");

        builder.pop();
    }
}
