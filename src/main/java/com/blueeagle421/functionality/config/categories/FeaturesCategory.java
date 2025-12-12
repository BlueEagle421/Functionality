package com.blueeagle421.functionality.config.categories;

import com.blueeagle421.functionality.config.subcategories.features.TreasureSacks;

import net.minecraftforge.common.ForgeConfigSpec;

public class FeaturesCategory {

    public final TreasureSacks treasureSacks;

    public FeaturesCategory(ForgeConfigSpec.Builder builder) {
        builder.push("features");

        builder.pop();

        treasureSacks = new TreasureSacks(builder);
    }
}
