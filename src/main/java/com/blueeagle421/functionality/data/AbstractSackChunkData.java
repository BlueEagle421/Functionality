package com.blueeagle421.functionality.data;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.saveddata.SavedData;

public abstract class AbstractSackChunkData extends SavedData {
    protected static final String TAG_ENTRIES = "Entries";
    protected static final String TAG_CHUNK = "Chunk";
    protected static final String TAG_COUNT = "Count";
    protected static final String TAG_LAST_DAY = "LastDay";

    protected final Map<Long, Integer> counts = new HashMap<>();
    protected long lastProcessedDay = -1L;

    protected AbstractSackChunkData() {
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

    protected void readFromTag(CompoundTag nbt) {
        counts.clear();
        if (nbt.contains(TAG_ENTRIES)) {
            ListTag entries = nbt.getList(TAG_ENTRIES, 10); // 10 = Compound
            for (int i = 0; i < entries.size(); i++) {
                CompoundTag ent = entries.getCompound(i);
                long chunk = ent.getLong(TAG_CHUNK);
                int count = ent.getInt(TAG_COUNT);
                counts.put(chunk, count);
            }
        }
        if (nbt.contains(TAG_LAST_DAY)) {
            lastProcessedDay = nbt.getLong(TAG_LAST_DAY);
        }
    }

    public int getCurrentForChunkOrInit(long chunkLong) {
        return counts.computeIfAbsent(chunkLong, k -> getMaxPerChunk());
    }

    public boolean consumeOne(long chunkLong) {
        int cur = counts.getOrDefault(chunkLong, getMaxPerChunk());
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
            int after = Math.min(getMaxPerChunk(), before + getDailyRegen());
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

    protected abstract int getMaxPerChunk();

    protected abstract int getDailyRegen();
}
