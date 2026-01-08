package com.blueeagle421.functionality.config.subcategories.items;

import net.minecraftforge.common.ForgeConfigSpec;

public class ObsidianFins {

    public final ForgeConfigSpec.BooleanValue enabled;

    public ObsidianFins(ForgeConfigSpec.Builder builder) {
        builder.push("obsidian_fins");

        enabled = builder
                .comment(
                        "If false, the item won't be craftable and present in tabs effectively disabling it. Only works with Curio API present.")
                .define("enabled", true);

        builder.pop();
    }
}