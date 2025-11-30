package com.blueeagle421.functionality.client;

import com.blueeagle421.functionality.entity.custom.ObsidianBoatEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderBlockScreenEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "functionality", value = Dist.CLIENT)
public class ObsidianBoatClientEvents {

    @SubscribeEvent
    public static void onRenderFireOverlay(RenderBlockScreenEffectEvent event) {
        if (event.getPlayer() != null &&
                event.getPlayer().getVehicle() instanceof ObsidianBoatEntity) {

            if (event.getOverlayType() == RenderBlockScreenEffectEvent.OverlayType.FIRE) {
                event.setCanceled(true);
            }
        }
    }
}