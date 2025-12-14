package com.blueeagle421.functionality.config.subcategories.items;

import com.blueeagle421.functionality.item.ModItems;

import net.minecraftforge.common.ForgeConfigSpec;

public class FishTrap {

    public final ForgeConfigSpec.BooleanValue enabled;

    public FishTrap(ForgeConfigSpec.Builder builder) {
        builder.push(ModItems.FISH_TRAP.getId().getPath());

        enabled = builder
                .comment("If false, the item won't be craftable and present in tabs effectively disabling it.")
                .define("enabled", true);

        builder.pop();
    }
}