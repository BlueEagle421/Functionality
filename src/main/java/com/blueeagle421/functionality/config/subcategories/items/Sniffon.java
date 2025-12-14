package com.blueeagle421.functionality.config.subcategories.items;

import com.blueeagle421.functionality.item.ModItems;

import net.minecraftforge.common.ForgeConfigSpec;

public class Sniffon {

    public final ForgeConfigSpec.BooleanValue enabled;

    public Sniffon(ForgeConfigSpec.Builder builder) {
        builder.push(ModItems.SNIFFON.getId().getPath());

        enabled = builder
                .comment(
                        "If false, all items related to sniffon will be disabled.")
                .define("enabled", true);

        builder.pop();
    }
}