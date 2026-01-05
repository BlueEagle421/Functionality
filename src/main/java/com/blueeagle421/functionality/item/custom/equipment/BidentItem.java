package com.blueeagle421.functionality.item.custom.equipment;

import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.config.subcategories.items.Bident;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Tier;

public class BidentItem extends UnderwaterWeaponItem {

    public BidentItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    public int getAirRegenAmount() {
        return config().airBubblesRegenAmount.get();
    }

    @Override
    public boolean getCanAttractItems() {
        return config().attractItems.get();
    }

    @Override
    public int getExtraReach() {
        return config().extraReach.get();
    }

    @Override
    public boolean isUnderwater(Entity entity) {
        return entity.isInWaterOrRain() || entity.isInFluidType();
    }

    private static Bident config() {
        return FunctionalityConfig.COMMON.items.bident;
    }

}
