package com.blueeagle421.functionality.config.subcategories.items;

import com.blueeagle421.functionality.item.ModItems;

import net.minecraftforge.common.ForgeConfigSpec;

public class Terrapin {

    public final ForgeConfigSpec.BooleanValue enabled;

    public Terrapin(ForgeConfigSpec.Builder builder) {
        builder.push(ModItems.TERRAPIN.getId().getPath());

        enabled = builder
                .comment("If false, all items related to terrapin will be disabled.")
                .define("enabled", true);

        builder.pop();
    }
}