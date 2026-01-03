package com.blueeagle421.functionality.event.treasureSack;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.data.TreasureSackChunkData;
import com.blueeagle421.functionality.item.custom.openable.TreasureSackItem;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FunctionalityMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OnLivingDrops {

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {

        if (!(event.getEntity().level() instanceof ServerLevel server))
            return;

        if (!config().features.treasureSacks.limitEnabled.get())
            return;

        // read or create data
        TreasureSackChunkData data = server.getDataStorage()
                .computeIfAbsent(TreasureSackChunkData::load, TreasureSackChunkData::new,
                        TreasureSackChunkData.storageKey());

        for (ItemEntity itemEntity : event.getDrops()) {
            if (itemEntity.getItem().getItem() instanceof TreasureSackItem) {

                long chunkLong = getChunk(itemEntity.blockPosition());
                data.getCurrentForChunkOrInit(chunkLong);

                // do we have more sacks here?
                boolean allowed = config().items.treasureSack.enabled.get() && data.consumeOne(chunkLong);

                if (!allowed)
                    itemEntity.remove(RemovalReason.DISCARDED);
            }
        }
    }

    private static long getChunk(BlockPos pos) {
        // 4 right bits shifts same as dividing by 16 and flooring
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        return ChunkPos.asLong(chunkX, chunkZ);
    }

    private static FunctionalityConfig.Common config() {
        return FunctionalityConfig.COMMON;
    }
}
