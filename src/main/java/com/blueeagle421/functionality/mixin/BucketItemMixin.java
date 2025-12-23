package com.blueeagle421.functionality.mixin;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blueeagle421.functionality.item.ModItems;

@Mixin(BucketItem.class)
public class BucketItemMixin {

    @Inject(method = "getEmptySuccessItem", at = @At("RETURN"), cancellable = true)
    private static void makeInfiniteBucket(ItemStack bucketStack, Player player,
            CallbackInfoReturnable<ItemStack> cir) {

        if (!bucketStack.is(ModItems.BOTTOMLESS_WATER_BUCKET.get()))
            return;

        cir.setReturnValue(bucketStack.copy());
    }
}
