package com.blueeagle421.functionality.network;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

import java.util.ArrayList;
import java.util.List;

public class ChunkHighlightState {

    private static ChunkPos centerChunk;
    private static int radius = 0;
    private static long startTime = -1;

    public static final long DURATION_MS = 2000;

    public static void start(BlockPos pos, int r) {
        centerChunk = new ChunkPos(pos);
        radius = Math.max(0, Math.min(r, 1)); // clamp to 0..1
        startTime = System.currentTimeMillis();
    }

    public static boolean isActive() {
        return centerChunk != null && System.currentTimeMillis() - startTime < DURATION_MS;
    }

    public static float alpha() {
        long elapsed = System.currentTimeMillis() - startTime;
        return 1f - Math.min(elapsed / (float) DURATION_MS, 1f);
    }

    public static ChunkPos center() {
        return centerChunk;
    }

    public static int radius() {
        return radius;
    }

    public static List<ChunkPos> chunks() {
        List<ChunkPos> list = new ArrayList<>();
        if (centerChunk == null)
            return list;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                list.add(new ChunkPos(centerChunk.x + dx, centerChunk.z + dz));
            }
        }
        return list;
    }
}
