package com.blueeagle421.functionality.mixin;

import com.blueeagle421.functionality.entity.custom.ObsidianBoatEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityStopRidingMixin {

    @Inject(method = "stopRiding()V", at = @At("HEAD"))
    private void onStopRiding(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        Entity vehicle = self.getVehicle();

        if (!(vehicle instanceof ObsidianBoatEntity))
            return;

        if (!(self instanceof LivingEntity living))
            return;

        living.clearFire();
    }
}
