package com.blueeagle421.functionality.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.List;

import com.blueeagle421.functionality.FunctionalityMod;

public class ModPlacedFeatures {

    public static final ResourceKey<PlacedFeature> RIVER_CHEST_PLACED_KEY = registerKey("river_chest_placed");

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        var configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, RIVER_CHEST_PLACED_KEY,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.RIVER_CHEST_KEY),
                List.of());
    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(FunctionalityMod.MOD_ID, name));
    }

    private static void register(
            BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key,
            Holder<ConfiguredFeature<?, ?>> configuration,
            List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}
