package com.blueeagle421.functionality.client;

import com.blueeagle421.functionality.FunctionalityMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class InfernoGearHumanoidModel<T extends LivingEntity> extends HumanoidModel<T> {

	@SuppressWarnings("removal")
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation(FunctionalityMod.MOD_ID, "inferno_gear"), "main");

	private final ModelPart infernoGear;

	public InfernoGearHumanoidModel(ModelPart root) {
		super(root);
		this.infernoGear = root.getChild("inferno_gear");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
		PartDefinition part = mesh.getRoot();

		PartDefinition inferno_gear = part.addOrReplaceChild("inferno_gear", CubeListBuilder.create(),
				PartPose.offset(0.0F, 12.0F, 0.0F));

		PartDefinition cube_r1 = inferno_gear.addOrReplaceChild("cube_r1",
				CubeListBuilder.create().texOffs(0, 16).addBox(0.1F, 0.75F, 2.0F, 4.0F, 12.0F, 4.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -12.5F, 0.0F, 0.0F, 0.0873F, -0.0436F));

		PartDefinition cube_r2 = inferno_gear.addOrReplaceChild("cube_r2",
				CubeListBuilder.create().texOffs(0, 0).addBox(-3.9F, 0.75F, 2.0F, 4.0F, 12.0F, 4.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -12.5F, 0.0F, 0.0F, -0.0873F, 0.0436F));

		return LayerDefinition.create(mesh, 32, 32);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer,
			int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

		infernoGear.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

}
