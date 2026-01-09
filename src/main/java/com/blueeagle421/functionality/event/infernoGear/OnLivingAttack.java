package com.blueeagle421.functionality.event.infernoGear;

import com.blueeagle421.functionality.compat.CurioCompat;
import com.blueeagle421.functionality.compat.ModCompatManager;
import com.blueeagle421.functionality.item.custom.equipment.InfernoGearItem;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class OnLivingAttack {

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();

        if (!ModCompatManager.curiosPresent)
            return;

        if (event.getSource().is(DamageTypes.IN_FIRE))
            return;

        if (!event.getSource().is(DamageTypeTags.IS_FIRE))
            return;

        ItemStack stack = CurioCompat.Utils.getCurio(entity, InfernoGearItem.class);

        if (stack == null)
            return;

        CurioCompat.Utils.hurtAndBreak(entity, stack, 1);
    }
}