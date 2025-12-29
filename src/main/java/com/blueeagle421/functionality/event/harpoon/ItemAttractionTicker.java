package com.blueeagle421.functionality.event.harpoon;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.config.subcategories.items.Harpoon;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = FunctionalityMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemAttractionTicker {

    private static final double ATTRACT_RADIUS = 32.0;
    private static final double ATTRACT_ACCEL = 0.15;
    private static final double DAMPING = 0.9;
    private static final double MAX_SPEED = 1.6;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {

        if (event.player.level().isClientSide)
            return;

        if (event.phase != TickEvent.Phase.END)
            return;

        Player player = event.player;
        Level level = player.level();

        AABB searchBox = player.getBoundingBox().inflate(ATTRACT_RADIUS);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, searchBox, item -> {
            CompoundTag t = item.getPersistentData();
            return t.contains("AttractedBy");
        });

        for (ItemEntity item : items) {
            if (item == null || item.isRemoved())
                continue;

            CompoundTag tag = item.getPersistentData();
            UUID owner = tag.getUUID("AttractedBy");
            if (!player.getUUID().equals(owner))
                continue;

            int ticksLeft = tag.getInt("AttractTicks");
            if (ticksLeft <= 0) {
                tag.remove("AttractedBy");
                tag.remove("AttractTicks");
                continue;
            }

            Vec3 targetPos = player.position();
            Vec3 dir = targetPos.subtract(item.position());
            double dist = dir.length();

            if (dist < 0.5) {
                tag.putInt("AttractTicks", ticksLeft - 1);
                continue;
            }

            Vec3 norm = dir.normalize();
            Vec3 currentVel = item.getDeltaMovement();

            Vec3 accel = norm.scale(ATTRACT_ACCEL);
            Vec3 newVel = currentVel.scale(DAMPING).add(accel);

            double len = newVel.length();
            if (len > MAX_SPEED)
                newVel = newVel.normalize().scale(MAX_SPEED);

            item.setDeltaMovement(newVel);

            if (item.isInWater() && level instanceof ServerLevel server) {
                // every second tick
                if ((item.tickCount & 1) == 0) {
                    Vec3 pos = item.position();

                    server.sendParticles(
                            ParticleTypes.BUBBLE,
                            pos.x,
                            pos.y + 0.1,
                            pos.z,
                            2,
                            0.05, 0.05, 0.05,
                            0.01);
                }
            }

            item.setNoPickUpDelay();
            item.setThrower(owner);

            tag.putInt("AttractTicks", ticksLeft - 1);
        }
    }

    private static Harpoon config() {
        return FunctionalityConfig.COMMON.items.harpoon;
    }
}
