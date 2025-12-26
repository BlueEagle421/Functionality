package com.blueeagle421.functionality.config.subcategories.items;

import com.blueeagle421.functionality.item.ModItems;

import net.minecraftforge.common.ForgeConfigSpec;

public class GlowHerb {

    public final ForgeConfigSpec.BooleanValue enabled;
    public final ForgeConfigSpec.IntValue voidCorruptionDuration;

    public GlowHerb(ForgeConfigSpec.Builder builder) {
        builder.push(ModItems.GLOW_HERB.getId().getPath());

        enabled = builder
                .comment("If false, the item won't be craftable and present in tabs effectively disabling it.")
                .define("enabled", true);

        voidCorruptionDuration = builder
                .comment(
                        "The duration in ticks of void corruption after taking void damage. Default: 20 seconds * 20 ticks per second = 400 ticks")
                .defineInRange("voidCorruptionDuration", 20 * 20, 0, Integer.MAX_VALUE);

        builder.pop();
    }
}