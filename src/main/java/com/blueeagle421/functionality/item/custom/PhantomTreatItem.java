package com.blueeagle421.functionality.item.custom;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;
import java.util.WeakHashMap;

public class PhantomTreatItem extends TooltipItem {

    private static final WeakHashMap<LivingEntity, Integer> usageCounts = new WeakHashMap<>();
    private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("c8c87d9a-9f4b-4e2e-87c7-1f5e0a7a5b23");

    public PhantomTreatItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target,
            InteractionHand hand) {

        if (target instanceof Player)
            return InteractionResult.PASS;

        if (!player.level().isClientSide) {
            applySpeed(target);

            target.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                    SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 1.0F, 1.0F);

            spawnItemParticles(target, stack, 8);
        }

        return InteractionResult.SUCCESS;
    }

    public static void applySpeed(LivingEntity entity) {
        int uses = usageCounts.getOrDefault(entity, 0);

        if (uses >= 4) {
            return;
        }

        double speedIncrease = 0.075;

        if (entity.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(SPEED_MODIFIER_UUID) != null) {
            entity.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SPEED_MODIFIER_UUID);
        }

        AttributeModifier modifier = new AttributeModifier(
                SPEED_MODIFIER_UUID,
                "phantom_treat_speed_boost",
                speedIncrease * (uses + 1),
                AttributeModifier.Operation.MULTIPLY_TOTAL);
        entity.getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(modifier);

        usageCounts.put(entity, uses + 1);
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
