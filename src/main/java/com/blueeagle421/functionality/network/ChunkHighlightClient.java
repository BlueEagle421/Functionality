package com.blueeagle421.functionality.network;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ChunkHighlightClient {

    private static final Set<ChunkPos> alwaysRenderChunks = new HashSet<>();

    private static final Map<ChunkPos, Integer> refCounts = new HashMap<>();

    private static final Map<BlockPos, Integer> loaderRadius = new HashMap<>();

    public static synchronized boolean isAlwaysRender(ChunkPos chunk) {
        return alwaysRenderChunks.contains(chunk);
    }

    public static synchronized Set<ChunkPos> getAll() {
        return new HashSet<>(alwaysRenderChunks);
    }

    private static void addChunk(ChunkPos chunk) {
        int count = refCounts.getOrDefault(chunk, 0) + 1;
        refCounts.put(chunk, count);
        alwaysRenderChunks.add(chunk);
    }

    private static void removeChunk(ChunkPos chunk) {
        Integer count = refCounts.get(chunk);
        if (count == null)
            return;
        if (count <= 1) {
            refCounts.remove(chunk);
            alwaysRenderChunks.remove(chunk);
        } else {
            refCounts.put(chunk, count - 1);
        }
    }

    public static synchronized void toggle(BlockPos pos, int radius) {
        pos = pos.immutable();

        if (loaderRadius.containsKey(pos)) {
            removeChunksForLoader(pos);
        } else {
            addChunksForLoader(pos, radius);
        }
    }

    public static synchronized void removeChunksForLoader(BlockPos pos) {
        pos = pos.immutable();

        Integer prev = loaderRadius.remove(pos);
        if (prev == null)
            return;

        ChunkPos center = new ChunkPos(pos);
        for (int dx = -prev; dx <= prev; dx++) {
            for (int dz = -prev; dz <= prev; dz++) {
                ChunkPos chunk = new ChunkPos(center.x + dx, center.z + dz);
                removeChunk(chunk);
            }
        }
    }

    public static synchronized void addChunksForLoader(BlockPos pos, int radius) {
        ChunkPos center = new ChunkPos(pos);
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                ChunkPos chunk = new ChunkPos(center.x + dx, center.z + dz);
                addChunk(chunk);
            }
        }
        loaderRadius.put(pos.immutable(), radius);

    }

    public static synchronized void update(BlockPos pos, int radius) {
        pos = pos.immutable();

        if (!loaderRadius.containsKey(pos))
            return;

        removeChunksForLoader(pos);
        addChunksForLoader(pos, radius);
    }

    public static synchronized void remove(BlockPos pos, int radius) {
        ChunkPos center = new ChunkPos(pos);

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                ChunkPos chunk = new ChunkPos(center.x + dx, center.z + dz);
                removeChunk(chunk);
            }
        }
    }
}
