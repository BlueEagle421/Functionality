package com.blueeagle421.functionality.item.custom;

import java.util.Optional;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent.ChorusFruit;

public class ChorusHerbItem extends TooltipItem {
    public ChorusHerbItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        ItemStack result = super.finishUsingItem(stack, level, user);

        if (level.isClientSide)
            return result;

        if (!(user instanceof ServerPlayer serverPlayer))
            return result;

        BlockPos respawnPos = serverPlayer.getRespawnPosition();
        float respawnAngle = serverPlayer.getRespawnAngle();
        boolean respawnForced = serverPlayer.isRespawnForced();
        MinecraftServer server = serverPlayer.getServer();

        ServerLevel respawnLevel = null;
        if (server != null && serverPlayer.getRespawnDimension() != null) {
            respawnLevel = server.getLevel(serverPlayer.getRespawnDimension());
        }

        Optional<Vec3> optionalPos = Optional.empty();

        if (respawnLevel != null && respawnPos != null)
            optionalPos = Player.findRespawnPositionAndUseSpawnBlock(respawnLevel, respawnPos, respawnAngle,
                    respawnForced, false);

        ServerLevel targetLevel = (respawnLevel != null && optionalPos.isPresent()) ? respawnLevel
                : server != null ? server.overworld() : (ServerLevel) level;

        Vec3 targetVec;
        float targetYaw;

        if (optionalPos.isPresent()) {
            Vec3 vec = optionalPos.get();

            BlockState blockState = targetLevel.getBlockState(respawnPos);
            boolean isAnchor = blockState.is(Blocks.RESPAWN_ANCHOR);

            if (!blockState.is(BlockTags.BEDS) && !isAnchor)
                targetYaw = respawnAngle;
            else {
                Vec3 center = Vec3.atBottomCenterOf(respawnPos).subtract(vec).normalize();
                targetYaw = (float) Mth.wrapDegrees(Mth.atan2(center.z, center.x) * (180.0D / Math.PI) - 90.0D);
            }

            targetVec = vec;
        } else {
            BlockPos worldSpawn = targetLevel.getSharedSpawnPos();
            targetVec = Vec3.atCenterOf(worldSpawn);
            targetYaw = targetLevel.getSharedSpawnAngle();
        }

        if (user.isPassenger()) {
            user.stopRiding();
        }

        level.gameEvent(GameEvent.TELEPORT, user.position(), Context.of(user));

        ChorusFruit evt = ForgeEventFactory.onChorusFruitTeleport(user, targetVec.x, targetVec.y, targetVec.z);

        if (evt.isCanceled())
            return result;

        double finalX = evt.getTargetX();
        double finalY = evt.getTargetY();
        double finalZ = evt.getTargetZ();

        serverPlayer.teleportTo(targetLevel, finalX, finalY, finalZ, targetYaw, 0.0F);

        while (!serverPlayer.serverLevel().noCollision(serverPlayer)
                && serverPlayer.getY() < (double) serverPlayer.serverLevel().getMaxBuildHeight()) {
            serverPlayer.setPos(serverPlayer.getX(), serverPlayer.getY() + 1.0D, serverPlayer.getZ());
        }

        SoundEvent sound = SoundEvents.CHORUS_FRUIT_TELEPORT;
        targetLevel.playSound((Player) null, finalX, finalY, finalZ, sound, SoundSource.PLAYERS, 1.0F, 1.0F);
        serverPlayer.playSound(sound, 1.0F, 1.0F);

        serverPlayer.getCooldowns().addCooldown(this, 20);

        return result;
    }
}
