package com.blueeagle421.functionality.effect;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.effect.custom.GlowShieldEffect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS,
            FunctionalityMod.MOD_ID);

    public static final RegistryObject<MobEffect> GLOW_SHIELD = MOB_EFFECTS.register("glow_shield",
            () -> new GlowShieldEffect(MobEffectCategory.BENEFICIAL, 0xf4bc58));

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}