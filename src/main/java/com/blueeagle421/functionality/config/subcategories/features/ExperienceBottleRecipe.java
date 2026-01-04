package com.blueeagle421.functionality.config.subcategories.features;

import net.minecraftforge.common.ForgeConfigSpec;

public class ExperienceBottleRecipe {

    public final ForgeConfigSpec.BooleanValue enabled;

    public ExperienceBottleRecipe(ForgeConfigSpec.Builder builder) {
        builder.push("experience bottle recipe");

        enabled = builder
                .comment(
                        "If false, the experience bottle recipe will not be available.")
                .define("enabled", true);

        builder.pop();
    }
}
