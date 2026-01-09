package com.blueeagle421.functionality.item.custom.equipment;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.client.InfernoGearHumanoidModel;
import com.blueeagle421.functionality.compat.CurioCompat;
import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.config.subcategories.items.InfernoGear;
import com.blueeagle421.functionality.utils.TooltipUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class InfernoGearItem extends Item implements ICurioItem {

    private static final int MARKER_AMPLIFIER = 127;
    private static final String LAVA_TICKS = "LavaTicks";

    public InfernoGearItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private InfernoGearHumanoidModel<LivingEntity> armorModel;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(
                    LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {

                if (armorModel == null) {
                    ModelPart part = Minecraft.getInstance()
                            .getEntityModels()
                            .bakeLayer(InfernoGearHumanoidModel.LAYER_LOCATION);
                    armorModel = new InfernoGearHumanoidModel<>(part);
                }

                return armorModel;
            }
        });
    }

    @Override
    public String getArmorTexture(
            ItemStack stack,
            Entity entity,
            EquipmentSlot slot,
            String type) {

        return FunctionalityMod.MOD_ID + ":textures/models/armor/inferno_gear.png";
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {

        if (slotContext.entity().level().isClientSide)
            return;

        addEffect(slotContext);

        LivingEntity entity = slotContext.entity();
        if (entity.isInLava())
            CurioCompat.Utils.durabilityTick(entity, stack, config().lastsForTicks.get(), LAVA_TICKS);
    }

    private static void addEffect(SlotContext slotContext) {
        MobEffectInstance current = slotContext.entity().getEffect(MobEffects.FIRE_RESISTANCE);

        if (current != null)
            return;

        MobEffectInstance infiniteFR = new MobEffectInstance(MobEffects.FIRE_RESISTANCE,
                MobEffectInstance.INFINITE_DURATION, MARKER_AMPLIFIER, true, false, true);

        slotContext.entity().addEffect(infiniteFR);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (!newStack.isEmpty())
            return;

        if (slotContext.entity().level().isClientSide)
            return;

        MobEffectInstance fr = slotContext.entity().getEffect(MobEffects.FIRE_RESISTANCE);

        if (InfernoGearItem.isEffectFromGear(fr))
            slotContext.entity().removeEffect(MobEffects.FIRE_RESISTANCE);
    }

    @Override
    @Nonnull
    public ICurio.SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
        return new ICurio.SoundInfo(SoundEvents.ARMOR_EQUIP_DIAMOND, 1.0f, 1.0f);
    }

    private static Boolean isEffectFromGear(MobEffectInstance mobEffectInstance) {
        if (mobEffectInstance == null)
            return false;

        if (mobEffectInstance.getEffect() != MobEffects.FIRE_RESISTANCE)
            return false;

        if (mobEffectInstance.getAmplifier() != MARKER_AMPLIFIER)
            return false;

        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip,
            TooltipFlag isAdvanced) {

        TooltipUtils.addFormattedTooltip(stack, tooltip);

        super.appendHoverText(stack, level, tooltip, isAdvanced);
    }

    private static InfernoGear config() {
        return FunctionalityConfig.COMMON.items.infernoGear;
    }
}
