package com.blueeagle421.functionality.config.subcategories.items;

import com.blueeagle421.functionality.item.ModItems;

import net.minecraftforge.common.ForgeConfigSpec;

public class BearVenison {

    public final ForgeConfigSpec.BooleanValue enabled;

    public BearVenison(ForgeConfigSpec.Builder builder) {
        builder.push(ModItems.BEAR_VENISON.getId().getPath());

        enabled = builder
                .comment("If false, all items related to bear venison will be disabled.")
                .define("enabled", true);

        builder.pop();
    }
}