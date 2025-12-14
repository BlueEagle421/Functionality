package com.blueeagle421.functionality.mixin.throwableDiscs;

import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.config.subcategories.features.ThrowableDiscs;
import com.blueeagle421.functionality.entity.ModEntities;
import com.blueeagle421.functionality.entity.custom.ThrownDiscEntity;
import com.blueeagle421.functionality.sound.ModSounds;

import net.minecraft.stats.Stats;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;", at = @At("HEAD"), cancellable = true)
    private void onUse(Level level, Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack stack = player.getItemInHand(hand);

        if (!config().enabled.get())
            return;

        if (!(stack.getItem() instanceof RecordItem))
            return;

        if (!level.isClientSide)
            throwDisc(level, player, stack, hand);

        cir.setReturnValue(InteractionResultHolder.sidedSuccess(stack, level.isClientSide));
        cir.cancel();
    }

    private static void throwDisc(Level level, Player player, ItemStack stack, InteractionHand hand) {
        ThrownDiscEntity disc = new ThrownDiscEntity(ModEntities.THROWN_DISC.get(), player, level);
        disc.setDiscStack(stack.copy());

        int slot;
        if (hand == InteractionHand.OFF_HAND) {
            slot = -2;
        } else {
            slot = player.getInventory().selected;
        }
        disc.setReturnSlot(slot);

        disc.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.6F, 0.0F);
        level.addFreshEntity(disc);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                ModSounds.DISC_THROW.get(),
                net.minecraft.sounds.SoundSource.PLAYERS,
                0.8F,
                1.0F);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
    }

    private static ThrowableDiscs config() {
        return FunctionalityConfig.COMMON.features.throwableDiscs;
    }
}
