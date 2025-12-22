package com.blueeagle421.functionality.mixin;

import com.blueeagle421.functionality.item.custom.PheromonesItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Horse.class)
public class HorseMobInteractMixin {

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void onMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty())
            return;

        if (!(stack.getItem() instanceof PheromonesItem))
            return;

        Level level = player.level();

        if (level.isClientSide) {
            cir.setReturnValue(InteractionResult.sidedSuccess(true));
            cir.cancel();
            return;
        }

        Horse horse = (Horse) (Object) this;
        if (horse.getOwnerUUID() != null) {
            return;
        }

        boolean tamed = horse.tameWithName(player);

        if (tamed) {
            PheromonesItem.tameSucceeded((ServerLevel) level, player, horse, stack);

            cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide()));
            cir.cancel();
        }

    }
}
