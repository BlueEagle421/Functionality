package com.blueeagle421.functionality.client.renderer;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.block.entity.custom.RepairAltarEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.model.geom.ModelLayerLocation;
import org.joml.Quaternionf;

//based on end crystal renderer
@OnlyIn(Dist.CLIENT)
@SuppressWarnings("removal")
public class RepairAltarRenderer implements BlockEntityRenderer<RepairAltarEntity> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation("functionality", "repair_altar_crystal"), "main");

    private static final ResourceLocation TEXTURE = new ResourceLocation(FunctionalityMod.MOD_ID,
            "textures/entity/repair_crystal.png");

    private static final float SIN_45 = (float) Math.sin(Math.PI / 4.0D);

    private final ModelPart glass;

    public RepairAltarRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = context.bakeLayer(LAYER_LOCATION);
        this.glass = root.getChild("glass");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("glass",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-1.5F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void render(
            RepairAltarEntity tile, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight,
            int packedOverlay) {

        if (tile == null)
            return;

        long worldTime = 0L;
        if (tile.getLevel() != null)
            worldTime = tile.getLevel().getGameTime();

        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));

        poseStack.pushPose();

        poseStack.translate(0.5D, 0.5D, 0.5D);

        poseStack.pushPose();

        poseStack.scale(2.0F, 2.0F, 2.0F);
        poseStack.translate(0.0F, -0.5F, 0.0F);

        float rot = (worldTime + partialTicks) * 3.0F;
        poseStack.mulPose(Axis.YP.rotationDegrees(rot));

        float time = worldTime + partialTicks;
        float pixelBob = Mth.sin(time * 0.1F) * 0.03125F;
        poseStack.translate(0.0F, 0.9f + pixelBob, 0.0F);

        int fullBright = 0xF000F0;

        poseStack.mulPose((new Quaternionf()).setAngleAxis(((float) Math.PI / 3F), SIN_45, 0.0F, SIN_45));
        this.glass.render(poseStack, vc, fullBright, OverlayTexture.NO_OVERLAY);

        poseStack.scale(0.875F, 0.875F, 0.875F);
        poseStack.mulPose((new Quaternionf()).setAngleAxis(((float) Math.PI / 3F), SIN_45, 0.0F, SIN_45));
        poseStack.mulPose(Axis.YP.rotationDegrees(rot));
        this.glass.render(poseStack, vc, fullBright, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(RepairAltarEntity pBlockEntity) {
        return true;
    }
}
