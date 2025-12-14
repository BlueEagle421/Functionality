package com.blueeagle421.functionality.client.renderer;

import com.blueeagle421.functionality.entity.custom.ThrownDiscEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

public class ThrownDiscRenderer extends EntityRenderer<ThrownDiscEntity> {
    private final ItemRenderer itemRenderer;
    private final float scale = 1.0F;
    private static final float MIN_CAMERA_DISTANCE_SQUARED = 12.25F;

    // degrees per tick
    private static final float SPIN_SPEED = 40.0F;

    public ThrownDiscRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(ThrownDiscEntity entity, float entityYaw, float partialTicks,
            PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        if (entity.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity()
                .distanceToSqr(entity) < MIN_CAMERA_DISTANCE_SQUARED)) {

            poseStack.pushPose();
            poseStack.scale(scale, scale, scale);

            // lay flat
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));

            // frisbee spin
            float spin = (entity.tickCount + partialTicks) * SPIN_SPEED;
            poseStack.mulPose(Axis.ZN.rotationDegrees(spin));

            // took a while to get this kind of right
            final double yOffset = -0.125d;
            final double quarterPixel = 0.0625 / 4;
            poseStack.translate(quarterPixel, -quarterPixel * 8, yOffset);

            this.itemRenderer.renderStatic(
                    entity.getItem(),
                    ItemDisplayContext.GROUND,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    poseStack,
                    buffer,
                    entity.level(),
                    entity.getId());

            poseStack.popPose();
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public ResourceLocation getTextureLocation(ThrownDiscEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
