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
    private void onAppendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag,
            CallbackInfo ci) {

        tooltip.add(Component.literal("").withStyle(ChatFormatting.GRAY));

        tooltip.add(
                Component.translatable("attribute.category.functionality.when_thrown").withStyle(ChatFormatting.GRAY));

        String damageString = String.format("%.0f", config().getDamage(stack));
        Component amountComp = Component.literal(damageString);
        Component attrNameComp = Component.translatable("attribute.name.generic.attack_damage");
        Component modifierLine = Component.translatable("attribute.modifier.equals.0", amountComp, attrNameComp)
                .withStyle(ChatFormatting.DARK_GREEN);

        tooltip.add(CommonComponents.space().append(modifierLine));
    }

    private static ThrowableDiscs config() {
        return FunctionalityConfig.COMMON.features.throwableDiscs;
    }
}