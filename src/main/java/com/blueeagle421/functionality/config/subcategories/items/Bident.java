package com.blueeagle421.functionality.config.subcategories.items;

import com.blueeagle421.functionality.item.ModItems;

import net.minecraftforge.common.ForgeConfigSpec;

public class Bident {

    public final ForgeConfigSpec.BooleanValue enabled;
    public final ForgeConfigSpec.BooleanValue attractItems;
    public final ForgeConfigSpec.IntValue airBubblesRegenAmount;
    public final ForgeConfigSpec.IntValue extraReach;

    public Bident(ForgeConfigSpec.Builder builder) {
        builder.push(ModItems.BIDENT.getId().getPath());

        enabled = builder
                .comment("If false, the item won't be craftable and present in tabs effectively disabling it.")
                .define("enabled", true);

        attractItems = builder
                .comment("If false, the items won't be attracted to player after killing a mob underwater.")
                .define("attractItems", true);

        airBubblesRegenAmount = builder
                .comment("The amount of air bubble ticks that will be regenerated after killing. 60 -> two bubbles.")
                .defineInRange("airBubblesRegenAmount", 60, 0, Integer.MAX_VALUE);

        extraReach = builder
                .comment("The extra reach that will be added to weapon while underwater.")
                .defineInRange("extraReach", 6, 0, Integer.MAX_VALUE);

        builder.pop();
    }
}