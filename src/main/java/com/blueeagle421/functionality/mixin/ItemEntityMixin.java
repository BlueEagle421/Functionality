package com.blueeagle421.functionality.mixin;

import com.blueeagle421.functionality.event.ThunderRitualHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class ItemEntityMixin {

    @Shadow
    protected boolean onGround;

    @Inject(method = "setOnGround", at = @At("HEAD"))
    private void onSetOnGround(boolean pOnGround, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (pOnGround && !entity.onGround() && entity instanceof ItemEntity item) {
            ThunderRitualHandler.tryTriggerRitual(item);
        }
    }

    @Inject(method = "setOnGroundWithKnownMovement", at = @At("HEAD"))
    private void onSetOnGroundWithKnownMovement(boolean pOnGround, Vec3 pMovement, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (pOnGround && !entity.onGround() && entity instanceof ItemEntity item) {
            ThunderRitualHandler.tryTriggerRitual(item);
        }
    }
}
