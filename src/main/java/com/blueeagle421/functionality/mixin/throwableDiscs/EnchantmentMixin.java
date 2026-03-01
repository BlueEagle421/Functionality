package com.blueeagle421.functionality.mixin.throwableDiscs;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {

    @Inject(method = "canEnchant", at = @At("HEAD"), cancellable = true)
    private void functionality$disableMending(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {

        Enchantment self = (Enchantment) (Object) this;

        if (self == Enchantments.MENDING && stack.getItem() instanceof RecordItem) {
            cir.setReturnValue(false);
        }
    }
}