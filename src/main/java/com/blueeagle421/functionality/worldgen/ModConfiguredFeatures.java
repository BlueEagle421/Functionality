package com.blueeagle421.functionality.worldgen;

import com.blueeagle421.functionality.FunctionalityMod;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class ModConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> RIVER_CHEST_KEY = registerKey("river_chest");

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {

        Feature<NoneFeatureConfiguration> riverChestFeature = (Feature<NoneFeatureConfiguration>) ModFeatures.RIVER_CHEST
                .get();

        register(context, RIVER_CHEST_KEY, riverChestFeature, NoneFeatureConfiguration.INSTANCE);
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(FunctionalityMod.MOD_ID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(
            BootstapContext<ConfiguredFeature<?, ?>> context,
            ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
