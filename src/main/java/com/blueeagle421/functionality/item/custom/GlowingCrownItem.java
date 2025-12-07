package com.blueeagle421.functionality.item.custom;

import com.blueeagle421.functionality.effect.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GlowingCrownItem extends ArmorItem {
    private static final int MARKER_AMPLIFIER = 127;

    public GlowingCrownItem(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {

        if (level.isClientSide)
            return;

        MobEffectInstance current = player.getEffect(ModEffects.GLOW_SHIELD.get());

        if (current != null)
            return;

        MobEffectInstance infiniteFR = new MobEffectInstance(ModEffects.GLOW_SHIELD.get(),
                MobEffectInstance.INFINITE_DURATION, MARKER_AMPLIFIER, true, false, true);

        player.addEffect(infiniteFR);
    }

    public static Boolean isEffectFromGear(MobEffectInstance mobEffectInstance) {
        if (mobEffectInstance == null)
            return false;

        if (mobEffectInstance.getEffect() != ModEffects.GLOW_SHIELD.get())
            return false;

        if (mobEffectInstance.getAmplifier() != MARKER_AMPLIFIER)
            return false;

        return true;
    }
}
