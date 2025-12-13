package com.blueeagle421.functionality.config.subcategories.features;

import com.blueeagle421.functionality.FunctionalityMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.*;

public class TreasureSacks {
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> illagerEntities;
    public final ForgeConfigSpec.BooleanValue limitEnabled;
    public final ForgeConfigSpec.IntValue maxPerChunk;
    public final ForgeConfigSpec.IntValue dailyRegen;

    public TreasureSacks(ForgeConfigSpec.Builder builder) {
        builder.push("illager treasure sacks");

        List<String> defaults = Arrays.asList(
                "minecraft:vindicator",
                "minecraft:evoker",
                "minecraft:pillager");

        this.illagerEntities = builder
                .comment("List of entity IDs (namespace:path) that should receive the illager treasure loot.")
                .defineList("illagerEntities", defaults, o -> o instanceof String);

        limitEnabled = builder
                .comment("If true, the treasure sacks drops are limited per chunk to avoid farming them.")
                .define("limitEnabled", true);

        maxPerChunk = builder
                .comment("Max amount of treasure sacks a single chunk can store.")
                .defineInRange("maxPerChunk", 30, 1, Integer.MAX_VALUE);

        dailyRegen = builder
                .comment("The amount of treasure sacks added to all chunks per day.")
                .defineInRange("dailyRegen", 5, 0, Integer.MAX_VALUE);

        builder.pop();
    }

    @SuppressWarnings("removal")
    public Set<ResourceLocation> getEntitiesAsResourceLocations() {
        List<? extends String> rawList = this.illagerEntities.get();
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
                FunctionalityMod.LOGGER.warn("Invalid entity id in config illagerEntities: '{}'. Ignoring.", raw);
            }
        }
        return out;
    }
}
