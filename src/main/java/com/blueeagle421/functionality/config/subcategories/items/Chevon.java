package com.blueeagle421.functionality.config.subcategories.items;

import com.blueeagle421.functionality.item.ModItems;

import net.minecraftforge.common.ForgeConfigSpec;

public class Chevon {

    public final ForgeConfigSpec.BooleanValue enabled;

    public Chevon(ForgeConfigSpec.Builder builder) {
        builder.push(ModItems.CHEVON.getId().getPath());

        enabled = builder
                .comment("If false, all items related to chevon leg will be disabled.")
                .define("enabled", true);

        builder.pop();
    }
}