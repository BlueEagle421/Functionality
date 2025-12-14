package com.blueeagle421.functionality.mixin.throwableDiscs;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.config.subcategories.features.ThrowableDiscs;

@Mixin(Item.class)
public abstract class ItemEnchantabilityMixin {

    @Inject(method = "isEnchantable", at = @At("HEAD"), cancellable = true)
    private void makeStickEnchantable(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (config().enabled.get() && stack.getItem() instanceof RecordItem) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getEnchantmentValue", at = @At("HEAD"), cancellable = true)
    private void giveStickEnchantValue(CallbackInfoReturnable<Integer> cir) {
        Item self = (Item) (Object) this;
        if (config().enabled.get() && self instanceof RecordItem) {
            cir.setReturnValue(14); // same as iron tools
        }
    }

    private static ThrowableDiscs config() {
        return FunctionalityConfig.COMMON.features.throwableDiscs;
    }
}
