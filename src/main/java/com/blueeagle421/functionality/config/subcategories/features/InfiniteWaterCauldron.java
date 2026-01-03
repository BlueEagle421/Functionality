package com.blueeagle421.functionality.config.subcategories.features;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.*;

import com.blueeagle421.functionality.FunctionalityMod;

public class InfiniteWaterCauldron {
    public final ForgeConfigSpec.BooleanValue enabled;
    public final ForgeConfigSpec.IntValue regenWaterTicks;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> infiniteWaterBlocks;

    public InfiniteWaterCauldron(ForgeConfigSpec.Builder builder) {
        builder.push("infinite water cauldron");

        enabled = builder
                .comment("If true, the cauldron will regenerate water when a configured block is placed below it.")
                .define("enabled", true);

        List<String> defaults = Arrays.asList(
                "minecraft:prismarine",
                "minecraft:dark_prismarine",
                "minecraft:prismarine_bricks",
                "upgrade_aquatic:luminous_prismarine",
                "minecraft:sea_lantern");

        regenWaterTicks = builder
                .comment("The ticks duration of a single water regeneration in the infinite cauldron.")
                .defineInRange("regenWaterTicks", 8, 1, Integer.MAX_VALUE);

        this.infiniteWaterBlocks = builder
                .comment(
                        "List of block IDs (namespace:path) that should make a cauldron have infinite water if placed below it.")
                .defineList("infiniteWaterBlocks", defaults, o -> o instanceof String);

        builder.pop();
    }

    @SuppressWarnings("removal")
    public Set<ResourceLocation> getBlocksAsResourceLocations() {
        List<? extends String> rawList = this.infiniteWaterBlocks.get();
        if (rawList == null || rawList.isEmpty())
            return Collections.emptySet();

        Set<ResourceLocation> out = new HashSet<>();
        for (String raw : rawList) {
            if (raw == null)
                continue;
            String s = raw.trim();
            if (s.isEmpty())
                continue;

            try {
                ResourceLocation rl = new ResourceLocation(s);
                out.add(rl);
            } catch (Exception e) {
                FunctionalityMod.LOGGER.warn("Invalid block id in config infiniteWaterBlocks: '{}'. Ignoring.", raw);
            }
        }
        return out;
    }
}
