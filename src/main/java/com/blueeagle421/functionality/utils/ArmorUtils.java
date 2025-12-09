package com.blueeagle421.functionality.utils;

import com.blueeagle421.functionality.item.custom.InfernoGearItem;
import com.blueeagle421.functionality.item.custom.ObsidianFinsItem;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

public class ArmorUtils {

    public static boolean hasInfernoGear(Entity entity) {
        return hasEquipment(entity, EquipmentSlot.CHEST, InfernoGearItem.class);
    }

    public static boolean hasObsidianFins(Entity entity) {
        return hasEquipment(entity, EquipmentSlot.FEET, ObsidianFinsItem.class);
    }

    public static boolean hasEquipment(Entity entity, EquipmentSlot slot, Class<? extends Item> itemType) {
        if (!(entity instanceof LivingEntity livingEntity))
            return false;

        var item = livingEntity.getItemBySlot(slot).getItem();
        return itemType.isInstance(item);
    }

}
