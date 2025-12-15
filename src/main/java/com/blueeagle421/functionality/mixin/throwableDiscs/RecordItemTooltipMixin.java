package com.blueeagle421.functionality.mixin.throwableDiscs;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.config.subcategories.features.ThrowableDiscs;

import java.util.List;

@Mixin(RecordItem.class)
public class RecordItemTooltipMixin {

    @Inject(method = "appendHoverText", at = @At("TAIL"))
    private void onAppendHoverText(
            ItemStack stack,
            @Nullable Level level,
            List<Component> tooltip,
            TooltipFlag flag,
            CallbackInfo ci) {

        // category

        tooltip.add(Component.literal("").withStyle(ChatFormatting.GRAY));
        tooltip.add(
                Component.translatable("attribute.category.functionality.when_thrown")
                        .withStyle(ChatFormatting.GRAY));

        // damage

        String damageString = String.format("%.0f", config().getDamage(stack));
        Component damageAmount = Component.literal(damageString);
        Component damageName = Component.translatable("attribute.name.generic.attack_damage");

        Component damageLine = Component
                .translatable("attribute.modifier.equals.0", damageAmount, damageName)
                .withStyle(ChatFormatting.DARK_GREEN);

        tooltip.add(CommonComponents.space().append(damageLine));

        // range

        String rangeString = String.format("%.0f", config().getRange(stack));
        Component rangeAmount = Component.literal(rangeString);
        Component rangeName = Component.translatable("attribute.name.functionality.throw_range");

        Component rangeLine = Component
                .translatable("attribute.modifier.equals.0", rangeAmount, rangeName)
                .withStyle(ChatFormatting.DARK_GREEN);

        tooltip.add(CommonComponents.space().append(rangeLine));
    }

    private static ThrowableDiscs config() {
        return FunctionalityConfig.COMMON.features.throwableDiscs;
    }
}