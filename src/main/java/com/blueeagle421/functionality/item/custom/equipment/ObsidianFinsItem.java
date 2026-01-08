package com.blueeagle421.functionality.item.custom.equipment;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import javax.annotation.Nonnull;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.utils.TooltipUtils;

//lava swimming methods are in mixins (the class is important for type checks)
public class ObsidianFinsItem extends Item implements ICurioItem {

    public ObsidianFinsItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public String getArmorTexture(
            ItemStack stack,
            Entity entity,
            EquipmentSlot slot,
            String type) {

        return FunctionalityMod.MOD_ID + ":textures/models/armor/obsidian_fins.png";
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip,
            TooltipFlag isAdvanced) {

        TooltipUtils.addFormattedTooltip(stack, tooltip);

        super.appendHoverText(stack, level, tooltip, isAdvanced);
    }

    @Override
    @Nonnull
    public ICurio.SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
        return new ICurio.SoundInfo(SoundEvents.ARMOR_EQUIP_DIAMOND, 1.0f, 1.0f);
    }
}
