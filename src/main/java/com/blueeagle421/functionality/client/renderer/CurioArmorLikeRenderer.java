package com.blueeagle421.functionality.client.renderer;

import com.blueeagle421.functionality.client.ModModelLayers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class CurioArmorLikeRenderer implements ICurioRenderer.HumanoidRender {

    private final HumanoidModel<LivingEntity> model;

    public CurioArmorLikeRenderer() {
        var models = Minecraft.getInstance().getEntityModels();
        this.model = new HumanoidModel<>(models.bakeLayer(ModModelLayers.CURIO_ARMOR));
    }

    @Override
    public HumanoidModel<LivingEntity> getModel(ItemStack stack, SlotContext slotContext) {
        return model;
    }

    @SuppressWarnings("removal")
    @Override
    public ResourceLocation getModelTexture(ItemStack stack, SlotContext slotContext) {
        Item item = stack.getItem();

        String res = item.getArmorTexture(stack, slotContext.entity(), EquipmentSlot.HEAD, null);
        return new ResourceLocation(res);
    }

    @Override
    public void renderModel(
            ItemStack stack,
            SlotContext slotContext,
            com.mojang.blaze3d.vertex.PoseStack poseStack,
            RenderLayerParent<LivingEntity, net.minecraft.client.model.EntityModel<LivingEntity>> renderLayerParent,
            MultiBufferSource buffer,
            int light) {

        poseStack.pushPose();

        ICurioRenderer.renderModel(
                this.getModel(stack, slotContext),
                this.getModelTexture(stack, slotContext),
                poseStack,
                buffer,
                light,
                stack.hasFoil() ? RenderType.armorEntityGlint() : null);

        poseStack.popPose();
    }

    public class CurioModel {

        public static LayerDefinition createLayer() {
            final float INFLATE_BY = 0.075f;

            CubeDeformation deformation = new CubeDeformation(1F + INFLATE_BY);

            MeshDefinition mesh = HumanoidModel.createMesh(deformation, 0.0F);
            return LayerDefinition.create(mesh, 64, 32);
        }
    }
}
