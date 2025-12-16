package com.blueeagle421.functionality.client.renderer;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.block.entity.custom.ChunkLoaderEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

@SuppressWarnings("removal")
public class ChunkLoaderRenderer implements BlockEntityRenderer<ChunkLoaderEntity> {

    private static final ResourceLocation TOP_MODEL = new ResourceLocation(FunctionalityMod.MOD_ID,
            "block/chunk_loader_top");

    private final Minecraft mc;
    private final BakedModel topModel;

    public ChunkLoaderRenderer(BlockEntityRendererProvider.Context context) {
        this.mc = Minecraft.getInstance();
        this.topModel = mc.getModelManager().getModel(TOP_MODEL);
    }

    @Override
    public void render(ChunkLoaderEntity be, float partialTicks, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight, int packedOverlay) {

        Level level = be.getLevel();
        if (level == null || topModel == mc.getModelManager().getMissingModel())
            return;

        BlockPos pos = be.getBlockPos();
        BlockState state = be.getBlockState();
        BlockRenderDispatcher dispatcher = mc.getBlockRenderer();

        float time = level.getGameTime() + partialTicks;
        float rotationDeg = getRotation(level, pos, partialTicks);

        poseStack.pushPose();

        if (be.isNineChunks()) {

            poseStack.translate(0.5, 0.0, 0.5); // center for rotation
            poseStack.mulPose(Axis.YP.rotationDegrees(rotationDeg));
            poseStack.translate(-0.5, 0.0, -0.5);

            // 3x3 mode
            final float modelWidthPixels = 4f;
            final float gapPixels = 0.01f;
            final float spacingBlocks = (modelWidthPixels + gapPixels) / 16f;

            final float amplitude = 2f / 16f; // vertical wave
            final float waveSpeed = 0.25f;
            final float phaseStep = 0.7f;

            for (int ix = -1; ix <= 1; ix++) {
                for (int iz = -1; iz <= 1; iz++) {

                    float xOffset = ix * spacingBlocks;
                    float zOffset = iz * spacingBlocks;

                    float phase = ix * phaseStep + iz * phaseStep * 0.5f;
                    float bob = (float) Math.sin(time * waveSpeed + phase) * amplitude;

                    long seed = pos.asLong() ^ ((ix + 2L) << 8) ^ ((iz + 2L) << 16);

                    poseStack.pushPose();
                    poseStack.translate(xOffset, bob, zOffset);

                    dispatcher.getModelRenderer().tesselateBlock(
                            level,
                            topModel,
                            state,
                            pos,
                            poseStack,
                            buffer.getBuffer(RenderType.cutout()),
                            false,
                            RandomSource.create(seed),
                            seed,
                            packedOverlay,
                            ModelData.EMPTY,
                            RenderType.cutout());

                    poseStack.popPose();
                }
            }

        } else {
            // single chunk mode
            poseStack.translate(0.5, 0.0, 0.5); // center for rotation
            poseStack.mulPose(Axis.YP.rotationDegrees(rotationDeg));

            float bob = getBob(level, partialTicks);
            poseStack.translate(0.0, bob, 0.0);
            poseStack.translate(-0.5, 0.0, -0.5);

            dispatcher.getModelRenderer().tesselateBlock(
                    level,
                    topModel,
                    state,
                    pos,
                    poseStack,
                    buffer.getBuffer(RenderType.cutout()),
                    false,
                    RandomSource.create(pos.asLong()),
                    pos.asLong(),
                    packedOverlay,
                    ModelData.EMPTY,
                    RenderType.cutout());
        }

        poseStack.popPose();
    }

    private float getRotation(Level level, BlockPos pos, float partialTicks) {
        final float degrees_per_tick = 2f;
        return (level.getGameTime() + partialTicks) * degrees_per_tick;
    }

    private float getBob(Level level, float partialTicks) {
        final float frequency = 0.25f; // radians per tick
        final float amplitude = 2f / 16f; // blocks

        return (float) Math.sin((level.getGameTime() + partialTicks) * frequency) * amplitude;
    }
}
