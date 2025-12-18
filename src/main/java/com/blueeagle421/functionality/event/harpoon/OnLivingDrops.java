package com.blueeagle421.functionality.event.harpoon;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.config.subcategories.items.Harpoon;
import com.blueeagle421.functionality.item.custom.HarpoonItem;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = FunctionalityMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OnLivingDrops {

    private static final double ATTRACT_SPEED = 1;

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {

        if (event.getEntity().level().isClientSide)
            return;

        if (!config().attractItems.get())
            return;

        List<ItemEntity> drops = new ArrayList<>(event.getDrops());

        if (drops == null || drops.isEmpty())
            return;

        Player attacker = findPlayerAttacker(event.getSource());

        if (attacker == null)
            return;

        if (!attacker.isUnderWater())
            return;

        if (!playerUsedHarpoon(attacker, event.getSource()))
            return;

        Vec3 targetPos = attacker.position().add(0.0, attacker.getEyeHeight() - 0.25f, 0.0);

        for (ItemEntity itemEntity : drops) {
            if (itemEntity == null || itemEntity.isRemoved())
                continue;

            Vec3 itemPos = itemEntity.position();
            Vec3 dir = targetPos.subtract(itemPos);
            double dist = dir.length();

            Vec3 norm = dir.normalize();

            double speed = ATTRACT_SPEED * Math.min(1.0, 1.0 + (1.0 / Math.max(dist, 1.0) - 1.0) * 0.25);
            double vx = norm.x * speed;
            double vy = norm.y * speed;
            double vz = norm.z * speed;

            itemEntity.setDeltaMovement(vx, vy, vz);

            itemEntity.setThrower(attacker.getUUID());
            itemEntity.setNoPickUpDelay();
        }
    }

    private static Player findPlayerAttacker(DamageSource source) {
        if (source == null)
            return null;

        Entity direct = source.getDirectEntity();
        Entity trueSource = source.getEntity();

        if (trueSource instanceof Player p)
            return p;

        if (direct instanceof Player p2)
            return p2;

        if (direct instanceof Projectile proj) {
            Entity owner = proj.getOwner();
            if (owner instanceof Player p3)
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
