package com.blueeagle421.functionality.config.subcategories.features;

import com.blueeagle421.functionality.FunctionalityMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.*;

public class InfernalSacks {
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> piglinEntities;
    public final ForgeConfigSpec.BooleanValue limitEnabled;
    public final ForgeConfigSpec.IntValue maxPerChunk;
    public final ForgeConfigSpec.IntValue dailyRegen;

    public InfernalSacks(ForgeConfigSpec.Builder builder) {
        builder.push("piglin infernal sacks");

        List<String> defaults = Arrays.asList(
                "minecraft:piglin",
                "minecraft:piglin_brute");

        this.piglinEntities = builder
                .comment("List of entity IDs (namespace:path) that should receive the piglin infernal loot.")
                .defineList("piglinEntities", defaults, o -> o instanceof String);

        limitEnabled = builder
                .comment("If true, the infernal sacks drops are limited per chunk to avoid farming them.")
                .define("limitEnabled", true);

        maxPerChunk = builder
                .comment("Max amount of infernal sacks a single chunk can store.")
                .defineInRange("maxPerChunk", 30, 1, Integer.MAX_VALUE);

        dailyRegen = builder
                .comment("The amount of infernal sacks added to all chunks per day.")
                .defineInRange("dailyRegen", 5, 0, Integer.MAX_VALUE);

        builder.pop();
    }

    @SuppressWarnings("removal")
    public Set<ResourceLocation> getEntitiesAsResourceLocations() {
        List<? extends String> rawList = this.piglinEntities.get();
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
                FunctionalityMod.LOGGER.warn("Invalid entity id in config piglinEntities: '{}'. Ignoring.", raw);
            }
        }
        return out;
    }
}
