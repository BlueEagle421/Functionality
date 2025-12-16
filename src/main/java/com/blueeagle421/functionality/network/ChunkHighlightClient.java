package com.blueeagle421.functionality.network;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ChunkHighlightClient {

    // visible set of chunks (currently highlighted)
    private static final Set<ChunkPos> alwaysRenderChunks = new HashSet<>();

    // reference counts per chunk — how many loaders requested this chunk
    // highlighted
    private static final Map<ChunkPos, Integer> refCounts = new HashMap<>();

    // per-loader recorded radius so we can remove the *previous* chunks when
    // updating
    private static final Map<BlockPos, Integer> loaderRadius = new HashMap<>();

    /** Check if a chunk is always rendered */
    public static synchronized boolean isAlwaysRender(ChunkPos chunk) {
        return alwaysRenderChunks.contains(chunk);
    }

    /** Get all chunks currently always rendered (a live view) */
    public static synchronized Set<ChunkPos> getAll() {
        return new HashSet<>(alwaysRenderChunks);
    }

    /** Internal: add a chunk (increments reference count) */
    private static void addChunk(ChunkPos chunk) {
        int count = refCounts.getOrDefault(chunk, 0) + 1;
        refCounts.put(chunk, count);
        alwaysRenderChunks.add(chunk);
    }

    /** Internal: remove a chunk (decrement count and remove when reaches 0) */
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

    /**
     * Toggle all chunks in a radius around a BlockPos.
     * If this loader is currently active, remove its chunks; otherwise add them.
     */
    public static synchronized void toggle(BlockPos pos, int radius) {
        if (loaderRadius.containsKey(pos)) {
            // was active — remove its recorded chunks
            removeChunksForLoader(pos);
        } else {
            // was inactive — add its chunks
            addChunksForLoader(pos, radius);
        }
    }

    /** Remove all chunks recorded for a loader at pos (uses stored radius). */
    public static synchronized void removeChunksForLoader(BlockPos pos) {
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

    /** Add all chunks for a loader at pos with given radius (and record it). */
    public static synchronized void addChunksForLoader(BlockPos pos, int radius) {
        ChunkPos center = new ChunkPos(pos);
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                ChunkPos chunk = new ChunkPos(center.x + dx, center.z + dz);
                addChunk(chunk);
            }
        }
        loaderRadius.put(pos.immutable(), radius); // BlockPos is immutable; .immutable() is safe if available;
                                                   // otherwise pos itself is fine
    }

    /**
     * Update the highlight for a loader at a given BlockPos and radius.
     * Removes previous chunks for this loader (if any) and adds the new ones.
     */
    public static synchronized void update(BlockPos pos, int radius) {
        if (!loaderRadius.containsKey(pos))
            return;

        // remove previous (if any) then add new
        removeChunksForLoader(pos);
        addChunksForLoader(pos, radius);
    }

    /**
     * Explicitly remove all chunks in a radius around a BlockPos (not per-loader).
     * (This method is kept for compatibility, but prefer per-loader methods above.)
     */
    public static synchronized void remove(BlockPos pos, int radius) {
        ChunkPos center = new ChunkPos(pos);

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                ChunkPos chunk = new ChunkPos(center.x + dx, center.z + dz);
                // decrement reference count (if any). This is a "global" remove
                // and may remove chunks that loaders still need — use carefully.
                removeChunk(chunk);
            }
        }
    }
}
