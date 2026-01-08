package com.blueeagle421.functionality.mixin.lavaTravel;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blueeagle421.functionality.compat.CurioCompat;
import com.blueeagle421.functionality.compat.ModCompatManager;

@Mixin(FluidType.class)
public class FluidTypeMixin {

    @Inject(at = @At("HEAD"), method = "canSwim(Lnet/minecraft/world/entity/Entity;)Z", remap = false, cancellable = true)
    private void onCanSwim(Entity entity, CallbackInfoReturnable<Boolean> cir) {

        if (!ModCompatManager.curiosPresent)
            return;

        if ((Object) this == ForgeMod.LAVA_TYPE.get() && CurioCompat.Utils.hasObsidianFins(entity))
            cir.setReturnValue(true);
    }
}
