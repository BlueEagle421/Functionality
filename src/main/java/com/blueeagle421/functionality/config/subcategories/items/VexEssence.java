package com.blueeagle421.functionality.config.subcategories.items;

import com.blueeagle421.functionality.item.ModItems;

import net.minecraftforge.common.ForgeConfigSpec;

public class VexEssence {

    public final ForgeConfigSpec.BooleanValue enabled;

    public VexEssence(ForgeConfigSpec.Builder builder) {
        builder.push(ModItems.VEX_ESSENCE.getId().getPath());

        enabled = builder
                .comment("If false, the item won't be acquirable and present in tabs effectively disabling it.")
                .define("enabled", true);

        builder.pop();
    }
}