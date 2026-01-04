package com.blueeagle421.functionality.data;

import com.blueeagle421.functionality.config.FunctionalityConfig;

import net.minecraft.nbt.CompoundTag;

public class TreasureSackChunkData extends AbstractSackChunkData {
    private static final String DATA_NAME = "functionality_treasure_sack_chunk_data";

    public TreasureSackChunkData() {
        super();
    }

    public static TreasureSackChunkData load(CompoundTag nbt) {
        TreasureSackChunkData data = new TreasureSackChunkData();
        data.readFromTag(nbt);
        return data;
    }

    public static String storageKey() {
        return DATA_NAME;
    }

    @Override
    protected int getMaxPerChunk() {
        return FunctionalityConfig.COMMON.features.treasureSacks.maxPerChunk.get();
    }

    @Override
    protected int getDailyRegen() {
        return FunctionalityConfig.COMMON.features.treasureSacks.dailyRegen.get();
    }
}
