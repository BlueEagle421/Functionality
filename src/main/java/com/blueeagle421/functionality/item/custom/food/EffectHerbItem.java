package com.blueeagle421.functionality.item.custom.food;

import java.util.List;

import javax.annotation.Nullable;

import com.blueeagle421.functionality.item.custom.TooltipItem;
import com.blueeagle421.functionality.utils.TooltipUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public abstract class EffectHerbItem extends TooltipItem {

    public static final String HERB_KEY = "Herb";
    public static final String DURATION_KEY = "Duration";
    public static final int DEFAULT_DURATION = 400;

    public EffectHerbItem(Properties pProperties) {
        super(pProperties);
    }

    public abstract MobEffect getEffect();

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        ItemStack result = super.finishUsingItem(stack, level, user);

        if (level.isClientSide)
            return result;

        int duration = DEFAULT_DURATION;

        CompoundTag tag = stack.getTagElement(HERB_KEY);
        if (tag != null && tag.contains(DURATION_KEY, 99)) {
            duration = tag.getInt(DURATION_KEY);
        }

        MobEffectInstance instance = new MobEffectInstance(getEffect(), duration, 0, false, true);
        user.addEffect(instance);

        return result;
    }

    public ItemStack getDefaultInstance() {
        ItemStack itemstack = new ItemStack(this);
        setDuration(itemstack, DEFAULT_DURATION);
        return itemstack;
    }

    public static void setDuration(ItemStack pStack, int pEffectDuration) {
        pStack.getOrCreateTagElement(HERB_KEY).putInt(DURATION_KEY, pEffectDuration);
    }

    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {

        TooltipUtils.addFormattedTooltip(pStack, pTooltip);

        CompoundTag tag = pStack.getTagElement(HERB_KEY);
        if (tag != null) {
            if (tag.contains(DURATION_KEY, 99)) {
                pTooltip.add(
                        Component.translatable("tooltip.nbt.functionality.duration")
                                .append(CommonComponents.SPACE)
                                .append(Component.literal(StringUtil.formatTickDuration(tag.getInt(
                                        DURATION_KEY))))
                                .withStyle(ChatFormatting.GRAY));
            }
        }
    }

}
