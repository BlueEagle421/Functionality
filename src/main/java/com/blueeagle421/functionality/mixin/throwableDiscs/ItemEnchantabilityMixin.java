package com.blueeagle421.functionality.mixin.throwableDiscs;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blueeagle421.functionality.config.FunctionalityConfig;

@Mixin(value = Item.class)
public abstract class ItemEnchantabilityMixin {

    @Inject(method = "isEnchantable", at = @At("HEAD"), cancellable = true)
    private void makeRecordEnchantable(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        boolean enabled = FunctionalityConfig.COMMON.features.throwableDiscs.enabled.get();

        if (enabled && stack.getItem() instanceof RecordItem) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getEnchantmentValue", at = @At("HEAD"), cancellable = true)
    private void giveRecordEnchantValue(CallbackInfoReturnable<Integer> cir) {
        Item self = (Item) (Object) this;
        boolean enabled = FunctionalityConfig.COMMON.features.throwableDiscs.enabled.get();
        if (enabled && self instanceof RecordItem) {
            cir.setReturnValue(14); // same as iron tools
        }
    }
}
