package com.blueeagle421.functionality.network;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

public class ChunkHighlightState {

    private static ChunkPos chunk;
    private static long startTime = -1;

    public static final long DURATION_MS = 2000;

    public static void start(BlockPos pos) {
        chunk = new ChunkPos(pos);
        startTime = System.currentTimeMillis();
    }

    public static boolean isActive() {
        return chunk != null && System.currentTimeMillis() - startTime < DURATION_MS;
    }

    public static float alpha() {
        long elapsed = System.currentTimeMillis() - startTime;
        return 1f - Math.min(elapsed / (float) DURATION_MS, 1f);
    }

    public static ChunkPos chunk() {
        return chunk;
    }
}