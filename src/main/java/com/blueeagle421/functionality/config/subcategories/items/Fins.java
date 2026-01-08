package com.blueeagle421.functionality.config.subcategories.items;

import net.minecraftforge.common.ForgeConfigSpec;

public class Fins {

    public final ForgeConfigSpec.BooleanValue enabled;
    public final ForgeConfigSpec.DoubleValue maxSpeedMultiplier;
    public final ForgeConfigSpec.IntValue lastsForTicks;

    public Fins(ForgeConfigSpec.Builder builder) {
        builder.push("fins");

        enabled = builder
                .comment(
                        "If false, the item won't be craftable and present in tabs effectively disabling it. Only works with Curio API present.")
                .define("enabled", true);

        maxSpeedMultiplier = builder
                .comment("The max speed multiplier that will be applied while swimming.")
                .defineInRange("maxSpeedMultiplier", 1.5, 1.0, 64.0);

        lastsForTicks = builder
                .comment("The amount of tick time it allows fast swimming for.")
                .defineInRange("lastsForTicks", 4800, 1, Integer.MAX_VALUE);

        builder.pop();
    }
}