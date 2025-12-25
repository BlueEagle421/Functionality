package com.blueeagle421.functionality.config.subcategories.items;

import com.blueeagle421.functionality.item.ModItems;

import net.minecraftforge.common.ForgeConfigSpec;

public class RepairAltar {

    public final ForgeConfigSpec.BooleanValue enabled;
    public final ForgeConfigSpec.DoubleValue deactivateChance;

    public RepairAltar(ForgeConfigSpec.Builder builder) {
        builder.push(ModItems.REPAIR_ALTAR.getId().getPath());

        enabled = builder
                .comment("If false, the item won't be craftable and present in tabs effectively disabling it.")
                .define("enabled", true);

        deactivateChance = builder
                .comment("A flat chance for the altar to deactivate after a repair (0.05 = 5% chance per repair).")
                .defineInRange("deactivateChance", 0.05f, 0f, 1f);

        builder.pop();
    }
}