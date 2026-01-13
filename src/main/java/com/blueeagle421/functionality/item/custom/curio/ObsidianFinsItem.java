package com.blueeagle421.functionality.item.custom.curio;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nonnull;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.compat.CurioCompat;
import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.config.subcategories.items.ObsidianFins;
import com.blueeagle421.functionality.item.custom.TooltipItem;

//lava swimming methods are in mixins (the class is important for type checks)
public class ObsidianFinsItem extends TooltipItem implements ICurioItem {

    private static final String SWIM_TICKS = "LavaSwimmingTicks";

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
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        Entity entity = slotContext.entity();
        if (!(entity instanceof Player player))
            return;
        if (player.level().isClientSide)
            return;

        if (player.isInLava() && player.isSwimming())
            CurioCompat.Utils.durabilityTick(player, stack, config().lastsForTicks.get(), SWIM_TICKS);
    }

    @Override
    @Nonnull
    public ICurio.SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
        return new ICurio.SoundInfo(SoundEvents.ARMOR_EQUIP_DIAMOND, 1.0f, 1.0f);
    }

    private static ObsidianFins config() {
        return FunctionalityConfig.COMMON.items.obsidianFins;
    }
}
