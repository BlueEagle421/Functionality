package com.blueeagle421.functionality.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class ThunderRitualHandler {

    private static final int BASE_DURATION_TICKS = 72000;

    public static boolean tryTriggerRitual(ItemEntity itemEntity) {
        Level level = itemEntity.level();

        if (level == null)
            return false;

        if (!(level instanceof ServerLevel server))
            return false;

        if (server.isThundering())
            return false;

        if (!itemEntity.getItem().is(Items.HEART_OF_THE_SEA))
            return false;

        BlockPos topCopperPos = itemEntity.blockPosition().below();
        BlockState topState = level.getBlockState(topCopperPos);

        if (!topState.is(Blocks.COPPER_BLOCK))
            return false;

        if (!hasTorchesAttached(level, topCopperPos))
            return false;

        List<BlockPos> copperPositions = collectCopperColumn(level, topCopperPos);
        if (copperPositions.isEmpty())
            return false;

        createLightning(server, topCopperPos);

        int copperCount = copperPositions.size();

        long computed = (long) BASE_DURATION_TICKS * (long) copperCount;
        int durationTicks = (int) Math.min((long) Integer.MAX_VALUE, computed);

        setThunderWeather(server, durationTicks);

        for (BlockPos pos : copperPositions)
            level.destroyBlock(pos, false);

        itemEntity.discard();

        return true;
    }

    private static void setThunderWeather(ServerLevel server, int duration) {
        server.setWeatherParameters(0, duration, true, true);
        server.setRainLevel(1.0F);
        server.setThunderLevel(1.0F);
    }

    private static void createLightning(ServerLevel server, BlockPos pos) {
        LightningBolt lightning = (LightningBolt) EntityType.LIGHTNING_BOLT.create(server);
        if (lightning != null) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1.0;
            double z = pos.getZ() + 0.5;
            lightning.moveTo(x, y, z);
            server.addFreshEntity(lightning);
        }
    }

    private static boolean hasTorchesAttached(Level level, BlockPos pos) {
        Direction[] dirs = new Direction[] { Direction.NORTH, Direction.EAST,
                Direction.SOUTH, Direction.WEST };
        for (Direction dir : dirs) {
            BlockPos torchPos = pos.relative(dir);
            BlockState torchState = level.getBlockState(torchPos);

            if (!torchState.is(Blocks.WALL_TORCH))
                return false;
        }

        return true;
    }

    private static List<BlockPos> collectCopperColumn(Level level, BlockPos startPos) {
        List<BlockPos> list = new ArrayList<>();
        BlockPos pos = startPos;

        while (true) {
            BlockState state = level.getBlockState(pos);
            if (!state.is(Blocks.COPPER_BLOCK)) {
                break;
            }
            list.add(pos);
            pos = pos.below();

            if (pos.getY() < level.getMinBuildHeight())
                break;
        }

        return list;
    }
}
