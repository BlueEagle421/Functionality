package com.blueeagle421.functionality.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blueeagle421.functionality.effect.ModEffects;

@Mixin(Entity.class)
public class MixinLivingEntity {

    @Inject(method = "vibrationAndSoundEffectsFromBlock", at = @At("HEAD"), cancellable = true)
    private void disableFootsteps(BlockPos pos, BlockState state, boolean playStepSound, boolean broadcastGameEvent,
            Vec3 p_286448_, CallbackInfoReturnable<Boolean> cir) {

        if (!((Object) this instanceof Player player))
            return;

        if (!player.hasEffect(ModEffects.CALMNESS.get()))
            return;

        cir.setReturnValue(false);
    }
}