package com.blueeagle421.functionality.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blueeagle421.functionality.effect.ModEffects;

@Mixin(VibrationSystem.Listener.class)
public class VibrationSystemListenerMixin {

    private static final Set<GameEvent> EVENTS_TO_CANCEL = Set.of(
            GameEvent.EAT,
            GameEvent.ITEM_INTERACT_FINISH,
            GameEvent.HIT_GROUND);

    @Inject(method = "handleGameEvent", at = @At("HEAD"), cancellable = true)
    private void cancelEatVibrations(
            ServerLevel level,
            GameEvent gameEvent,
            GameEvent.Context context,
            Vec3 pos,
            CallbackInfoReturnable<Boolean> cir) {

        if (!(context.sourceEntity() instanceof LivingEntity entity))
            return;

        if (!entity.hasEffect(ModEffects.CALMNESS.get()))
            return;

        if (!EVENTS_TO_CANCEL.contains(gameEvent))
            return;

        cir.setReturnValue(false);
    }
}
