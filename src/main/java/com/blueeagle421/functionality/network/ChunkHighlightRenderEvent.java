package com.blueeagle421.functionality.network;

import com.blueeagle421.functionality.FunctionalityMod;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(modid = FunctionalityMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChunkHighlightRenderEvent {

    private static final float PIXEL_WIDTH = 1.5f; // screen-space thickness in pixels
    private static final int R = 178, G = 132, B = 215;

    // alpha ping-pong
    private static final float MIN_ALPHA = 0.5f;
    private static final float MAX_ALPHA = 1f;
    private static final double PERIOD_SECONDS = 1;

    // (1 block)
    private static final double SEGMENT_HEIGHT = 1.0;

    private static final double VERTICAL_FADE_DISTANCE = 32.0;

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS)
            return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.getCameraEntity() == null)
            return;

        Set<ChunkPos> chunkSet = new HashSet<>();

        Set<ChunkPos> clientChunkSet = ChunkHighlightClient.getAll();
        chunkSet.addAll(clientChunkSet);

        if (chunkSet.isEmpty())
            return;

        double t = System.currentTimeMillis() / 1000.0; // seconds
        double s = Math.sin(2.0 * Math.PI * t / PERIOD_SECONDS); // -1..1
        float alpha = (float) (MIN_ALPHA + (MAX_ALPHA - MIN_ALPHA) * (0.5 * (1.0 + s)));

        Vec3 camPos = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(-camPos.x, -camPos.y, -camPos.z);

        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        var builder = buffer.getBuffer(RenderType.debugQuads());

        double minY = mc.level.getMinBuildHeight();
        double maxY = mc.level.getMaxBuildHeight();

        for (ChunkPos c : List.copyOf(chunkSet)) {

            // neighbor checks in chunk coords
            boolean hasNegX = chunkSet.contains(new ChunkPos(c.x - 1, c.z));
            boolean hasPosX = chunkSet.contains(new ChunkPos(c.x + 1, c.z));
            boolean hasNegZ = chunkSet.contains(new ChunkPos(c.x, c.z - 1));
            boolean hasPosZ = chunkSet.contains(new ChunkPos(c.x, c.z + 1));

            double minX = c.getMinBlockX();
            double minZ = c.getMinBlockZ();
            double maxX = c.getMaxBlockX() + 1;
            double maxZ = c.getMaxBlockZ() + 1;

            // draw a thick line as a camera-facing quad
            java.util.function.BiConsumer<Vec3, Vec3> addThickLine = (start, end) -> {
                double centerY = (start.y + end.y) * 0.5;
                double dy = Math.abs(centerY - camPos.y);
                float fadeFactor = (float) Math.max(0.0, 1.0 - dy / VERTICAL_FADE_DISTANCE);
                float alphaLocal = alpha * fadeFactor;
                if (alphaLocal <= 0.005f) // skip nearly invisible
                    return;

                Vec3 mid = start.add(end).scale(0.5);
                double dist = mid.distanceTo(camPos);

                double fov = mc.options.fov().get();
                int screenH = mc.getWindow().getHeight();
                double worldWidth = PIXEL_WIDTH * (2.0 * Math.tan(Math.toRadians(fov * 0.5))) * dist / screenH;
                float half = (float) (worldWidth * 0.5);

                Vec3 dir = end.subtract(start);
                double len = dir.length();
                if (len == 0)
                    return;
                dir = dir.scale(1.0 / len);

                Vec3 camLook = Vec3.directionFromRotation(
                        mc.gameRenderer.getMainCamera().getXRot(),
                        mc.gameRenderer.getMainCamera().getYRot());
                Vec3 side = dir.cross(camLook).normalize().scale(half);

                Vec3 a1 = start.subtract(side);
                Vec3 a2 = start.add(side);
                Vec3 b1 = end.subtract(side);
                Vec3 b2 = end.add(side);

                var mat = poseStack.last().pose();
                float r = R / 255f, g = G / 255f, b = B / 255f;

                builder.vertex(mat, (float) a1.x, (float) a1.y, (float) a1.z).color(r, g, b, alphaLocal).endVertex();
                builder.vertex(mat, (float) b1.x, (float) b1.y, (float) b1.z).color(r, g, b, alphaLocal).endVertex();
                builder.vertex(mat, (float) b2.x, (float) b2.y, (float) b2.z).color(r, g, b, alphaLocal).endVertex();
                builder.vertex(mat, (float) a2.x, (float) a2.y, (float) a2.z).color(r, g, b, alphaLocal).endVertex();
            };

            // 12 edges of the chunk
            if (!hasNegZ)
                addThickLine.accept(new Vec3(minX, minY, minZ), new Vec3(maxX, minY, minZ));
            if (!hasPosX)
                addThickLine.accept(new Vec3(maxX, minY, minZ), new Vec3(maxX, minY, maxZ));
            if (!hasPosZ)
                addThickLine.accept(new Vec3(maxX, minY, maxZ), new Vec3(minX, minY, maxZ));
            if (!hasNegX)
                addThickLine.accept(new Vec3(minX, minY, maxZ), new Vec3(minX, minY, minZ));

            // top face edges
            if (!hasNegZ)
                addThickLine.accept(new Vec3(minX, maxY, minZ), new Vec3(maxX, maxY, minZ));
            if (!hasPosX)
                addThickLine.accept(new Vec3(maxX, maxY, minZ), new Vec3(maxX, maxY, maxZ));
            if (!hasPosZ)
                addThickLine.accept(new Vec3(maxX, maxY, maxZ), new Vec3(minX, maxY, maxZ));
            if (!hasNegX)
                addThickLine.accept(new Vec3(minX, maxY, maxZ), new Vec3(minX, maxY, minZ));

            // vertical edges
            if (!(hasNegX || hasNegZ)) {
                for (double y = Math.floor(minY); y < maxY; y += SEGMENT_HEIGHT) {
                    double y0 = y;
                    double y1 = Math.min(maxY, y + SEGMENT_HEIGHT);
                    addThickLine.accept(new Vec3(minX, y0, minZ), new Vec3(minX, y1, minZ));
                }
            }
            if (!(hasPosX || hasNegZ)) {
                for (double y = Math.floor(minY); y < maxY; y += SEGMENT_HEIGHT) {
                    double y0 = y;
                    double y1 = Math.min(maxY, y + SEGMENT_HEIGHT);
                    addThickLine.accept(new Vec3(maxX, y0, minZ), new Vec3(maxX, y1, minZ));
                }
            }
            if (!(hasPosX || hasPosZ)) {
                for (double y = Math.floor(minY); y < maxY; y += SEGMENT_HEIGHT) {
                    double y0 = y;
                    double y1 = Math.min(maxY, y + SEGMENT_HEIGHT);
                    addThickLine.accept(new Vec3(maxX, y0, maxZ), new Vec3(maxX, y1, maxZ));
                }
            }
            if (!(hasNegX || hasPosZ)) {
                for (double y = Math.floor(minY); y < maxY; y += SEGMENT_HEIGHT) {
                    double y0 = y;
                    double y1 = Math.min(maxY, y + SEGMENT_HEIGHT);
                    addThickLine.accept(new Vec3(minX, y0, maxZ), new Vec3(minX, y1, maxZ));
                }
            }

            double step = 1.0;

            // vertical grid lines along edges
            if (!hasNegX) {
                for (double z = minZ; z <= maxZ; z += step) {
                    for (double y = Math.floor(minY); y < maxY; y += SEGMENT_HEIGHT) {
                        double y0 = y;
                        double y1 = Math.min(maxY, y + SEGMENT_HEIGHT);
                        addThickLine.accept(new Vec3(minX, y0, z), new Vec3(minX, y1, z));
                    }
                }
            }
            if (!hasPosX) {
                for (double z = minZ; z <= maxZ; z += step) {
                    for (double y = Math.floor(minY); y < maxY; y += SEGMENT_HEIGHT) {
                        double y0 = y;
                        double y1 = Math.min(maxY, y + SEGMENT_HEIGHT);
                        addThickLine.accept(new Vec3(maxX, y0, z), new Vec3(maxX, y1, z));
                    }
                }
            }

            // more vertical grid lines
            if (!hasNegZ) {
                for (double x = minX; x <= maxX; x += step) {
                    for (double y = Math.floor(minY); y < maxY; y += SEGMENT_HEIGHT) {
                        double y0 = y;
                        double y1 = Math.min(maxY, y + SEGMENT_HEIGHT);
                        addThickLine.accept(new Vec3(x, y0, minZ), new Vec3(x, y1, minZ));
                    }
                }
            }
            if (!hasPosZ) {
                for (double x = minX; x <= maxX; x += step) {
                    for (double y = Math.floor(minY); y < maxY; y += SEGMENT_HEIGHT) {
                        double y0 = y;
                        double y1 = Math.min(maxY, y + SEGMENT_HEIGHT);
                        addThickLine.accept(new Vec3(x, y0, maxZ), new Vec3(x, y1, maxZ));
                    }
                }
            }

            // horizontal grid lines
            for (double y = minY; y <= maxY; y += step) {
                if (!hasNegZ)
                    addThickLine.accept(new Vec3(minX, y, minZ), new Vec3(maxX, y, minZ));
                if (!hasPosZ)
                    addThickLine.accept(new Vec3(minX, y, maxZ), new Vec3(maxX, y, maxZ));

                if (!hasNegX)
                    addThickLine.accept(new Vec3(minX, y, minZ), new Vec3(minX, y, maxZ));
                if (!hasPosX)
                    addThickLine.accept(new Vec3(maxX, y, minZ), new Vec3(maxX, y, maxZ));
            }
        }

        buffer.endBatch(RenderType.debugQuads());
        poseStack.popPose();
    }
}
