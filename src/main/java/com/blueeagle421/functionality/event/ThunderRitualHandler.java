package com.blueeagle421.functionality.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.config.subcategories.features.ThunderRitual;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class ThunderRitualHandler {

    private static final float EXPLOSION_RADIUS = 2.0f;

    @SuppressWarnings("removal")
    public static boolean tryTriggerRitual(ItemEntity itemEntity) {
        Level level = itemEntity.level();

        if (level == null)
            return false;
        if (!(level instanceof ServerLevel server))
            return false;

        if (!config().enabled.get())
            return false;

        if (server.isThundering())
            return false;

        String ritualItemStr = config().ritualItem.get();
        Item ritualItem = null;
        try {
            ResourceLocation rl = new ResourceLocation(ritualItemStr);
            if (ForgeRegistries.ITEMS.containsKey(rl)) {
                ritualItem = ForgeRegistries.ITEMS.getValue(rl);
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        if (!itemEntity.getItem().is(ritualItem))
            return false;

        BlockPos topCopperPos = itemEntity.blockPosition().below();
        BlockState topState = level.getBlockState(topCopperPos);
        if (!topState.is(Blocks.COPPER_BLOCK))
            return false;

        if (!hasTorchesAttached(level, topCopperPos))
            return false;

        List<BlockPos> copperPositions = collectCopperColumn(level, topCopperPos, config().maxColumnHeight.get());
        if (copperPositions.isEmpty())
            return false;

        createExplosiveLightning(server, topCopperPos);

        long perBlock = config().weatherDurationPerCopperBlock.get();
        long copperCount = copperPositions.size();
        long computed = perBlock * copperCount;
        int durationTicks = (int) Math.min((long) Integer.MAX_VALUE, computed);

        setThunderWeather(server, durationTicks);

        for (BlockPos pos : copperPositions) {
            level.destroyBlock(pos, false);
        }

        itemEntity.discard();

        return true;
    }

    private static void setThunderWeather(ServerLevel server, int duration) {
        server.setWeatherParameters(0, duration, true, true);
    }

    private static void createExplosiveLightning(ServerLevel server, BlockPos pos) {
        LightningBolt lightning = (LightningBolt) EntityType.LIGHTNING_BOLT.create(server);
        if (lightning != null) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1.0;
            double z = pos.getZ() + 0.5;
            lightning.moveTo(x, y, z);
            server.addFreshEntity(lightning);

            server.explode(
                    null,
                    x, y, z,
                    EXPLOSION_RADIUS,
                    Level.ExplosionInteraction.MOB);
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

    // Collects copper blocks downward from startPos (inclusive) up to maxHeight
    // Stops when a non-copper block is encountered or when maxHeight is reached

    private static List<BlockPos> collectCopperColumn(Level level, BlockPos startPos, int maxHeight) {
        List<BlockPos> list = new ArrayList<>();
        BlockPos pos = startPos;

        while (true) {
            if (pos.getY() < level.getMinBuildHeight())
                break;

            BlockState state = level.getBlockState(pos);
            if (!state.is(Blocks.COPPER_BLOCK)) {
                break;
            }

            list.add(pos);

            if (list.size() >= maxHeight)
                break;

            pos = pos.below();
        }

        return list;
    }

    private static ThunderRitual config() {
        return FunctionalityConfig.COMMON.features.thunderRitual;
    }
}
