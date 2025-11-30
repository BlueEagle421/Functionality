package com.blueeagle421.functionality.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface LavaPatchModel {
    ModelPart lavaPatch();
}