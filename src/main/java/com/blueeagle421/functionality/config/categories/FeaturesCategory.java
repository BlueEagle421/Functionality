package com.blueeagle421.functionality.config.categories;

import com.blueeagle421.functionality.config.subcategories.features.*;

import net.minecraftforge.common.ForgeConfigSpec;

public class FeaturesCategory {

    public final TreasureSacks treasureSacks;
    public final InfernalSacks infernalSacks;
    public final GlowSquidMilking glowSquidMilking;
    public final GlowSquidEffectCloud glowSquidEffectCloud;
    public final InfiniteWaterCauldron infiniteWaterCauldron;
    public final ThrowableDiscs throwableDiscs;
    public final BetterLichens betterLichens;
    public final ThunderRitual thunderRitual;
    public final HastePotionHarvesting hastePotionHarvesting;
    public final SpongeRecipe spongeRecipe;

    public FeaturesCategory(ForgeConfigSpec.Builder builder) {
        builder.push("features");

        treasureSacks = new TreasureSacks(builder);
        infernalSacks = new InfernalSacks(builder);
        glowSquidMilking = new GlowSquidMilking(builder);
        glowSquidEffectCloud = new GlowSquidEffectCloud(builder);
        infiniteWaterCauldron = new InfiniteWaterCauldron(builder);
        throwableDiscs = new ThrowableDiscs(builder);
        betterLichens = new BetterLichens(builder);
        hastePotionHarvesting = new HastePotionHarvesting(builder);
        thunderRitual = new ThunderRitual(builder);
        spongeRecipe = new SpongeRecipe(builder);

        builder.pop();
    }
}