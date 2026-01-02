package com.blueeagle421.functionality.mixin.throwableDiscs;

import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.entity.ModEntities;
import com.blueeagle421.functionality.entity.custom.ThrownDiscEntity;
import com.blueeagle421.functionality.sound.ModSounds;

import net.minecraft.stats.Stats;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onUse(Level level, Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack stack = player.getItemInHand(hand);

        if (!FunctionalityConfig.COMMON.features.throwableDiscs.enabled.get())
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
        if (hand == InteractionHand.OFF_HAND)
            slot = -2;
        else
            slot = player.getInventory().selected;

        disc.setReturnSlot(slot);

        Vec3 eyePos = player.getEyePosition(1.0F);
        Vec3 look = player.getViewVector(1.0F).normalize();

        disc.setPos(eyePos.x, eyePos.y - 0.1D, eyePos.z);

        double speed = ThrownDiscEntity.OUTGOING_SPEED;
        double vx = look.x * speed;
        double vy = look.y * speed;
        double vz = look.z * speed;
        disc.setDeltaMovement(vx, vy, vz);

        float yaw = (float) (Math.atan2(look.x, look.z) * (180.0D / Math.PI));
        float pitch = (float) (Math.atan2(look.y, Math.sqrt(look.x * look.x + look.z * look.z)) * (180.0D / Math.PI));
        disc.setYRot(yaw);
        disc.setXRot(pitch);

        level.addFreshEntity(disc);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                ModSounds.DISC_THROW.get(),
                net.minecraft.sounds.SoundSource.PLAYERS,
                0.65F,
                1.0F);

        if (!player.getAbilities().instabuild)
            stack.shrink(1);

        player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
    }
}
