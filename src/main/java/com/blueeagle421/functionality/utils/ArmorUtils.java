package com.blueeagle421.functionality.utils;

import com.blueeagle421.functionality.item.custom.InfernoGearItem;
import com.blueeagle421.functionality.item.custom.ObsidianFinsItem;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public class ArmorUtils {

    public static boolean hasInfernoGear(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity))
            return false;

        var chestItem = livingEntity.getItemBySlot(EquipmentSlot.CHEST).getItem();
        return chestItem instanceof InfernoGearItem;
    }

    public static boolean hasObsidianFins(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity))
            return false;

        var feetItem = livingEntity.getItemBySlot(EquipmentSlot.FEET).getItem();
        return feetItem instanceof ObsidianFinsItem;
    }

}
