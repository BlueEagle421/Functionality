package com.blueeagle421.functionality.client.renderer;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.block.entity.custom.ChunkLoaderEntity;
import com.mojang.blaze3d.vertex.PoseStack;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("removal")
public class ChunkLoaderRenderer implements BlockEntityRenderer<ChunkLoaderEntity> {

    private static final Logger LOGGER = LoggerFactory.getLogger("Functionality-Renderer");

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
        if (level == null)
            return;

        if (topModel == mc.getModelManager().getMissingModel())
            return;

        BlockPos pos = be.getBlockPos();
        BlockState state = be.getBlockState();

        float time = level.getGameTime() + partialTicks;
        float rotationDeg = getRotation(level, pos, partialTicks);

        // Model + spacing
        final float modelWidthPixels = 4f;
        final float gapPixels = 1f;
        final float spacingBlocks = (modelWidthPixels + gapPixels) / 16f;

        // Wave
        final float amplitude = 1.4f / 16f;
        final float waveSpeed = 0.25f;
        final float phaseStep = 0.7f;

        BlockRenderDispatcher dispatcher = mc.getBlockRenderer();

        poseStack.pushPose();

        // poseStack.translate(0.5, 0.0, 0.5);
        // poseStack.mulPose(Axis.YP.rotationDegrees(rotationDeg));
        // poseStack.translate(-0.5, 0.0, -0.5);

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

        poseStack.popPose();
    }

    private float getRotation(Level level, BlockPos pos, float partialTicks) {
        long time = level.getGameTime();
        return (time + partialTicks) * 4f; // degrees per tick
    }
}
