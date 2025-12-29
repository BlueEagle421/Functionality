package com.blueeagle421.functionality.item.custom.equipment;

import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.blueeagle421.functionality.utils.TooltipUtils;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public abstract class UnderwaterWeaponItem extends SwordItem {

    public static final UUID WATER_REACH_UUID = UUID.fromString("d71b6f20-35d3-4b1a-a33d-5a139a47db31");

    public UnderwaterWeaponItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier,
            Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip,
            TooltipFlag isAdvanced) {

        TooltipUtils.addFormattedTooltip(stack, tooltip);

        super.appendHoverText(stack, level, tooltip, isAdvanced);
    }

    public abstract int getAirRegenAmount();

    public abstract boolean getCanAttractItems();

    public abstract int getExtraReach();

    public abstract boolean isUnderwater(Entity entity);
}
