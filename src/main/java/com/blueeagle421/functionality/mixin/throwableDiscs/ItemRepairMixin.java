package com.blueeagle421.functionality.mixin.throwableDiscs;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blueeagle421.functionality.config.FunctionalityConfig;

@Mixin(Item.class)
public class ItemRepairMixin {

    @Inject(method = "isValidRepairItem", at = @At("HEAD"), cancellable = true)
    private void onIsValidRepairItem(
            ItemStack toRepair,
            ItemStack repair,
            CallbackInfoReturnable<Boolean> cir) {
        var config = FunctionalityConfig.COMMON.features.throwableDiscs;

        Ingredient ingredient = config.getDiscRepairIngredient();
        if (!ingredient.isEmpty() && ingredient.test(repair)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}