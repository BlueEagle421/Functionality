package com.blueeagle421.functionality.item.custom;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.blueeagle421.functionality.entity.custom.AmethystArrowEntity;
import com.blueeagle421.functionality.utils.TooltipUtils;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class AmethystArrowItem extends ArrowItem {

    public AmethystArrowItem(Properties properties) {
        super(properties);
    }

    @Override
    public AbstractArrow createArrow(Level world, ItemStack stack, LivingEntity shooter) {
        return new AmethystArrowEntity(world, shooter);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip,
            TooltipFlag isAdvanced) {

        TooltipUtils.addFormattedTooltip(stack, tooltip);

        super.appendHoverText(stack, level, tooltip, isAdvanced);
    }
}
