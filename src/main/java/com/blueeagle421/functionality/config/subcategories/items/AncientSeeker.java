package com.blueeagle421.functionality.config.subcategories.items;

import com.blueeagle421.functionality.item.ModItems;

import net.minecraftforge.common.ForgeConfigSpec;

public class AncientSeeker {

    public final ForgeConfigSpec.BooleanValue enabled;
    public final ForgeConfigSpec.IntValue searchRadius;
    public final ForgeConfigSpec.IntValue levelsCost;

    public AncientSeeker(ForgeConfigSpec.Builder builder) {
        builder.push(ModItems.ANCIENT_SEEKER.getId().getPath());

        enabled = builder
                .comment("If false, the item won't be craftable and present in tabs effectively disabling it.")
                .define("enabled", true);

        searchRadius = builder
                .comment("Max amount of treasure sacks a single chunk can store.")
                .defineInRange("searchRadius", 24, 0, 96);

        levelsCost = builder
                .comment("The amount of experience levels the seeker needs to be used.")
                .defineInRange("levelsCost", 2, 0, Integer.MAX_VALUE);

        builder.pop();
    }
}