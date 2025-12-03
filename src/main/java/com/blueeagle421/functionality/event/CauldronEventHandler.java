package com.blueeagle421.functionality.event;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.utils.CauldronUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//holy that works
@Mod.EventBusSubscriber(modid = FunctionalityMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CauldronEventHandler {

    private static final Map<ServerLevel, ConcurrentHashMap<BlockPos, Integer>> PENDING_FILL = new ConcurrentHashMap<>();

    private static final int FILL_INTERVAL_TICKS = 8;

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level world = event.getLevel();
        BlockPos pos = event.getPos();

        if (world == null || world.isClientSide)
            return;

        BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof LayeredCauldronBlock))
            return;

        var stack = event.getItemStack();
        if (stack == null)
            return;
        var item = stack.getItem();
        boolean isDrainingUse = item == Items.BUCKET || item == Items.GLASS_BOTTLE || item == Items.POTION;
        if (!isDrainingUse)
            return;

        if (CauldronUtils.isInfiniteWaterSource(world, pos))
            scheduleFill((ServerLevel) world, pos);

    }

    private static void scheduleFill(ServerLevel level, BlockPos pos) {
        PENDING_FILL.computeIfAbsent(level, l -> new ConcurrentHashMap<>()).put(pos, 0);
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {

        if (event.phase != TickEvent.Phase.END)
            return;

        if (event.level == null)
            return;

        if (event.level.isClientSide())
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

            if (counter >= FILL_INTERVAL_TICKS) {
                int curLvl = state.getValue(LayeredCauldronBlock.LEVEL);
                if (curLvl < 3) {
                    BlockState next = state.setValue(LayeredCauldronBlock.LEVEL, curLvl + 1);
                    serverLevel.setBlockAndUpdate(pos, next);

                    serverLevel.levelEvent(1047, pos, 0);
                } else {
                    it.remove();
                    continue;
                }
                counter = 0;
            } else if (state.is(Blocks.CAULDRON)) {
                BlockState waterCauldron = Blocks.WATER_CAULDRON.defaultBlockState()
                        .setValue(LayeredCauldronBlock.LEVEL, 1);
                serverLevel.setBlockAndUpdate(pos, waterCauldron);
                serverLevel.levelEvent(1047, pos, 0);
            }

            entry.setValue(counter);
        }

        if (map.isEmpty())
            PENDING_FILL.remove(serverLevel);
    }

    private static Boolean canContinueFilling(Level level, BlockState state, BlockPos pos) {
        if (!(state.getBlock() instanceof AbstractCauldronBlock cauldronBlock))
            return false;

        if (!CauldronUtils.isInfiniteWaterSource(level, pos))
            return false;

        return true;
    }

    public static void cancelScheduledFill(ServerLevel level, BlockPos pos) {
        var m = PENDING_FILL.get(level);
        if (m != null)
            m.remove(pos);
    }
}
