package com.blueeagle421.functionality.config.subcategories.items;

import com.blueeagle421.functionality.item.ModItems;

import net.minecraftforge.common.ForgeConfigSpec;

public class FrogLeg {

    public final ForgeConfigSpec.BooleanValue enabled;

    public FrogLeg(ForgeConfigSpec.Builder builder) {
        builder.push(ModItems.FROG_LEG.getId().getPath());

        enabled = builder
                .comment("If false, all items related to frog leg will be disabled.")
                .define("enabled", true);

        builder.pop();
    }
}