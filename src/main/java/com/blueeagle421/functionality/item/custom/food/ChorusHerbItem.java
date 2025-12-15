package com.blueeagle421.functionality.item.custom.food;

import java.util.Optional;

import com.blueeagle421.functionality.item.custom.TooltipItem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent.ChorusFruit;

public class ChorusHerbItem extends TooltipItem {
    public ChorusHerbItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        if (!level.isClientSide && user instanceof ServerPlayer serverPlayer) {
            BlockPos respawnPos = serverPlayer.getRespawnPosition();
            float respawnAngle = serverPlayer.getRespawnAngle();
            boolean respawnForced = serverPlayer.isRespawnForced();
            MinecraftServer server = serverPlayer.getServer();

            ServerLevel respawnLevel = getRespawnLevel(serverPlayer);
            Optional<Vec3> optionalRespawnVec = findRespawnPosition(respawnLevel, respawnPos, respawnAngle,
                    respawnForced);

            ServerLevel targetLevel = determineTargetLevel(respawnLevel, optionalRespawnVec, server, level);
            Vec3AndYaw target = calculateTargetVecAndYaw(optionalRespawnVec, targetLevel, respawnPos, respawnAngle);

            performTeleport(level, targetLevel, serverPlayer, target.vec, target.yaw);
        }

        return super.finishUsingItem(stack, level, user);
    }

    private ServerLevel getRespawnLevel(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server != null && player.getRespawnDimension() != null)
            return server.getLevel(player.getRespawnDimension());

        return null;
    }

    private Optional<Vec3> findRespawnPosition(ServerLevel respawnLevel, BlockPos respawnPos, float respawnAngle,
            boolean respawnForced) {
        if (respawnLevel != null && respawnPos != null) {
            return Player.findRespawnPositionAndUseSpawnBlock(respawnLevel, respawnPos, respawnAngle, respawnForced,
                    false);
        }
        return Optional.empty();
    }

    private ServerLevel determineTargetLevel(ServerLevel respawnLevel, Optional<Vec3> optionalPos,
            MinecraftServer server, Level currentLevel) {
        if (respawnLevel != null && optionalPos.isPresent())
            return respawnLevel;

        return server != null ? server.overworld() : (ServerLevel) currentLevel;
    }

    private Vec3AndYaw calculateTargetVecAndYaw(Optional<Vec3> optionalPos, ServerLevel targetLevel,
            BlockPos respawnPos, float respawnAngle) {
        if (optionalPos.isPresent()) {
            Vec3 vec = optionalPos.get();
            BlockState blockState = targetLevel.getBlockState(respawnPos);
            boolean isAnchor = blockState.is(Blocks.RESPAWN_ANCHOR);

            float targetYaw;
            if (!blockState.is(BlockTags.BEDS) && !isAnchor) {
                targetYaw = respawnAngle;
            } else {
                Vec3 center = Vec3.atBottomCenterOf(respawnPos).subtract(vec).normalize();
                targetYaw = (float) Mth.wrapDegrees(Mth.atan2(center.z, center.x) * (180.0D / Math.PI) - 90.0D);
            }

            return new Vec3AndYaw(vec, targetYaw);
        } else {
            BlockPos worldSpawn = targetLevel.getSharedSpawnPos();
            return new Vec3AndYaw(Vec3.atCenterOf(worldSpawn), targetLevel.getSharedSpawnAngle());
        }
    }

    private void performTeleport(Level currentLevel, ServerLevel targetLevel, ServerPlayer serverPlayer, Vec3 targetVec,
            float targetYaw) {

        if (serverPlayer.isPassenger())
            serverPlayer.stopRiding();

        currentLevel.gameEvent(GameEvent.TELEPORT, serverPlayer.position(), GameEvent.Context.of(serverPlayer));

        ChorusFruit evt = ForgeEventFactory.onChorusFruitTeleport(
                serverPlayer,
                targetVec.x,
                targetVec.y,
                targetVec.z);

        if (evt.isCanceled())
            return;

        serverPlayer.teleportTo(
                targetLevel,
                evt.getTargetX(),
                evt.getTargetY(),
                evt.getTargetZ(),
                targetYaw,
                0.0F);

        serverPlayer.fallDistance = 0.0F;

        sendPortalParticles(targetLevel, evt);
        ensureNoCollisionAbove(serverPlayer);
        playTeleportSound(targetLevel, serverPlayer, evt);
        serverPlayer.getCooldowns().addCooldown(this, 20);
    }

    private void sendPortalParticles(ServerLevel level, ChorusFruit evt) {
        level.sendParticles(
                ParticleTypes.PORTAL,
                evt.getTargetX(),
                evt.getTargetY() + 1.0D,
                evt.getTargetZ(),
                32,
                0.5D, 0.5D, 0.5D,
                0.1D);
    }

    private void ensureNoCollisionAbove(ServerPlayer player) {
        while (!player.serverLevel().noCollision(player)
                && player.getY() < player.serverLevel().getMaxBuildHeight()) {
            player.setPos(player.getX(), player.getY() + 1.0D, player.getZ());
        }
    }

    private void playTeleportSound(ServerLevel level, ServerPlayer player, ChorusFruit evt) {
        SoundEvent sound = SoundEvents.CHORUS_FRUIT_TELEPORT;

        level.playSound(null,
                evt.getTargetX(),
                evt.getTargetY(),
                evt.getTargetZ(),
                sound,
                SoundSource.PLAYERS,
                1.0F,
                1.0F);

        player.playSound(sound, 1.0F, 1.0F);
    }

    private static class Vec3AndYaw {
        final Vec3 vec;
        final float yaw;

        Vec3AndYaw(Vec3 vec, float yaw) {
            this.vec = vec;
            this.yaw = yaw;
        }
    }
}
