package com.blueeagle421.functionality.block.custom;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class OvergrownLichenBlock extends BloomLichenBlock {

    public OvergrownLichenBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {

        ItemStack held = player.getItemInHand(hand);
        int ticksPerHarvest = config().ticksDurationPerHarvest.get();

        if (held.getItem() == Items.GLASS_BOTTLE) {

            if (!level.isClientSide) {
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }

                ItemStack hastePotion = createHastePotion(ticksPerHarvest, 1);
                giveItemToPlayerOrDrop(player, hastePotion);
                playPotionSound(level, pos);
                destroySelf(level, pos);
            }

            if (level.isClientSide) {
                spawnHarvestParticlesClient(level, hit);
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (held.getItem() == Items.POTION) {
            List<MobEffectInstance> effects = PotionUtils.getMobEffects(held);

            for (MobEffectInstance inst : effects) {
                if (inst.getEffect() == MobEffects.DIG_SPEED && inst.getAmplifier() == 0) {

                    if (!level.isClientSide) {
                        amplifyHastePotion(held, inst);
                        playPotionSound(level, pos);
                        destroySelf(level, pos);
                    }

                    if (level.isClientSide) {
                        spawnHarvestParticlesClient(level, hit);
                    }

                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }

        return super.use(state, level, pos, player, hand, hit);
    }

    private void amplifyHastePotion(ItemStack stack, MobEffectInstance haste) {
        List<MobEffectInstance> newEffects = new ArrayList<>();

        for (MobEffectInstance inst : PotionUtils.getMobEffects(stack)) {
            if (inst.getEffect() == MobEffects.DIG_SPEED) {
                newEffects.add(new MobEffectInstance(
                        MobEffects.DIG_SPEED,
                        inst.getDuration(),
                        1,
                        inst.isAmbient(),
                        inst.isVisible()));
            } else {
                newEffects.add(inst);
            }
        }

        stack.getOrCreateTag().remove(PotionUtils.TAG_CUSTOM_POTION_EFFECTS);
        PotionUtils.setPotion(stack, net.minecraft.world.item.alchemy.Potions.WATER);
        PotionUtils.setCustomEffects(stack, newEffects);

        stack.setHoverName(Component.translatable(HASTE_POTION_KEY));
    }
}