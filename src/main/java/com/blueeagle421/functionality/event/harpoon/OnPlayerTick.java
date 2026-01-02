package com.blueeagle421.functionality.event.harpoon;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.item.custom.equipment.UnderwaterWeaponItem;
import com.blueeagle421.functionality.utils.UnderwaterUtils;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
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

        var weaponItem = UnderwaterUtils.findUnderwaterWeapon(player);

        if (weaponItem == null) {
            removeModifier(player);
            return;
        }

        if (weaponItem.isUnderwater(player))
            applyModifier(player, weaponItem);
        else
            removeModifier(player);
    }

    private static void applyModifier(Player player, UnderwaterWeaponItem weaponItem) {
        var attr = player.getAttribute(ForgeMod.ENTITY_REACH.get());
        if (attr == null)
            return;

        // Only add if not already present
        if (attr.getModifier(UnderwaterWeaponItem.WATER_REACH_UUID) == null) {
            attr.addTransientModifier(new AttributeModifier(
                    UnderwaterWeaponItem.WATER_REACH_UUID,
                    "water_reach_bonus",
                    weaponItem.getExtraReach(),
                    AttributeModifier.Operation.ADDITION));
        }
    }

    private static void removeModifier(Player player) {
        var attr = player.getAttribute(ForgeMod.ENTITY_REACH.get());
        if (attr == null)
            return;

        var mod = attr.getModifier(UnderwaterWeaponItem.WATER_REACH_UUID);
        if (mod != null)
            attr.removeModifier(mod);
    }

}
