package com.blueeagle421.functionality.config.subcategories.items;

import com.blueeagle421.functionality.item.ModItems;

import net.minecraftforge.common.ForgeConfigSpec;

public class ObsidianBoat {

    public final ForgeConfigSpec.BooleanValue enabled;

    public ObsidianBoat(ForgeConfigSpec.Builder builder) {
        builder.push(ModItems.OBSIDIAN_BOAT.getId().getPath());

        enabled = builder
                .comment("If false, the item won't be craftable and present in tabs effectively disabling it.")
                .define("enabled", true);

        builder.pop();
    }
}