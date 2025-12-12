package com.blueeagle421.functionality.data;

import java.util.HashMap;
import java.util.Map;

import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.config.subcategories.features.TreasureSacks;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.saveddata.SavedData;

public class TreasureSackChunkData extends SavedData {
    private static final String DATA_NAME = "functionality_treasure_sack_chunk_data";
    private static final String TAG_ENTRIES = "Entries";
    private static final String TAG_CHUNK = "Chunk";
    private static final String TAG_COUNT = "Count";
    private static final String TAG_LAST_DAY = "LastDay";

    private final Map<Long, Integer> counts = new HashMap<>();
    private long lastProcessedDay = -1L;

    public TreasureSackChunkData() {
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        ListTag entries = new ListTag();
        for (Map.Entry<Long, Integer> e : counts.entrySet()) {
            CompoundTag ent = new CompoundTag();
            ent.putLong(TAG_CHUNK, e.getKey());
            ent.putInt(TAG_COUNT, e.getValue());
            entries.add(ent);
        }
        nbt.put(TAG_ENTRIES, entries);
        nbt.putLong(TAG_LAST_DAY, lastProcessedDay);
        return nbt;
    }

    public static TreasureSackChunkData load(CompoundTag nbt) {
        TreasureSackChunkData data = new TreasureSackChunkData();
        if (nbt.contains(TAG_ENTRIES)) {
            ListTag entries = nbt.getList(TAG_ENTRIES, 10); // 10 = Compound
            for (int i = 0; i < entries.size(); i++) {
                CompoundTag ent = entries.getCompound(i);
                long chunk = ent.getLong(TAG_CHUNK);
                int count = ent.getInt(TAG_COUNT);
                data.counts.put(chunk, count);
            }
        }
        if (nbt.contains(TAG_LAST_DAY)) {
            data.lastProcessedDay = nbt.getLong(TAG_LAST_DAY);
        }
        return data;
    }

    public int getCurrentForChunkOrInit(long chunkLong) {
        return counts.computeIfAbsent(chunkLong, k -> config().maxPerChunk.get());
    }

    public boolean consumeOne(long chunkLong) {
        int cur = counts.getOrDefault(chunkLong, config().maxPerChunk.get());
        if (cur <= 0) {
            counts.put(chunkLong, 0);
            return false;
        } else {
            counts.put(chunkLong, cur - 1);
            setDirty();
            return true;
        }
    }

    public void dailyRegen() {
        boolean changed = false;
        for (Map.Entry<Long, Integer> e : counts.entrySet()) {
            int before = e.getValue();
            int after = Math.min(config().maxPerChunk.get(),
                    before + config().dailyRegen.get());
            if (after != before) {
                e.setValue(after);
                changed = true;
            }
        }
        if (changed)
            setDirty();
    }

    public long getLastProcessedDay() {
        return lastProcessedDay;
    }

    public void setLastProcessedDay(long day) {
        this.lastProcessedDay = day;
        setDirty();
    }

    public static String storageKey() {
        return DATA_NAME;
    }

    private TreasureSacks config() {
        return FunctionalityConfig.COMMON.features.treasureSacks;
    }
}
