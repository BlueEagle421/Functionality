package com.blueeagle421.functionality.mixin.throwableDiscs;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.RecordItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.config.subcategories.features.ThrowableDiscs;

@Mixin(Item.class)
public class ItemRepairMixin {

    @Inject(method = "isValidRepairItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void onIsValidRepairItem(ItemStack toRepair, ItemStack repair, CallbackInfoReturnable<Boolean> cir) {

        if (!(((Object) this) instanceof RecordItem))
            return;

        if (config().repairWithSelf.get() && repair.is(toRepair.getItem())) {
            cir.setReturnValue(true);
            return;
        }

        if (repair.is(Items.DISC_FRAGMENT_5)) {
            cir.setReturnValue(true);
        }
    }

    private static ThrowableDiscs config() {
        return FunctionalityConfig.COMMON.features.throwableDiscs;
    }
}
