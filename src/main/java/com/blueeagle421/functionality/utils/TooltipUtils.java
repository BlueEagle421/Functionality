package com.blueeagle421.functionality.utils;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class TooltipUtils {

    public static void addFormattedTooltip(ItemStack stack, List<Component> tooltip) {
        if (stack == null || stack.isEmpty())
            return;

        var key = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (key == null)
            return;

        String registryName = key.getPath();

        String baseKey = "tooltip.functionality." + registryName;
        String detailKey = "tooltip.detailed.functionality." + registryName;

        tooltip.add(Component.translatable(baseKey).withStyle(ChatFormatting.GRAY));

        boolean hasDetail = Component.translatable(detailKey).getString() != null
                && !Component.translatable(detailKey).getString().equals(detailKey);

        if (!hasDetail) {
            return;
        }

        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable(detailKey).withStyle(ChatFormatting.WHITE));
        } else {
            tooltip.add(
                    Component.translatable(
                            "tooltip.functionality.details",
                            Component.translatable("key.sneak"))
                            .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        }
    }
}