package com.blueeagle421.functionality.config.subcategories.features;

import net.minecraftforge.common.ForgeConfigSpec;

public class GlowSquidEffectCloud {
    public final ForgeConfigSpec.BooleanValue enabled;
    public final ForgeConfigSpec.IntValue effectDuration;

    public GlowSquidEffectCloud(ForgeConfigSpec.Builder builder) {
        builder.push("glow squid effect cloud");

        enabled = builder
                .comment(
                        "If true, attacking the glow squid will release a night vision effect cloud.")
                .define("enabled", true);

        effectDuration = builder
                .comment(
                        "The duration of night vision from effect cloud (20 ticks per second).")
                .defineInRange("effectDuration", 90 * 20, 0, Integer.MAX_VALUE);

        builder.pop();
    }
}
