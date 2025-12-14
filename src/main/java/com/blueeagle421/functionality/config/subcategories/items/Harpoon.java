package com.blueeagle421.functionality.config.subcategories.items;

import com.blueeagle421.functionality.item.ModItems;

import net.minecraftforge.common.ForgeConfigSpec;

public class Harpoon {

    public final ForgeConfigSpec.BooleanValue enabled;

    public Harpoon(ForgeConfigSpec.Builder builder) {
        builder.push(ModItems.HARPOON.getId().getPath());

        enabled = builder
                .comment("If false, the item won't be craftable and present in tabs effectively disabling it.")
                .define("enabled", true);

        builder.pop();
    }
}