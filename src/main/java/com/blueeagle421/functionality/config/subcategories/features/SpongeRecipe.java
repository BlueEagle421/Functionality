package com.blueeagle421.functionality.config.subcategories.features;

import net.minecraftforge.common.ForgeConfigSpec;

public class SpongeRecipe {

    public final ForgeConfigSpec.BooleanValue enabled;

    public SpongeRecipe(ForgeConfigSpec.Builder builder) {
        builder.push("sponge recipe");

        enabled = builder
                .comment(
                        "If false, the sponge recipe will not be available.")
                .define("enabled", true);

        builder.pop();
    }
}
