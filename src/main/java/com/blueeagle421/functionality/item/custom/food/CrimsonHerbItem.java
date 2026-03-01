package com.blueeagle421.functionality.item.custom.food;

import com.blueeagle421.functionality.effect.ModEffects;
import net.minecraft.world.effect.MobEffect;

public class CrimsonHerbItem extends EffectHerbItem {
    public CrimsonHerbItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public MobEffect getEffect() {
        return ModEffects.CRIMSON_SHIELD.get();
    }

    @Override
    public int getDefaultDuration() {
        return 700;
    }
}
