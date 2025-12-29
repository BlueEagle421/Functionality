package com.blueeagle421.functionality.utils;

import com.blueeagle421.functionality.item.custom.equipment.UnderwaterWeaponItem;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;

public class UnderwaterUtils {

    public static ServerPlayer findPlayerAttacker(DamageSource source) {
        if (source == null)
            return null;

        Entity direct = source.getDirectEntity();
        Entity trueSource = source.getEntity();

        if (trueSource instanceof ServerPlayer p)
            return p;
        if (direct instanceof ServerPlayer p2)
            return p2;

        if (direct instanceof Projectile proj) {
            Entity owner = proj.getOwner();
            if (owner instanceof ServerPlayer p3)
                return p3;
        }

        return null;
    }

    public static UnderwaterWeaponItem findUnderwaterWeapon(Player player) {

        if (player.getMainHandItem().getItem() instanceof UnderwaterWeaponItem item)
            return item;

        if (player.getOffhandItem().getItem() instanceof UnderwaterWeaponItem item)
            return item;

        return null;
    }
}
