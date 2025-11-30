package com.blueeagle421.functionality.entity.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;

public class AmethystArrowEntity extends Arrow {
    public AmethystArrowEntity(EntityType<? extends Arrow> type, Level world) {
        super(EntityType.ARROW, world);
    }

    public AmethystArrowEntity(Level world, LivingEntity shooter) {
        super(world, shooter);
    }
}