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

        handleBaseTooltip(tooltip, registryName);
        handleDetailTooltip(tooltip, registryName);
    }

    private static void handleBaseTooltip(List<Component> tooltip, String registryName) {
        String baseKey = "tooltip.functionality." + registryName;

        Component.translatable(baseKey).getString().lines()
                .forEach(line -> tooltip.add(Component.literal(line).withStyle(ChatFormatting.GRAY)));
    }

    private static void handleDetailTooltip(List<Component> tooltip, String registryName) {
        String detailKey = "tooltip.detailed.functionality." + registryName;

        String translatedDetail = Component.translatable(detailKey).getString();

        boolean hasDetail = translatedDetail != null && !translatedDetail.equals(detailKey);

        if (!hasDetail)
            return;

        if (Screen.hasShiftDown()) {
            translatedDetail.lines()
                    .forEach(line -> tooltip.add(Component.literal(line).withStyle(ChatFormatting.WHITE)));
        } else {
            tooltip.add(
                    Component.translatable(
                            "tooltip.functionality.details",
                            Component.translatable("key.sneak"))
                            .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        }
    }
}