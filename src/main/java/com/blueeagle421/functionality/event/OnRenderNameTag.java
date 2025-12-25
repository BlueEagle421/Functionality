package com.blueeagle421.functionality.event;

import com.blueeagle421.functionality.effect.ModEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class OnRenderNameTag {

    @SubscribeEvent
    public static void onRenderNameTag(RenderNameTagEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof Player player))
            return;

        if (!player.hasEffect(ModEffects.CALMNESS.get()))
            return;

        event.setCanceled(true);
    }
}
