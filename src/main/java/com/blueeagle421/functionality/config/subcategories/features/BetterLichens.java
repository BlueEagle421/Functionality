package com.blueeagle421.functionality.config.subcategories.features;

import net.minecraftforge.common.ForgeConfigSpec;

public class BetterLichens {

    public final ForgeConfigSpec.BooleanValue enabled;
    public final ForgeConfigSpec.DoubleValue lichenFeatureGenChance;
    public final ForgeConfigSpec.DoubleValue surroundingLichenGenChance;
    public final ForgeConfigSpec.DoubleValue dryLichenGenChance;

    public BetterLichens(ForgeConfigSpec.Builder builder) {
        builder.push("better lichens");

        enabled = builder
                .comment("If true, the vanilla glow lichen will be replaced by a nice variety of 'functional' lichens.")
                .define("enabled", true);

        lichenFeatureGenChance = builder
                .comment(
                        "The chance of letting lichens run their placing logic. Setting to 1 will make them as common as vanilla glow lichens.")
                .defineInRange("lichenFeatureGenChance", 0.35, 0.0, 1.0);

        surroundingLichenGenChance = builder
                .comment("The chance of lichen or dry lichen spawn around glow lichen.")
                .defineInRange("surroundingLichenGenChance", 0.55, 0.0, 1.0);

        dryLichenGenChance = builder
                .comment("The chance of generating a dry lichen instead of a normal one.")
                .defineInRange("dryLichenGenChance", 0.35, 0.0, 1.0);

        builder.pop();
    }
}
