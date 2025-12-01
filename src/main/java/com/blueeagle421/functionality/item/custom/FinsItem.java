package com.blueeagle421.functionality.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.blueeagle421.functionality.utils.TooltipUtils;

public class FinsItem extends ArmorItem {
    public FinsItem(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }

    private static final UUID FINS_SWIM_UUID = UUID.fromString("a46b3c7a-5f6a-4c2d-9d3e-1a2b3c4d5e6f");

    private static final double MAX_MULTIPLIER = 1.5D;

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (level.isClientSide)
            return;

        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        boolean wearing = !boots.isEmpty() && boots.getItem() == this;

        AttributeInstance swimAttr = player.getAttribute(ForgeMod.SWIM_SPEED.get());
        if (swimAttr == null)
            return;

        boolean inWater = player.isInWater();
        if (wearing && inWater) {

            double levelEquivalent = 3.0D;
            double fraction = Math.min(3.0D, levelEquivalent) / 3.0D;
            double amount = MAX_MULTIPLIER * fraction;

            AttributeModifier modifier = new AttributeModifier(FINS_SWIM_UUID, "fins_swim_boost", amount,
                    AttributeModifier.Operation.MULTIPLY_TOTAL);

            if (!swimAttr.hasModifier(modifier)) {
                swimAttr.getModifiers().stream()
                        .filter(m -> m.getId().equals(FINS_SWIM_UUID))
                        .findFirst()
                        .ifPresent(swimAttr::removeModifier);

                swimAttr.addTransientModifier(modifier);
            }
        } else {
            swimAttr.getModifiers().stream()
                    .filter(m -> m.getId().equals(FINS_SWIM_UUID))
                    .findFirst()
                    .ifPresent(swimAttr::removeModifier);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip,
            TooltipFlag isAdvanced) {

        TooltipUtils.addFormattedTooltip(stack, tooltip);

        super.appendHoverText(stack, level, tooltip, isAdvanced);
    }
}
