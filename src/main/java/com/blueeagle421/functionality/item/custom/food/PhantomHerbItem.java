package com.blueeagle421.functionality.item.custom.food;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import com.blueeagle421.functionality.item.custom.TooltipItem;

import net.minecraft.core.particles.ParticleTypes;

public class PhantomHerbItem extends TooltipItem {

    private static final int COOLDOWN_TICKS = 20;

    public PhantomHerbItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        ItemStack result = super.finishUsingItem(stack, level, user);

        if (level.isClientSide)
            return result;

        if (!(user instanceof ServerPlayer serverPlayer))
            return result;

        ServerStatsCounter stats = serverPlayer.getStats();
        stats.setValue(serverPlayer, Stats.CUSTOM.get(Stats.TIME_SINCE_REST), 0);

        serverPlayer.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

        ServerLevel serverLevel = serverPlayer.serverLevel();
        serverLevel.playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                SoundEvents.PHANTOM_AMBIENT, SoundSource.PLAYERS, 1.0F, 1.0F);

        serverLevel.sendParticles(ParticleTypes.SMOKE, serverPlayer.getX(), serverPlayer.getY() + 0.5,
                serverPlayer.getZ(), 36, 0.5, 1, 0.5, 0.01);

        return result;
    }
}
