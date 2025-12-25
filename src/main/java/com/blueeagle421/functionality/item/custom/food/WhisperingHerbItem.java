package com.blueeagle421.functionality.item.custom.food;

import com.blueeagle421.functionality.effect.ModEffects;
import net.minecraft.world.effect.MobEffect;

public class WhisperingHerbItem extends EffectHerbItem {
    public WhisperingHerbItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public MobEffect getEffect() {
        return ModEffects.CALMNESS.get();
    }
}
