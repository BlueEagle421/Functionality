package com.blueeagle421.functionality.utils;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class TooltipUtils {

    public static void addFormattedTooltip(ItemStack stack, List<Component> tooltip) {
        if (stack == null || stack.isEmpty())
            return;

        var key = ForgeRegistries.ITEMS.getKey(stack.getItem());
        String registryName = key != null ? key.getPath() : "unknown";

        String tooltipKey = "tooltip.functionality." + registryName;
        tooltip.add(Component.translatable(tooltipKey).withStyle(ChatFormatting.DARK_GRAY));
    }

}
