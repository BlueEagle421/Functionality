package com.blueeagle421.functionality.event;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.item.custom.InfernoGearItem;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FunctionalityMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)

public class ClientEvents {
    final static Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    public static void fogDensityEvent(ViewportEvent.RenderFog event) {
        Player player = mc.player;

        if (player == null)
            return;

        if (mc.level == null)
            return;

        var chestItem = player.getItemBySlot(EquipmentSlot.CHEST).getItem();

        if (!(chestItem instanceof InfernoGearItem))
            return;

        if (!player.isEyeInFluidType(ForgeMod.LAVA_TYPE.get()))
            return;

        event.setNearPlaneDistance(16.0f);
        event.setFarPlaneDistance(32.0f);
        event.setCanceled(true);
    }
}
