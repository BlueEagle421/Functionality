package com.blueeagle421.functionality.item.custom.equipment;

import javax.annotation.Nonnull;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.effect.ModEffects;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class GlowCrownItem extends Item implements ICurioItem {
    private static final int MARKER_AMPLIFIER = 127;

    public GlowCrownItem(Properties pProperties) {
        super(pProperties);
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
    public void curioTick(SlotContext slotContext, ItemStack stack) {

        if (slotContext.entity().level().isClientSide)
            return;

        MobEffectInstance current = slotContext.entity().getEffect(MobEffects.FIRE_RESISTANCE);

        if (current != null)
            return;

        MobEffectInstance infiniteFR = new MobEffectInstance(MobEffects.FIRE_RESISTANCE,
                MobEffectInstance.INFINITE_DURATION, MARKER_AMPLIFIER, true, false, true);

        slotContext.entity().addEffect(infiniteFR);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (slotContext.entity().level().isClientSide)
            return;

        MobEffectInstance fr = slotContext.entity().getEffect(MobEffects.FIRE_RESISTANCE);

        if (GlowCrownItem.isEffectFromGear(fr))
            slotContext.entity().removeEffect(MobEffects.FIRE_RESISTANCE);
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

    @Override
    @Nonnull
    public ICurio.SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
        return new ICurio.SoundInfo(SoundEvents.SMALL_DRIPLEAF_PLACE, 1.0f, 1.0f);
    }
}
