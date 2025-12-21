package com.blueeagle421.functionality.config.categories;

import com.blueeagle421.functionality.config.subcategories.features.BetterLichens;
import com.blueeagle421.functionality.config.subcategories.features.InfiniteWaterCauldron;
import com.blueeagle421.functionality.config.subcategories.features.ThrowableDiscs;
import com.blueeagle421.functionality.config.subcategories.features.ThunderRitual;
import com.blueeagle421.functionality.config.subcategories.features.TreasureSacks;

import net.minecraftforge.common.ForgeConfigSpec;

public class FeaturesCategory {

    public final TreasureSacks treasureSacks;
    public final InfiniteWaterCauldron infiniteWaterCauldron;
    public final ThrowableDiscs throwableDiscs;
    public final BetterLichens betterLichens;
    public final ThunderRitual thunderRitual;

    public FeaturesCategory(ForgeConfigSpec.Builder builder) {
        builder.push("features");

        treasureSacks = new TreasureSacks(builder);
        infiniteWaterCauldron = new InfiniteWaterCauldron(builder);
        throwableDiscs = new ThrowableDiscs(builder);
        betterLichens = new BetterLichens(builder);
        thunderRitual = new ThunderRitual(builder);

        builder.pop();
    }
}
