package com.blueeagle421.functionality.config.subcategories.features;

import net.minecraft.util.RandomSource;
import net.minecraftforge.common.ForgeConfigSpec;

public class BetterLichens {

    public final ForgeConfigSpec.BooleanValue enabled;
    public final ForgeConfigSpec.DoubleValue featureGenChance;
    public final ForgeConfigSpec.DoubleValue overrideChance;
    public final ForgeConfigSpec.DoubleValue surroundingChance;
    public final ForgeConfigSpec.DoubleValue overgrownChance;
    public final ForgeConfigSpec.IntValue overgrownMaxHeight;
    public final ForgeConfigSpec.DoubleValue dryChance;

    public BetterLichens(ForgeConfigSpec.Builder builder) {
        builder.push("better_lichens");

        enabled = builder
                .comment("Enable functional lichens and replace vanilla glow lichen.")
                .define("enabled", true);

        featureGenChance = builder
                .comment("Chance override for vanilla glow lichen feature to attempt placement.")
                .defineInRange("featureGenChance", 0.25, 0.0, 1.0);

        overrideChance = builder
                .comment("Chance to replace vanilla glow lichen with modded variants.")
                .defineInRange("overrideChance", 1.0, 0.0, 1.0);

        surroundingChance = builder
                .comment("Chance to generate lichens around bloom lichens.")
                .defineInRange("surroundingChance", 0.55, 0.0, 1.0);

        overgrownChance = builder
                .comment("Chance to generate overgrown lichen instead of bloom.")
                .defineInRange("overgrownChance", 0.06, 0.0, 1.0);

        overgrownMaxHeight = builder
                .comment("Max Y level for overgrown lichens.")
                .defineInRange("overgrownMaxHeight", 0, -64, 320);

        dryChance = builder
                .comment("Chance to generate dry lichen instead of normal.")
                .defineInRange("dryChance", 0.35, 0.0, 1.0);

        builder.pop();
    }

    public boolean isEnabled() {
        return enabled.get();
    }

    public boolean rollFeature(RandomSource random) {
        return roll(random, featureGenChance.get());
    }

    public boolean rollOverride(RandomSource random) {
        return roll(random, overrideChance.get());
    }

    public boolean rollOvergrown(RandomSource random) {
        return roll(random, overgrownChance.get());
    }

    public boolean rollDry(RandomSource random) {
        return roll(random, dryChance.get());
    }

    public boolean rollSurrounding(RandomSource random) {
        return roll(random, surroundingChance.get());
    }

    private boolean roll(RandomSource random, Double chance) {
        return random.nextFloat() < chance;
    }
}
