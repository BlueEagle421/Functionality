package com.blueeagle421.functionality.event;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import com.blueeagle421.functionality.FunctionalityMod;

@Mod.EventBusSubscriber(modid = FunctionalityMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OnHurt {

    public static final int TIME = 2400;

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level();

        if (!(entity instanceof GlowSquid) || level.isClientSide)
            return;

        List<Player> players = level.getEntitiesOfClass(
                Player.class,
                entity.getBoundingBox().inflate(4.0D),
                p -> p != null && p.isAlive() && !p.isSpectator());

        for (Player p : players) {
            p.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, TIME, 0));
        }

        if (level instanceof ServerLevel serverLevel) {
            double x = entity.getX();
            double y = entity.getY() + entity.getBbHeight() / 2.0;
            double z = entity.getZ();
            serverLevel.sendParticles(ParticleTypes.GLOW_SQUID_INK, x, y, z, 30, 0.4, 0.4, 0.4, 0.03);
        }
    }
}
