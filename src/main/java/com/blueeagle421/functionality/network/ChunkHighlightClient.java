package com.blueeagle421.functionality.network;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.core.BlockPos;

import java.util.HashSet;
import java.util.Set;

public class ChunkHighlightClient {

    private static final Set<ChunkPos> alwaysRenderChunks = new HashSet<>();

    /** Toggle all chunks in a radius around a BlockPos */
    public static void toggle(BlockPos pos, int radius) {
        boolean added = false;
        ChunkPos center = new ChunkPos(pos);

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                ChunkPos chunk = new ChunkPos(center.x + dx, center.z + dz);
                // If any chunk is added, mark as added
                if (alwaysRenderChunks.add(chunk)) {
                    added = true;
                } else {
                    alwaysRenderChunks.remove(chunk);
                }
            }
        }

        // optional: could return added status if needed
    }

    /** Check if a chunk is always rendered */
    public static boolean isAlwaysRender(ChunkPos chunk) {
        return alwaysRenderChunks.contains(chunk);
    }

    /** Get all chunks currently always rendered */
    public static Set<ChunkPos> getAll() {
        return alwaysRenderChunks;
    }
}
