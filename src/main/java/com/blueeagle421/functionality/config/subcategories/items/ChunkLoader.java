package com.blueeagle421.functionality.config.subcategories.items;

import com.blueeagle421.functionality.item.ModItems;

import net.minecraftforge.common.ForgeConfigSpec;

public class ChunkLoader {

    public final ForgeConfigSpec.BooleanValue enabled;

    public ChunkLoader(ForgeConfigSpec.Builder builder) {
        builder.push(ModItems.CHUNK_LOADER.getId().getPath());

        enabled = builder
                .comment("If false, the item won't be craftable and present in tabs effectively disabling it.")
                .define("enabled", true);

        builder.pop();
    }
}