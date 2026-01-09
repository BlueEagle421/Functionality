package com.blueeagle421.functionality.entity.custom;

import com.blueeagle421.functionality.entity.ModEntities;
import com.blueeagle421.functionality.item.ModItems;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AmethystArrowEntity extends AbstractArrow {

    public AmethystArrowEntity(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);
    }

    public AmethystArrowEntity(Level level, LivingEntity shooter) {
        super(ModEntities.AMETHYST_ARROW.get(), shooter, level);
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(ModItems.AMETHYST_ARROW.get());
    }
}