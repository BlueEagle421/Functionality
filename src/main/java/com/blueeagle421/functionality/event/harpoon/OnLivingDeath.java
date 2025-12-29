package com.blueeagle421.functionality.event.harpoon;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.config.subcategories.items.Harpoon;
import com.blueeagle421.functionality.item.custom.equipment.HarpoonItem;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
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

        if (!(level instanceof ServerLevel serverLevel))
            return;

        DamageSource source = event.getSource();
        ServerPlayer attacker = findPlayerAttacker(source);
        if (attacker == null)
            return;

        if (!attacker.isInWater())
            return;

        if (!playerUsedHarpoon(attacker, source))
            return;

        int currentAir = attacker.getAirSupply();
        int maxAir = attacker.getMaxAirSupply();
        int newAir = Math.min(maxAir, currentAir + config().airBubblesRegenAmount.get());
        attacker.setAirSupply(newAir);

        serverLevel.sendParticles(ParticleTypes.BUBBLE, attacker.getX(), attacker.getY() + 0.5,
                attacker.getZ(), 6, 0.2, 0.2, 0.2, 0.02);
    }

    private static ServerPlayer findPlayerAttacker(DamageSource source) {
        if (source == null)
            return null;

        Entity direct = source.getDirectEntity();
        Entity trueSource = source.getEntity();

        if (trueSource instanceof ServerPlayer p)
            return p;
        if (direct instanceof ServerPlayer p2)
            return p2;

        if (direct instanceof Projectile proj) {
            Entity owner = proj.getOwner();
            if (owner instanceof ServerPlayer p3)
                return p3;
        }

        return null;
    }

    private static boolean playerUsedHarpoon(Player player, DamageSource source) {

        if (player.getMainHandItem().getItem() instanceof HarpoonItem)
            return true;

        if (player.getOffhandItem().getItem() instanceof HarpoonItem)
            return true;

        return false;
    }

    private static Harpoon config() {
        return FunctionalityConfig.COMMON.items.harpoon;
    }
}
