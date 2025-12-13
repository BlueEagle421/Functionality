package com.blueeagle421.functionality.event;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.config.subcategories.features.InfiniteWaterCauldron;
import com.blueeagle421.functionality.utils.CauldronUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = FunctionalityMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CauldronEventHandler {

    private static final Map<ServerLevel, ConcurrentHashMap<BlockPos, Integer>> PENDING_FILL = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onNeighborChange(net.minecraftforge.event.level.BlockEvent.NeighborNotifyEvent event) {

        if (!(event.getLevel() instanceof Level world))
            return;

        if (world.isClientSide())
            return;

        BlockPos changedPos = event.getPos();

        BlockPos abovePos = changedPos.above();
        BlockState stateAbove = world.getBlockState(abovePos);

        if (!(stateAbove.getBlock() instanceof AbstractCauldronBlock))
            return;

        if (CauldronUtils.isInfiniteWaterSource(world, abovePos))
            scheduleFill((ServerLevel) world, abovePos);
    }

    public static void scheduleFill(ServerLevel level, BlockPos pos) {
        PENDING_FILL.computeIfAbsent(level, l -> new ConcurrentHashMap<>()).putIfAbsent(pos, 0);
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {

        if (event.phase != TickEvent.Phase.END)
            return;
        if (!(event.level instanceof ServerLevel serverLevel))
            return;

        ConcurrentHashMap<BlockPos, Integer> map = PENDING_FILL.get(serverLevel);
        if (map == null || map.isEmpty())
            return;

        Iterator<Map.Entry<BlockPos, Integer>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, Integer> entry = it.next();
            BlockPos pos = entry.getKey();
            int counter = entry.getValue() + 1;

            BlockState state = serverLevel.getBlockState(pos);

            if (!canContinueFilling(serverLevel, state, pos)) {
                it.remove();
                continue;
            }

            if (counter >= config().regenWaterTicks.get()) {
                if (state.getBlock() instanceof LayeredCauldronBlock) {
                    int curLvl = state.getValue(LayeredCauldronBlock.LEVEL);
                    if (curLvl < 3) {
                        BlockState next = state.setValue(LayeredCauldronBlock.LEVEL, curLvl + 1);
                        serverLevel.setBlockAndUpdate(pos, next);
                        serverLevel.levelEvent(1047, pos, 0);

                        spawnBubbleParticles(serverLevel, pos);
                    } else
                        it.remove();

                } else if (state.is(Blocks.CAULDRON)) {
                    BlockState waterLvl1 = Blocks.WATER_CAULDRON.defaultBlockState()
                            .setValue(LayeredCauldronBlock.LEVEL, 1);
                    serverLevel.setBlockAndUpdate(pos, waterLvl1);
                    serverLevel.levelEvent(1047, pos, 0);

                    spawnBubbleParticles(serverLevel, pos);
                } else
                    it.remove();

                counter = 0;
            }

            entry.setValue(counter);
        }

        if (map.isEmpty())
            PENDING_FILL.remove(serverLevel);
    }

    private static boolean canContinueFilling(Level level, BlockState state, BlockPos pos) {
        if (!CauldronUtils.isInfiniteWaterSource(level, pos))
            return false;

        if (state.getBlock() instanceof LayeredCauldronBlock) {
            int lvl = state.getValue(LayeredCauldronBlock.LEVEL);
            return lvl < 3;
        }

        if (state.is(Blocks.CAULDRON))
            return true;

        return false;
    }

    private static void spawnBubbleParticles(ServerLevel world, BlockPos pos) {
        for (int i = 0; i < 5; i++) {
            double px = pos.getX() + world.random.nextDouble() * 1.2 - 0.1; // from -0.1 to 1.1
            double py = pos.getY() + world.random.nextDouble() * 1.2 - 0.1; // from -0.1 to 1.1
            double pz = pos.getZ() + world.random.nextDouble() * 1.2 - 0.1; // from -0.1 to 1.1
            world.sendParticles(ParticleTypes.BUBBLE_POP, px, py, pz, 1, 0, 0, 0, 0);
        }
    }

    private static InfiniteWaterCauldron config() {
        return FunctionalityConfig.COMMON.features.infiniteWaterCauldron;
    }

}
