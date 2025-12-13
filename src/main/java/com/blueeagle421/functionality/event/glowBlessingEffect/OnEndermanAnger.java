package com.blueeagle421.functionality.event.glowBlessingEffect;

import com.blueeagle421.functionality.effect.ModEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.EnderManAngerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "functionality")
public class OnEndermanAnger {

    @SubscribeEvent
    public static void onEndermanAnger(EnderManAngerEvent event) {
        Player player = event.getPlayer();

        if (player != null && player.hasEffect(ModEffects.GLOW_BLESSING.get())) {
            event.setCanceled(true);
        }
    }
}
