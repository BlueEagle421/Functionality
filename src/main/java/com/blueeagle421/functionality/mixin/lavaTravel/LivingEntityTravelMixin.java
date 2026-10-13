package com.blueeagle421.functionality.mixin.lavaTravel;

import com.blueeagle421.functionality.compat.CurioCompat;
import com.blueeagle421.functionality.compat.ModCompatManager;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LivingEntity.class)
public class LivingEntityTravelMixin {

    private static final float LAVA_MOVE_MULTIPLIER = 1.15f;
    private static final double VANILLA_LAVA_SLOWDOWN = 0.5d;
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

    // The horizontal slowdown is scaled through the arguments of the lava calls instead of a
    // @ModifyConstant on 0.5D: only one mod can modify a constant, which crashed with mods that
    // require theirs (e.g. Additional Entity Attributes, bundled in Origins)
    @ModifyArg(method = "travel(Lnet/minecraft/world/phys/Vec3;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;multiply(DDD)Lnet/minecraft/world/phys/Vec3;", ordinal = 1), index = 0)
    private double modifyLavaSlowdownX(double original) {
        return functionality$modifyLavaHorizontalSlowdown(original);
    }

    @ModifyArg(method = "travel(Lnet/minecraft/world/phys/Vec3;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;multiply(DDD)Lnet/minecraft/world/phys/Vec3;", ordinal = 1), index = 2)
    private double modifyLavaSlowdownZ(double original) {
        return functionality$modifyLavaHorizontalSlowdown(original);
    }

    @ModifyArg(method = "travel(Lnet/minecraft/world/phys/Vec3;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;scale(D)Lnet/minecraft/world/phys/Vec3;"), index = 0)
    private double modifyLavaSlowdownScale(double original) {
        return functionality$modifyLavaHorizontalSlowdown(original);
    }

    @Unique
    private double functionality$modifyLavaHorizontalSlowdown(double original) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (!ModCompatManager.curiosPresent)
            return original;

        if (CurioCompat.Utils.hasObsidianFins(self))
            return original * (LAVA_SLOWDOWN_REDUCTION / VANILLA_LAVA_SLOWDOWN);

        return original;
    }

    // No index: the Vec3 is found by type, so this still applies when another mod (e.g. Aileron)
    // redirects the call and shifts its arguments
    @ModifyArg(method = "travel(Lnet/minecraft/world/phys/Vec3;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
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
