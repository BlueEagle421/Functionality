package com.blueeagle421.functionality.particle;

import com.blueeagle421.functionality.FunctionalityMod;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister
            .create(ForgeRegistries.PARTICLE_TYPES, FunctionalityMod.MOD_ID);

    public static final RegistryObject<SimpleParticleType> GLOW_SMOKE = PARTICLE_TYPES
            .register("glow_smoke", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> ANCIENT_SEEKER = PARTICLE_TYPES
            .register("ancient_seeker", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> BLOOM_LICHEN_AIR = PARTICLE_TYPES
            .register("bloom_lichen_air", () -> new SimpleParticleType(true));

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}