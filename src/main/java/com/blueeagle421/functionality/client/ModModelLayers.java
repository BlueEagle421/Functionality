package com.blueeagle421.functionality.client;

import com.blueeagle421.functionality.FunctionalityMod;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("removal")
public class ModModelLayers {

    public static final ModelLayerLocation OBSIDIAN_BOAT_LAYER = new ModelLayerLocation(
            new ResourceLocation(FunctionalityMod.MOD_ID, "boat/obsidian"), "main");

    public static final ModelLayerLocation CURIO_ARMOR = new ModelLayerLocation(
            new ResourceLocation(FunctionalityMod.MOD_ID, "curio_armor"), "main");

}