package com.blueeagle421.functionality.config.subcategories.features;

import net.minecraftforge.common.ForgeConfigSpec;

public class GlowSquidMilking {
    public final ForgeConfigSpec.BooleanValue enabled;

    public GlowSquidMilking(ForgeConfigSpec.Builder builder) {
        builder.push("glow squid milking");

        enabled = builder
                .comment(
                        "If true, using an empty glass bottle on a glow squid will make a night vision potion and hurt the squid.")
                .define("enabled", true);

        builder.pop();
    }
}
