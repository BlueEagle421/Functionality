package com.blueeagle421.functionality.mixin;

import com.blueeagle421.functionality.item.custom.ObsidianFinsItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LivingEntity.class)
public class LivingEntityTravelMixin {

    private static final float LAVA_MOVE_MULTIPLIER = 1.25f;
    private static final double LAVA_SLOWDOWN_REDUCTION = 0.92d;
    private static final float LAVA_VERTICAL_DAMPING = 0.35f;
    private static final double LAVA_FALL_REDUCTION = 0.85d;

    @ModifyArg(method = "travel(Lnet/minecraft/world/phys/Vec3;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;moveRelative(FLnet/minecraft/world/phys/Vec3;)V"), index = 0)
    private float modifyMoveRelative(float originalValue, Vec3 travel) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (!self.isInLava())
            return originalValue;

        if (hasObsidianFins(self))
            return originalValue * LAVA_MOVE_MULTIPLIER;

        return originalValue;
    }

    @ModifyConstant(method = "travel(Lnet/minecraft/world/phys/Vec3;)V", constant = @org.spongepowered.asm.mixin.injection.Constant(floatValue = 0.8F))
    private float modifyLavaVerticalDamping(float original) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (hasObsidianFins(self))
            return LAVA_VERTICAL_DAMPING;

        return original;
    }

    @ModifyConstant(method = "travel(Lnet/minecraft/world/phys/Vec3;)V", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 0.5D))
    private double modifyLavaHorizontalSlowdown(double original) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (hasObsidianFins(self))
            return LAVA_SLOWDOWN_REDUCTION;

        return original;
    }

    @ModifyArg(method = "travel(Lnet/minecraft/world/phys/Vec3;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"), index = 0)
    private Vec3 reduceDownwardWhenInLava(Vec3 original) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (!self.isInLava() || !hasObsidianFins(self))
            return original;

        double y = original.y;
        if (y < 0)
            y = y * LAVA_FALL_REDUCTION;

        return new Vec3(original.x, y, original.z);
    }

    private boolean hasObsidianFins(LivingEntity entity) {
        if (entity == null)
            return false;

        var feetItemStack = entity.getItemBySlot(EquipmentSlot.FEET);

        if (feetItemStack == null || feetItemStack.isEmpty())
            return false;

        return feetItemStack.getItem() instanceof ObsidianFinsItem;
    }
}
