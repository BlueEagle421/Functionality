package com.blueeagle421.functionality.config.subcategories.items;

import net.minecraftforge.common.ForgeConfigSpec;

public class GlowCrown {

    public final ForgeConfigSpec.BooleanValue enabled;

    public GlowCrown(ForgeConfigSpec.Builder builder) {
        builder.push("glow_crown");

        enabled = builder
                .comment(
                        "If false, the item won't be craftable and present in tabs effectively disabling it. Only works with Curio API present.")
                .define("enabled", true);

        builder.pop();
    }
}