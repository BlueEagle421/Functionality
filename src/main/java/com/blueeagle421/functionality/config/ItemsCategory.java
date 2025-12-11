package com.blueeagle421.functionality.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ItemsCategory {

    public ItemsCategory(ForgeConfigSpec.Builder builder) {
        builder.push("items");

        builder.pop();
    }
}
