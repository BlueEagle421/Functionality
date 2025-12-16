package com.blueeagle421.functionality.network;

import com.blueeagle421.functionality.FunctionalityMod;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Collections;

@Mod.EventBusSubscriber(modid = FunctionalityMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChunkHighlightRenderEvent {

    private static final float PIXEL_WIDTH = 1.5f; // screen-space thickness in pixels
    private static final int R = 178, G = 132, B = 215;

    // Alpha to use for client-toggled "always render" chunks. Tweak this if you
    // prefer.
    private static final float CLIENT_ALPHA = 1.0f;

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS)
            return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.getCameraEntity() == null)
            return;

        // Collect server-controlled chunks only if the server highlight state is active
        List<ChunkPos> serverChunks = ChunkHighlightState.isActive() ? ChunkHighlightState.chunks()
                : Collections.emptyList();
        Set<ChunkPos> chunkSet = new HashSet<>(serverChunks);

        // Build a set of client-toggled chunk positions and add them to the render set
        Set<ChunkPos> clientChunkSet = ChunkHighlightClient.getAll();
        chunkSet.addAll(clientChunkSet);

        // If there are no chunks from either source, skip rendering
        if (chunkSet.isEmpty())
            return;

        Vec3 camPos = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(-camPos.x, -camPos.y, -camPos.z);

        float serverAlpha = ChunkHighlightState.isActive() ? ChunkHighlightState.alpha() : 1.0f;
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        var builder = buffer.getBuffer(RenderType.debugQuads());

        double minY = mc.level.getMinBuildHeight();
        double maxY = mc.level.getMaxBuildHeight();

        // For each chunk in the list, draw its outer bounding cube + grid lines
        for (ChunkPos c : List.copyOf(chunkSet)) {
            // Decide alpha for this chunk: client-toggled uses CLIENT_ALPHA, server uses
            // serverAlpha
            final float chunkAlpha = clientChunkSet.contains(c) ? CLIENT_ALPHA : serverAlpha;

            // neighbor checks in chunk coordinates (use chunkSet which contains both
            // sources)
            boolean hasNegX = chunkSet.contains(new ChunkPos(c.x - 1, c.z));
            boolean hasPosX = chunkSet.contains(new ChunkPos(c.x + 1, c.z));
            boolean hasNegZ = chunkSet.contains(new ChunkPos(c.x, c.z - 1));
            boolean hasPosZ = chunkSet.contains(new ChunkPos(c.x, c.z + 1));

            double minX = c.getMinBlockX();
            double minZ = c.getMinBlockZ();
            double maxX = c.getMaxBlockX() + 1;
            double maxZ = c.getMaxBlockZ() + 1;

            // Helper to draw a thick line as a camera-facing quad (captures chunkAlpha)
            java.util.function.BiConsumer<Vec3, Vec3> addThickLine = (start, end) -> {
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
                float a = chunkAlpha;
                float r = R / 255f, g = G / 255f, b = B / 255f;

                builder.vertex(mat, (float) a1.x, (float) a1.y, (float) a1.z).color(r, g, b, a).endVertex();
                builder.vertex(mat, (float) b1.x, (float) b1.y, (float) b1.z).color(r, g, b, a).endVertex();
                builder.vertex(mat, (float) b2.x, (float) b2.y, (float) b2.z).color(r, g, b, a).endVertex();
                builder.vertex(mat, (float) a2.x, (float) a2.y, (float) a2.z).color(r, g, b, a).endVertex();
            };

            // 12 edges of the chunk — skip edges that lie on an internal plane (neighbor
            // exists)
            // bottom face edges (y = minY)
            if (!hasNegZ)
                addThickLine.accept(new Vec3(minX, minY, minZ), new Vec3(maxX, minY, minZ)); // z = minZ
            if (!hasPosX)
                addThickLine.accept(new Vec3(maxX, minY, minZ), new Vec3(maxX, minY, maxZ)); // x = maxX
            if (!hasPosZ)
                addThickLine.accept(new Vec3(maxX, minY, maxZ), new Vec3(minX, minY, maxZ)); // z = maxZ
            if (!hasNegX)
                addThickLine.accept(new Vec3(minX, minY, maxZ), new Vec3(minX, minY, minZ)); // x = minX

            // top face edges (y = maxY)
            if (!hasNegZ)
                addThickLine.accept(new Vec3(minX, maxY, minZ), new Vec3(maxX, maxY, minZ)); // z = minZ
            if (!hasPosX)
                addThickLine.accept(new Vec3(maxX, maxY, minZ), new Vec3(maxX, maxY, maxZ)); // x = maxX
            if (!hasPosZ)
                addThickLine.accept(new Vec3(maxX, maxY, maxZ), new Vec3(minX, maxY, maxZ)); // z = maxZ
            if (!hasNegX)
                addThickLine.accept(new Vec3(minX, maxY, maxZ), new Vec3(minX, maxY, minZ)); // x = minX

            // vertical edges — skip if either plane that contains the edge is internal
            if (!(hasNegX || hasNegZ))
                addThickLine.accept(new Vec3(minX, minY, minZ), new Vec3(minX, maxY, minZ)); // corner minX,minZ
            if (!(hasPosX || hasNegZ))
                addThickLine.accept(new Vec3(maxX, minY, minZ), new Vec3(maxX, maxY, minZ)); // corner maxX,minZ
            if (!(hasPosX || hasPosZ))
                addThickLine.accept(new Vec3(maxX, minY, maxZ), new Vec3(maxX, maxY, maxZ)); // corner maxX,maxZ
            if (!(hasNegX || hasPosZ))
                addThickLine.accept(new Vec3(minX, minY, maxZ), new Vec3(minX, maxY, maxZ)); // corner minX,maxZ

            double step = 1.0;

            // Draw vertical grid lines along z and x edges for this chunk
            if (!hasNegX) {
                for (double z = minZ; z <= maxZ; z += step) {
                    addThickLine.accept(new Vec3(minX, minY, z), new Vec3(minX, maxY, z));
                }
            }
            if (!hasPosX) {
                for (double z = minZ; z <= maxZ; z += step) {
                    addThickLine.accept(new Vec3(maxX, minY, z), new Vec3(maxX, maxY, z));
                }
            }

            // vertical lines at z = minZ and z = maxZ for each x (these are on the
            // z-planes)
            if (!hasNegZ) {
                for (double x = minX; x <= maxX; x += step) {
                    addThickLine.accept(new Vec3(x, minY, minZ), new Vec3(x, maxY, minZ));
                }
            }
            if (!hasPosZ) {
                for (double x = minX; x <= maxX; x += step) {
                    addThickLine.accept(new Vec3(x, minY, maxZ), new Vec3(x, maxY, maxZ));
                }
            }

            // Draw horizontal grid lines within this chunk (y levels)
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
