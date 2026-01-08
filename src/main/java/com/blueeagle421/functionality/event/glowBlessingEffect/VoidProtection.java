package com.blueeagle421.functionality.event.glowBlessingEffect;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.compat.CurioCompat;
import com.blueeagle421.functionality.compat.ModCompatManager;
import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.effect.ModEffects;
import com.blueeagle421.functionality.item.custom.equipment.GlowCrownItem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = FunctionalityMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VoidProtection {

    private static final Map<UUID, Vec3> LAST_GROUND_POS = new ConcurrentHashMap<>();
    private static final int SEARCH_RADIUS = 32;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.level().isClientSide)
            return;

        Player player = event.player;
        if (player.onGround()) {
            LAST_GROUND_POS.put(player.getUUID(), player.position());
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;
        if (!(player.level() instanceof ServerLevel server))
            return;
        if (!player.hasEffect(ModEffects.GLOW_BLESSING.get()))
            return;
        if (!event.getSource().is(DamageTypes.FELL_OUT_OF_WORLD))
            return;

        event.setCanceled(true);

        destroyCrown(player);

        addVoidCorruption(player);

        Vec3 lastPos = LAST_GROUND_POS.get(player.getUUID());
        if (lastPos == null)
            return;

        BlockPos safePos = findNearestSafeBlock(server, BlockPos.containing(lastPos), SEARCH_RADIUS);

        // Teleport player
        Vec3 teleportPos = (safePos != null)
                ? new Vec3(safePos.getX() + 0.5, safePos.getY() + 1, safePos.getZ() + 0.5)
                : lastPos;

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.teleportTo(
                    server,
                    teleportPos.x,
                    teleportPos.y,
                    teleportPos.z,
                    serverPlayer.getYRot(),
                    serverPlayer.getXRot());
        } else {
            player.setPos(teleportPos.x, teleportPos.y, teleportPos.z);
        }

        // reset fall distance
        player.fallDistance = 0.0f;

        // play sound
        server.playSound(null, teleportPos.x, teleportPos.y, teleportPos.z,
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);

        // spawn particles
        spawnParticles(server, teleportPos);
    }

    private static void destroyCrown(Player player) {
        if (!ModCompatManager.curiosPresent)
            return;

        ItemStack crownStack = CurioCompat.Utils.getCurio(player, GlowCrownItem.class);

        if (crownStack == null)
            return;

        crownStack.shrink(1);
    }

    private static void addVoidCorruption(Player player) {
        if (player.hasEffect(ModEffects.VOID_CORRUPTION.get()))
            return;

        player.addEffect(new MobEffectInstance(
                ModEffects.VOID_CORRUPTION.get(),
                FunctionalityConfig.COMMON.items.glowHerb.voidCorruptionDuration.get()));
    }

    private static BlockPos findNearestSafeBlock(Level level, BlockPos center, int radius) {
        BlockPos closest = null;
        double closestDistance = Double.MAX_VALUE;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    if (isStandable(level, pos)) {
                        double distance = pos.distSqr(center);
                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closest = pos;
                        }
                    }
                }
            }
        }

        return closest;
    }

    private static boolean isStandable(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        BlockState above = level.getBlockState(pos.above());

        return state.isSolidRender(level, pos) && above.isAir();
    }

    private static void spawnParticles(ServerLevel server, Vec3 pos) {
        int amount = 50;
        double offset = 0.5; // how far particles spread
        double speed = 0.02d;

        for (int i = 0; i < amount; i++) {
            double offsetX = (server.random.nextDouble() - 0.5) * 2 * offset;
            double offsetY = server.random.nextDouble() * offset;
            double offsetZ = (server.random.nextDouble() - 0.5) * 2 * offset;

            server.sendParticles(ParticleTypes.PORTAL,
                    pos.x + offsetX,
                    pos.y + offsetY,
                    pos.z + offsetZ,
                    1, // 1 particle per send
                    0, 0, 0, speed);
        }
    }
}
