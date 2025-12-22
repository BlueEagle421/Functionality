package com.blueeagle421.functionality.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class PhantomTreatItem extends TooltipItem {
    private static final RandomSource RANDOM = RandomSource.create();

    private static final int RANDOM_MSG_COUNT = 6;

    private static final int MAX_USES = 4;
    private static final double SPEED_INCREASE_PER_USE = 0.075;

    private static final String NBT_USES = "PhantomTreatUses";
    private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("c8c87d9a-9f4b-4e2e-87c7-1f5e0a7a5b23");

    public PhantomTreatItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target,
            InteractionHand hand) {

        if (target instanceof Player)
            return InteractionResult.PASS;

        if (!player.level().isClientSide) {

            int usesBefore = getUses(target);

            sendTreatMessage(player, target, usesBefore);

            applySpeed(target);

            target.level().playSound(
                    null,
                    target.getX(),
                    target.getY(),
                    target.getZ(),
                    SoundEvents.GENERIC_EAT,
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F);

            spawnItemParticles(target, stack, 8);
        }

        return InteractionResult.SUCCESS;
    }

    private static int getUses(LivingEntity entity) {
        return entity.getPersistentData().getInt(NBT_USES);
    }

    private static void setUses(LivingEntity entity, int value) {
        entity.getPersistentData().putInt(NBT_USES, value);
    }

    private static void applySpeed(LivingEntity entity) {
        int uses = getUses(entity);

        if (uses >= MAX_USES)
            return;

        var attribute = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attribute == null)
            return;

        if (attribute.getModifier(SPEED_MODIFIER_UUID) != null) {
            attribute.removeModifier(SPEED_MODIFIER_UUID);
        }

        AttributeModifier modifier = new AttributeModifier(
                SPEED_MODIFIER_UUID,
                "phantom_treat_speed_boost",
                SPEED_INCREASE_PER_USE * (uses + 1),
                AttributeModifier.Operation.MULTIPLY_TOTAL);

        attribute.addPermanentModifier(modifier);

        setUses(entity, uses + 1);
    }

    private record MessageResult(String key, boolean tooMuch) {
    }

    private static void sendTreatMessage(Player player, LivingEntity target, int usesBefore) {
        MessageResult result = getMessage(usesBefore);
        Component msg = Component.translatable(result.key(), target.getDisplayName());

        if (result.tooMuch())
            msg = msg.copy().withStyle(ChatFormatting.DARK_RED);

        player.displayClientMessage(msg, true);
    }

    private static MessageResult getMessage(int usesBefore) {
        String result;
        boolean tooMuch = false;
        int randomKeysCount = 6;
        int maxUses = 4;
        if (usesBefore >= maxUses) {
            result = "message.functionality.phantom_treat.too_much";
            tooMuch = true;
        } else if (usesBefore == 0)
            result = "message.functionality.phantom_treat.start";
        else if (usesBefore == maxUses - 1)
            result = "message.functionality.phantom_treat.last";
        else {
            int roll = RANDOM.nextInt(randomKeysCount) + 1;
            result = "message.functionality.phantom_treat.random" + roll;
        }
        return new MessageResult(result, tooMuch);
    }

    public static void spawnItemParticles(LivingEntity entity, ItemStack stack, int amount) {
        final double EXPANSION = 0.08; // distance outside the hitbox

        double centerX = entity.getX();
        double centerY = entity.getY() + entity.getBbHeight() / 2.0;
        double centerZ = entity.getZ();

        final double radiusX = entity.getBbWidth() / 2.0 + EXPANSION;
        final double radiusY = entity.getBbHeight() / 2.0 + EXPANSION;
        final double radiusZ = entity.getBbWidth() / 2.0 + EXPANSION;

        Level level = entity.level();

        for (int i = 0; i < amount; i++) {
            double x = centerX + (level.random.nextDouble() - 0.5) * 2 * radiusX;
            double y = centerY + (level.random.nextDouble() - 0.5) * 2 * radiusY;
            double z = centerZ + (level.random.nextDouble() - 0.5) * 2 * radiusZ;

            if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        new ItemParticleOption(ParticleTypes.ITEM, stack),
                        x, y, z,
                        1,
                        0, 0, 0,
                        0);
            }

            if (level.isClientSide()) {
                level.addParticle(
                        new ItemParticleOption(ParticleTypes.ITEM, stack),
                        x, y, z,
                        0, 0, 0);
            }
        }
    }
}
