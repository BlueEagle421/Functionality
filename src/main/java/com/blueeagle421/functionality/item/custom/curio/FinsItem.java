package com.blueeagle421.functionality.item.custom.curio;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

import javax.annotation.Nonnull;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.compat.CurioCompat;
import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.config.subcategories.items.Fins;
import com.blueeagle421.functionality.item.custom.TooltipItem;

public class FinsItem extends TooltipItem implements ICurioItem {

    private static final UUID FINS_SWIM_UUID = UUID.fromString("a46b3c7a-5f6a-4c2d-9d3e-1a2b3c4d5e6f");
    private static final String SWIM_TICKS = "SwimmingTicks";

    public FinsItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return FunctionalityMod.MOD_ID + ":textures/models/armor/fins.png";
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        Entity entity = slotContext.entity();
        if (!(entity instanceof Player player))
            return;
        if (player.level().isClientSide)
            return;

        speedModifierTick(player);

        if (player.isInWater() && player.isSwimming())
            CurioCompat.Utils.durabilityTick(player, stack, config().lastsForTicks.get(), SWIM_TICKS);
    }

    private static void removeSwimModifier(Player player) {
        AttributeInstance swimAttr = player.getAttribute(ForgeMod.SWIM_SPEED.get());

        if (swimAttr != null)
            swimAttr.removeModifier(FINS_SWIM_UUID);
    }

    private static void speedModifierTick(Player player) {
        AttributeInstance swimAttr = player.getAttribute(ForgeMod.SWIM_SPEED.get());
        if (swimAttr == null)
            return;

        if (player.isInWater()) {
            double amount = config().maxSpeedMultiplier.get();

            swimAttr.removeModifier(FINS_SWIM_UUID);

            AttributeModifier modifier = new AttributeModifier(
                    FINS_SWIM_UUID,
                    "fins_swim_boost",
                    amount,
                    AttributeModifier.Operation.MULTIPLY_TOTAL);

            swimAttr.addTransientModifier(modifier);
        } else {
            swimAttr.removeModifier(FINS_SWIM_UUID);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (!newStack.isEmpty())
            return;
        Entity entity = slotContext.entity();
        if (!(entity instanceof Player player))
            return;
        if (player.level().isClientSide)
            return;

        if (stack.isEmpty() || stack.getDamageValue() >= stack.getMaxDamage())
            removeSwimModifier(player);
    }

    @Override
    @Nonnull
    public ICurio.SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
        return new ICurio.SoundInfo(SoundEvents.ARMOR_EQUIP_TURTLE, 1.0f, 1.0f);
    }

    private static Fins config() {
        return FunctionalityConfig.COMMON.items.fins;
    }
}
