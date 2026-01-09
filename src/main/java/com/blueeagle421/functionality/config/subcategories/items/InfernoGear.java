package com.blueeagle421.functionality.config.subcategories.items;

import net.minecraftforge.common.ForgeConfigSpec;

public class InfernoGear {

    public final ForgeConfigSpec.BooleanValue enabled;
    public final ForgeConfigSpec.IntValue lastsForTicks;

    public InfernoGear(ForgeConfigSpec.Builder builder) {
        builder.push("inferno_gear");

        enabled = builder.comment(
                "If false, the item won't be craftable and present in tabseffectively disabling it. Only works with Curio API present.")
                .define("enabled", true);

        lastsForTicks = builder
                .comment("The amount of tick time it allows to stay in lava for.")
                .defineInRange("lastsForTicks", 7200, 1, Integer.MAX_VALUE);

        builder.pop();
    }
}