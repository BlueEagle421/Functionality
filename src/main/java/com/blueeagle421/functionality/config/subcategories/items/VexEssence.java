package com.blueeagle421.functionality.config.subcategories.items;

import com.blueeagle421.functionality.item.ModItems;

import net.minecraftforge.common.ForgeConfigSpec;

public class VexEssence {

    public final ForgeConfigSpec.BooleanValue enabled;
    public final ForgeConfigSpec.DoubleValue dropXPChance;
    public final ForgeConfigSpec.IntValue baseXPValue;
    public final ForgeConfigSpec.IntValue extraXPValue;

    public VexEssence(ForgeConfigSpec.Builder builder) {
        builder.push(ModItems.VEX_ESSENCE.getId().getPath());

        enabled = builder
                .comment("If false, the item won't be acquirable and present in tabs effectively disabling it.")
                .define("enabled", true);

        dropXPChance = builder
                .comment("Chance for dropping experience after absorbing the essence (1 = 100% chance).")
                .defineInRange("dropXPChance", 1D, 0.0D, 1.0D);

        baseXPValue = builder
                .comment("The base amount of experience given from essence.")
                .defineInRange("baseXPValue", 7, 0, Integer.MAX_VALUE);

        extraXPValue = builder
                .comment(
                        "The extra amount of experience. The sum is calculated like this: baseXPValue + random(from 0 to extraXPValue - 1) + random(from 0 to extraXPValue - 1).")
                .defineInRange("extraXPValue", 9, 0, Integer.MAX_VALUE);

        builder.pop();
    }
}