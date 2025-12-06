package com.blueeagle421.functionality.event;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.effect.ModEffects;

@Mod.EventBusSubscriber(modid = FunctionalityMod.MOD_ID)
public class OnEffectAdded {

    private static final List<MobEffect> TO_CANCEL = List.of(
            MobEffects.POISON,
            MobEffects.WITHER,
            MobEffects.LEVITATION);

    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        LivingEntity entity = event.getEntity();
        if (entity == null)
            return;

        if (!entity.hasEffect(ModEffects.GLOW_SHIELD.get()))
            return;

        MobEffect incoming = event.getEffectInstance().getEffect();
        if (TO_CANCEL.contains(incoming)) {
            // DENY => the incoming effect will not be applied
            event.setResult(Result.DENY);
        }
    }
}
