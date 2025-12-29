package com.blueeagle421.functionality.item.custom.equipment;

import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.client.InfernoGearHumanoidModel;
import com.blueeagle421.functionality.utils.TooltipUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class InfernoGearItem extends ArmorItem {

    private static final int MARKER_AMPLIFIER = 127;

    public InfernoGearItem(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private InfernoGearHumanoidModel<LivingEntity> armorModel;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(
                    LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {

                if (armorModel == null) {
                    ModelPart part = Minecraft.getInstance()
                            .getEntityModels()
                            .bakeLayer(InfernoGearHumanoidModel.LAYER_LOCATION);
                    armorModel = new InfernoGearHumanoidModel<>(part);
                }

                return armorModel;
            }
        });
    }

    @Override
    public String getArmorTexture(
            ItemStack stack,
            Entity entity,
            EquipmentSlot slot,
            String type) {

        return FunctionalityMod.MOD_ID + ":textures/models/armor/inferno_gear.png";
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {

        if (level.isClientSide)
            return;

        MobEffectInstance current = player.getEffect(MobEffects.FIRE_RESISTANCE);

        if (current != null)
            return;

        MobEffectInstance infiniteFR = new MobEffectInstance(MobEffects.FIRE_RESISTANCE,
                MobEffectInstance.INFINITE_DURATION, MARKER_AMPLIFIER, true, false, true);

        player.addEffect(infiniteFR);
    }

    public static Boolean isEffectFromGear(MobEffectInstance mobEffectInstance) {
        if (mobEffectInstance == null)
            return false;

        if (mobEffectInstance.getEffect() != MobEffects.FIRE_RESISTANCE)
            return false;

        if (mobEffectInstance.getAmplifier() != MARKER_AMPLIFIER)
            return false;

        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip,
            TooltipFlag isAdvanced) {

        TooltipUtils.addFormattedTooltip(stack, tooltip);

        super.appendHoverText(stack, level, tooltip, isAdvanced);
    }
}
