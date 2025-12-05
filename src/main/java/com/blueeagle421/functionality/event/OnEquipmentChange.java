package com.blueeagle421.functionality.event;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.item.custom.InfernoGearItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FunctionalityMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OnEquipmentChange {

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {

        if (!(event.getEntity() instanceof Player player))
            return;

        if (player.level().isClientSide)
            return;

        if (event.getSlot() != EquipmentSlot.CHEST)
            return;

        ItemStack from = event.getFrom();
        ItemStack to = event.getTo();

        boolean removedInferno = !from.isEmpty() && from.getItem() instanceof InfernoGearItem;
        boolean stillInferno = !to.isEmpty() && to.getItem() instanceof InfernoGearItem;

        if (removedInferno && !stillInferno) {
            MobEffectInstance fr = player.getEffect(MobEffects.FIRE_RESISTANCE);

            if (InfernoGearItem.isEffectFromGear(fr))
                player.removeEffect(MobEffects.FIRE_RESISTANCE);
        }
    }

}
