package com.blueeagle421.functionality.item.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.blueeagle421.functionality.utils.TooltipUtils;

public class PheromonesItem extends Item {
    public PheromonesItem(Properties props) {
        super(props);
    }

    // horse specific interaction is handled in mixin
    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target,
            InteractionHand hand) {

        Level level = player.level();

        if (level.isClientSide)
            return InteractionResult.sidedSuccess(level.isClientSide);

        // TamableAnimal case
        if (target instanceof TamableAnimal animal) {
            UUID owner = animal.getOwnerUUID();

            if (owner != null && !owner.equals(player.getUUID()))
                return InteractionResult.PASS;

            if (animal.isOwnedBy(player))
                return InteractionResult.sidedSuccess(level.isClientSide);

            animal.tame(player);
            level.broadcastEntityEvent(animal, (byte) 7);

            tameSucceeded((ServerLevel) level, player, animal, stack);

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        // AbstractHorse case
        if (target instanceof AbstractHorse horse) {
            UUID owner = horse.getOwnerUUID();

            if (owner != null)
                return InteractionResult.PASS;

            boolean tamed = horse.tameWithName(player);
            if (tamed) {
                tameSucceeded((ServerLevel) level, player, horse, stack);
                return InteractionResult.sidedSuccess(level.isClientSide);
            } else
                return InteractionResult.PASS;
        }

        return InteractionResult.PASS;
    }

    public static void tameSucceeded(ServerLevel level, Player player, LivingEntity target, ItemStack stack) {

        // consume item
        consumeIfNeeded(player, stack);

        // sound
        level.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.HONEY_DRINK, SoundSource.PLAYERS, 1f, 1.0f);

        // heart particles
        spawnHearts(level, target);
    }

    private static void consumeIfNeeded(Player player, ItemStack stack) {
        if (player.isCreative())
            return;
        stack.shrink(1);
    }

    private static void spawnHearts(ServerLevel level, LivingEntity target) {
        double x = target.getX();
        double y = target.getY() + target.getBbHeight() * 0.5;
        double z = target.getZ();
        level.sendParticles(ParticleTypes.HEART, x, y, z, 7, 0.25, 0.25, 0.25, 0.02);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip,
            TooltipFlag isAdvanced) {

        TooltipUtils.addFormattedTooltip(stack, tooltip);

        super.appendHoverText(stack, level, tooltip, isAdvanced);
    }
}
