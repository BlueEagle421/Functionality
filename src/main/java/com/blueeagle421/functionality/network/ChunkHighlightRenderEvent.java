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

@Mod.EventBusSubscriber(modid = FunctionalityMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChunkHighlightRenderEvent {

    private static final float PIXEL_WIDTH = 1.5f; // screen-space thickness in pixels
    private static final int R = 178, G = 132, B = 215;

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS)
            return;
        if (!ChunkHighlightState.isActive())
            return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.getCameraEntity() == null)
            return;

        ChunkPos chunk = ChunkHighlightState.chunk();
        if (chunk == null)
            return;

        Vec3 camPos = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(-camPos.x, -camPos.y, -camPos.z);

        float alpha = ChunkHighlightState.alpha();
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        var builder = buffer.getBuffer(RenderType.debugQuads());

        double minX = chunk.getMinBlockX();
        double minZ = chunk.getMinBlockZ();
        double maxX = chunk.getMaxBlockX() + 1;
        double maxZ = chunk.getMaxBlockZ() + 1;
        int minY = mc.level.getMinBuildHeight();
        int maxY = mc.level.getMaxBuildHeight();

        // Helper to draw a thick line as a camera-facing quad
        java.util.function.BiConsumer<Vec3, Vec3> addThickLine = (start, end) -> {
            Vec3 mid = start.add(end).scale(0.5);
            double dist = mid.distanceTo(camPos);

            double fov = mc.options.fov().get();
            int screenH = mc.getWindow().getHeight();
            double worldWidth = PIXEL_WIDTH * (2.0 * Math.tan(Math.toRadians(fov * 0.5))) * dist / screenH;
            float half = (float) (worldWidth * 0.5);

            Vec3 dir = end.subtract(start).normalize();
            Vec3 camLook = Vec3.directionFromRotation(
                    mc.gameRenderer.getMainCamera().getXRot(),
                    mc.gameRenderer.getMainCamera().getYRot());
            Vec3 side = dir.cross(camLook).normalize().scale(half);

            Vec3 a1 = start.subtract(side);
            Vec3 a2 = start.add(side);
            Vec3 b1 = end.subtract(side);
            Vec3 b2 = end.add(side);

            var mat = poseStack.last().pose();
            float a = alpha;
            float r = R / 255f, g = G / 255f, b = B / 255f;

            builder.vertex(mat, (float) a1.x, (float) a1.y, (float) a1.z).color(r, g, b, a).endVertex();
            builder.vertex(mat, (float) b1.x, (float) b1.y, (float) b1.z).color(r, g, b, a).endVertex();
            builder.vertex(mat, (float) b2.x, (float) b2.y, (float) b2.z).color(r, g, b, a).endVertex();
            builder.vertex(mat, (float) a2.x, (float) a2.y, (float) a2.z).color(r, g, b, a).endVertex();
        };

        // 12 edges of the chunk
        addThickLine.accept(new Vec3(minX, minY, minZ), new Vec3(maxX, minY, minZ));
        addThickLine.accept(new Vec3(maxX, minY, minZ), new Vec3(maxX, minY, maxZ));
        addThickLine.accept(new Vec3(maxX, minY, maxZ), new Vec3(minX, minY, maxZ));
        addThickLine.accept(new Vec3(minX, minY, maxZ), new Vec3(minX, minY, minZ));

        addThickLine.accept(new Vec3(minX, maxY, minZ), new Vec3(maxX, maxY, minZ));
        addThickLine.accept(new Vec3(maxX, maxY, minZ), new Vec3(maxX, maxY, maxZ));
        addThickLine.accept(new Vec3(maxX, maxY, maxZ), new Vec3(minX, maxY, maxZ));
        addThickLine.accept(new Vec3(minX, maxY, maxZ), new Vec3(minX, maxY, minZ));

        addThickLine.accept(new Vec3(minX, minY, minZ), new Vec3(minX, maxY, minZ));
        addThickLine.accept(new Vec3(maxX, minY, minZ), new Vec3(maxX, maxY, minZ));
        addThickLine.accept(new Vec3(maxX, minY, maxZ), new Vec3(maxX, maxY, maxZ));
        addThickLine.accept(new Vec3(minX, minY, maxZ), new Vec3(minX, maxY, maxZ));

        double step = 1.0;

        // Draw vertical grid lines
        for (double z = minZ; z <= maxZ; z += step) {
            addThickLine.accept(new Vec3(minX, minY, z), new Vec3(minX, maxY, z));
            addThickLine.accept(new Vec3(maxX, minY, z), new Vec3(maxX, maxY, z));
        }
        for (double x = minX; x <= maxX; x += step) {
            addThickLine.accept(new Vec3(x, minY, minZ), new Vec3(x, maxY, minZ));
            addThickLine.accept(new Vec3(x, minY, maxZ), new Vec3(x, maxY, maxZ));
        }

        // Draw horizontal grid lines
        for (double y = minY; y <= maxY; y += step) {
            addThickLine.accept(new Vec3(minX, y, minZ), new Vec3(minX, y, maxZ));
            addThickLine.accept(new Vec3(maxX, y, minZ), new Vec3(maxX, y, maxZ));
            addThickLine.accept(new Vec3(minX, y, minZ), new Vec3(maxX, y, minZ));
            addThickLine.accept(new Vec3(minX, y, maxZ), new Vec3(maxX, y, maxZ));
        }

        buffer.endBatch(RenderType.debugQuads());
        poseStack.popPose();
    }
}
