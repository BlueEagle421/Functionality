package com.blueeagle421.functionality.event;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.effect.ModEffects;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FunctionalityMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VoidCorruptionCureHandler {

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide)
            return;

        if (event.getEffectInstance().getEffect() != MobEffects.REGENERATION)
            return;

        if (!entity.hasEffect(ModEffects.VOID_CORRUPTION.get()))
            return;

        entity.removeEffect(ModEffects.VOID_CORRUPTION.get());
    }
}
