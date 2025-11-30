package com.blueeagle421.functionality.item.custom;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.blueeagle421.functionality.utils.TooltipUtils;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class GlowTorchItem extends StandingAndWallBlockItem {

    public GlowTorchItem(Block pBlock, Block pWallBlock, Properties pProperties, Direction pAttachmentDirection) {
        super(pBlock, pWallBlock, pProperties, pAttachmentDirection);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip,
            TooltipFlag isAdvanced) {

        TooltipUtils.addFormattedTooltip(stack, tooltip);

        super.appendHoverText(stack, level, tooltip, isAdvanced);
    }

}
