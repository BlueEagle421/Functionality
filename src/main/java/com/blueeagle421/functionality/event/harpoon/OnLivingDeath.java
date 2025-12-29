package com.blueeagle421.functionality.event.harpoon;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.utils.UnderwaterUtils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FunctionalityMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OnLivingDeath {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        Level level = event.getEntity().level();
        if (level == null)
            return;

        if (!(level instanceof ServerLevel server))
            return;

        DamageSource source = event.getSource();
        ServerPlayer attacker = UnderwaterUtils.findPlayerAttacker(source);
        if (attacker == null)
            return;

        if (!attacker.isInWater())
            return;

        var weapon = UnderwaterUtils.findUnderwaterWeapon(attacker);

        if (weapon == null)
            return;

        if (weapon.getAirRegenAmount() == 0)
            return;

        int currentAir = attacker.getAirSupply();
        int maxAir = attacker.getMaxAirSupply();
        int newAir = Math.min(maxAir, currentAir + weapon.getAirRegenAmount());
        attacker.setAirSupply(newAir);

        spawnBubblesAroundEntity(server, event.getEntity(), 6);
    }

    private static void spawnBubblesAroundEntity(ServerLevel level, Entity entity, int count) {
        var box = entity.getBoundingBox();

        for (int i = 0; i < count; i++) {
            double x = level.random.nextDouble() * (box.maxX - box.minX) + box.minX;
            double y = level.random.nextDouble() * (box.maxY - box.minY) + box.minY;
            double z = level.random.nextDouble() * (box.maxZ - box.minZ) + box.minZ;

            level.sendParticles(
                    ParticleTypes.BUBBLE,
                    x, y, z,
                    1,
                    0.02, 0.02, 0.02,
                    0.02);
        }
    }
}
