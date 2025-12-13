package com.blueeagle421.functionality.config.categories;

import com.blueeagle421.functionality.config.subcategories.items.*;

import net.minecraftforge.common.ForgeConfigSpec;

public class ItemsCategory {

    public final AncientSeeker ancientSeeker;

    public ItemsCategory(ForgeConfigSpec.Builder builder) {
        builder.push("items");

        ancientSeeker = new AncientSeeker(builder);

        builder.pop();
    }
}
