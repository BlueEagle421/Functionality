package com.blueeagle421.functionality.event;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.item.custom.HarpoonItem;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FunctionalityMod.MOD_ID)
public class OnPlayerTick {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;

        Player player = event.player;
        ItemStack held = player.getMainHandItem();

        if (!(held.getItem() instanceof HarpoonItem)) {
            removeModifier(player);
            return;
        }

        if (player.isInWater()) {
            applyModifier(player);
        } else {
            removeModifier(player);
        }
    }

    private static void applyModifier(Player player) {
        var attr = player.getAttribute(ForgeMod.ENTITY_REACH.get());
        if (attr == null)
            return;

        // Only add if not already present
        if (attr.getModifier(HarpoonItem.WATER_REACH_UUID) == null) {
            attr.addTransientModifier(new AttributeModifier(
                    HarpoonItem.WATER_REACH_UUID,
                    "water_reach_bonus",
                    HarpoonItem.EXTRA_REACH_UNDERWATER,
                    AttributeModifier.Operation.ADDITION));
        }
    }

    private static void removeModifier(Player player) {
        var attr = player.getAttribute(ForgeMod.ENTITY_REACH.get());
        if (attr == null)
            return;

        var mod = attr.getModifier(HarpoonItem.WATER_REACH_UUID);
        if (mod != null)
            attr.removeModifier(mod);
    }
}
