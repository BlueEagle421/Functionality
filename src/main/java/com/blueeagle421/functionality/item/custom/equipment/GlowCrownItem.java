package com.blueeagle421.functionality.item.custom.equipment;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.effect.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GlowCrownItem extends ArmorItem {
    private static final int MARKER_AMPLIFIER = 127;

    public GlowCrownItem(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }

    @Override
    public String getArmorTexture(
            ItemStack stack,
            Entity entity,
            EquipmentSlot slot,
            String type) {

        return FunctionalityMod.MOD_ID + ":textures/models/armor/glow_crown.png";
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {

        if (level.isClientSide)
            return;

        MobEffectInstance current = player.getEffect(ModEffects.GLOW_BLESSING.get());

        if (current != null)
            return;

        MobEffectInstance infiniteFR = new MobEffectInstance(ModEffects.GLOW_BLESSING.get(),
                MobEffectInstance.INFINITE_DURATION, MARKER_AMPLIFIER, true, false, true);

        player.addEffect(infiniteFR);
    }

    public static Boolean isEffectFromGear(MobEffectInstance mobEffectInstance) {
        if (mobEffectInstance == null)
            return false;

        if (mobEffectInstance.getEffect() != ModEffects.GLOW_BLESSING.get())
            return false;

        if (mobEffectInstance.getAmplifier() != MARKER_AMPLIFIER)
            return false;

        return true;
    }
}
