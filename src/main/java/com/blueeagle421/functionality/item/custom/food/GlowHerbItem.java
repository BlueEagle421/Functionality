package com.blueeagle421.functionality.item.custom.food;

import com.blueeagle421.functionality.effect.ModEffects;
import net.minecraft.world.effect.MobEffect;

public class GlowHerbItem extends EffectHerbItem {
    public GlowHerbItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public MobEffect getEffect() {
        return ModEffects.GLOW_BLESSING.get();
    }

    @Override
    public int getDefaultDuration() {
        return 1100;
    }
}
