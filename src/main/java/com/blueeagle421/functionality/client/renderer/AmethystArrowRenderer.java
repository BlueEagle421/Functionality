package com.blueeagle421.functionality.client.renderer;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.entity.custom.AmethystArrowEntity;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class AmethystArrowRenderer extends ArrowRenderer<AmethystArrowEntity> {

    @SuppressWarnings("removal")
    public static final ResourceLocation TEXTURE = new ResourceLocation(FunctionalityMod.MOD_ID,
            "textures/entity/amethyst_arrow.png");

    public AmethystArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(AmethystArrowEntity entity) {
        return TEXTURE;
    }
}