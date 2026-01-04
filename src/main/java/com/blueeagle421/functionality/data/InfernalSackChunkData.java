package com.blueeagle421.functionality.data;

import com.blueeagle421.functionality.config.FunctionalityConfig;

import net.minecraft.nbt.CompoundTag;

public class InfernalSackChunkData extends AbstractSackChunkData {
    private static final String DATA_NAME = "functionality_infernal_sack_chunk_data";

    public InfernalSackChunkData() {
        super();
    }

    public static InfernalSackChunkData load(CompoundTag nbt) {
        InfernalSackChunkData data = new InfernalSackChunkData();
        data.readFromTag(nbt);
        return data;
    }

    public static String storageKey() {
        return DATA_NAME;
    }

    @Override
    protected int getMaxPerChunk() {
        return FunctionalityConfig.COMMON.features.infernalSacks.maxPerChunk.get();
    }

    @Override
    protected int getDailyRegen() {
        return FunctionalityConfig.COMMON.features.infernalSacks.dailyRegen.get();
    }
}
