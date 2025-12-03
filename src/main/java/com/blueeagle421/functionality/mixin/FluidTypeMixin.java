package com.blueeagle421.functionality.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blueeagle421.functionality.item.custom.ObsidianFinsItem;

@Mixin(FluidType.class)
public class FluidTypeMixin {

    @Inject(at = @At("HEAD"), method = "canSwim(Lnet/minecraft/world/entity/Entity;)Z", remap = false, cancellable = true)
    private void onCanSwim(Entity entity, CallbackInfoReturnable<Boolean> cir) {

        if ((Object) this == ForgeMod.LAVA_TYPE.get() && canSwimInLava(entity))
            cir.setReturnValue(true);
    }

    private Boolean canSwimInLava(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity))
            return false;

        var feetItemStack = livingEntity.getItemBySlot(EquipmentSlot.FEET).getItem();
        return feetItemStack instanceof ObsidianFinsItem;
    }
}
