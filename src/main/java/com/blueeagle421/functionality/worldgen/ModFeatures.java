package com.blueeagle421.functionality.worldgen;

import com.blueeagle421.functionality.FunctionalityMod;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES,
            FunctionalityMod.MOD_ID);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> RIVER_CHEST = FEATURES.register("river_chest",
            () -> new RiverChestFeature(NoneFeatureConfiguration.CODEC));

    public static void register(IEventBus bus) {
        FEATURES.register(bus);
    }
}
