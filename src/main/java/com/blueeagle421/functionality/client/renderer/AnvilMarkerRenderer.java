package com.blueeagle421.functionality.client.renderer;

import com.blueeagle421.functionality.entity.custom.AnvilMarkerEntity;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

public class AnvilMarkerRenderer extends EntityRenderer<AnvilMarkerEntity> {

    private static final double RENDER_DISTANCE = 3.0D; // blocks
    private static final double RENDER_DISTANCE_SQ = RENDER_DISTANCE * RENDER_DISTANCE;

    public AnvilMarkerRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(AnvilMarkerEntity entity, float entityYaw, float partialTicks, PoseStack pose,
            MultiBufferSource buffer, int packedLight) {

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !player.isCrouching())
            return;

        double distSq = this.entityRenderDispatcher.distanceToSqr(entity);
        if (distSq > RENDER_DISTANCE_SQ)
            return;

        int repairs = entity.getFreeRepairs();
        Component name = Component.literal("Free repairs: " + repairs);

        this.renderNameTag(entity, name, pose, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(AnvilMarkerEntity entity) {
        return InventoryMenu.BLOCK_ATLAS; // unused
    }
}
