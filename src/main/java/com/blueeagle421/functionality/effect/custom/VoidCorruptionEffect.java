package com.blueeagle421.functionality.effect.custom;

import java.util.Collections;
import java.util.List;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class VoidCorruptionEffect extends MobEffect {

    public static final int POST_VOID_DURATION = 400;

    public VoidCorruptionEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration == 1;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide)
            return;

        DamageSource source = entity.damageSources().magic();
        entity.hurt(source, Float.MAX_VALUE);
    }
}
