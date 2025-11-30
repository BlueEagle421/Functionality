package com.blueeagle421.functionality.client;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.entity.custom.ObsidianBoatEntity;
import com.google.common.collect.ImmutableMap;

import com.mojang.datafixers.util.Pair;

import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;

import java.util.Map;
import java.util.stream.Stream;

public class ObsidianBoatRenderer extends BoatRenderer {
    private final Map<ObsidianBoatEntity.Type, Pair<ResourceLocation, ListModel<Boat>>> boatResources;

    @SuppressWarnings("removal")
    public ObsidianBoatRenderer(EntityRendererProvider.Context pContext, boolean pChestBoat) {
        super(pContext, pChestBoat);
        this.boatResources = Stream.of(ObsidianBoatEntity.Type.values())
                .collect(ImmutableMap.toImmutableMap(type -> type,
                        type -> Pair.of(
                                new ResourceLocation(FunctionalityMod.MOD_ID, getTextureLocation(type, pChestBoat)),
                                this.createBoatModel(pContext, type, pChestBoat))));
    }

    private static String getTextureLocation(ObsidianBoatEntity.Type pType, boolean pChestBoat) {
        return "textures/entity/boat/" + pType.getName() + ".png";
    }

    private ListModel<Boat> createBoatModel(EntityRendererProvider.Context pContext, ObsidianBoatEntity.Type pType,
            boolean pChestBoat) {
        ModelLayerLocation modellayerlocation = ObsidianBoatRenderer.createBoatModelName(pType);
        ModelPart modelpart = pContext.bakeLayer(modellayerlocation);
        return pChestBoat ? new ChestBoatModel(modelpart) : new BoatModel(modelpart);
    }

    public static ModelLayerLocation createBoatModelName(ObsidianBoatEntity.Type pType) {
        return createLocation("boat/" + pType.getName(), "main");
    }

    @SuppressWarnings("removal")
    private static ModelLayerLocation createLocation(String pPath, String pModel) {
        return new ModelLayerLocation(new ResourceLocation(FunctionalityMod.MOD_ID, pPath), pModel);
    }

    public Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(Boat boat) {
        if (boat instanceof ObsidianBoatEntity modBoat)
            return this.boatResources.get(modBoat.getModVariant());
        else
            return null;

    }
}