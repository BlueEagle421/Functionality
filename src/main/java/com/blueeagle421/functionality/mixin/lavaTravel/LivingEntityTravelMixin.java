package com.blueeagle421.functionality.mixin.lavaTravel;

import com.blueeagle421.functionality.compat.CurioCompat;
import com.blueeagle421.functionality.compat.ModCompatManager;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LivingEntity.class)
public class LivingEntityTravelMixin {

    private static final float LAVA_MOVE_MULTIPLIER = 1.15f;
    private static final double LAVA_SLOWDOWN_REDUCTION = 0.85d;
    private static final float LAVA_VERTICAL_DAMPING = 0.45f;
    private static final double LAVA_FALL_REDUCTION = 0.8d;

    @ModifyArg(method = "travel(Lnet/minecraft/world/phys/Vec3;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;moveRelative(FLnet/minecraft/world/phys/Vec3;)V"), index = 0)
    private float modifyMoveRelative(float originalValue, Vec3 travel) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (!ModCompatManager.curiosPresent)
            return originalValue;

        if (!self.isInLava())
            return originalValue;

        if (CurioCompat.Utils.hasObsidianFins(self))
            return originalValue * LAVA_MOVE_MULTIPLIER;

        return originalValue;
    }

    @ModifyConstant(method = "travel(Lnet/minecraft/world/phys/Vec3;)V", constant = @org.spongepowered.asm.mixin.injection.Constant(floatValue = 0.8F))
    private float modifyLavaVerticalDamping(float original) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (!ModCompatManager.curiosPresent)
            return original;

        if (CurioCompat.Utils.hasObsidianFins(self))
            return LAVA_VERTICAL_DAMPING;

        return original;
    }

    @ModifyConstant(method = "travel(Lnet/minecraft/world/phys/Vec3;)V", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 0.5D))
    private double modifyLavaHorizontalSlowdown(double original) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (!ModCompatManager.curiosPresent)
            return original;

        if (CurioCompat.Utils.hasObsidianFins(self))
            return LAVA_SLOWDOWN_REDUCTION;

        return original;
    }

    @ModifyArg(method = "travel(Lnet/minecraft/world/phys/Vec3;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"), index = 0)
    private Vec3 reduceDownwardWhenInLava(Vec3 original) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (!ModCompatManager.curiosPresent)
            return original;

        if (!self.isInLava() || !CurioCompat.Utils.hasObsidianFins(self))
            return original;

        double y = original.y;
        if (y < 0)
            y = y * LAVA_FALL_REDUCTION;

        return new Vec3(original.x, y, original.z);
    }
}
