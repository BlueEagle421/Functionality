package com.blueeagle421.functionality.item.custom;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.blueeagle421.functionality.utils.TooltipUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.registries.ForgeRegistries;

// durability handled in mixin
public class BottomlessWaterBucketItem extends BucketItem {

    private static final String LIQUID_PLACING_TAG = "LiquidPlacing";

    public BottomlessWaterBucketItem(Properties properties) {
        super(ForgeRegistries.FLUIDS.getDelegateOrThrow(Fluids.WATER), properties);
    }

    public static boolean isLiquidPlacing(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean(LIQUID_PLACING_TAG);
    }

    public static void toggleLiquidPlacing(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean(LIQUID_PLACING_TAG, !tag.getBoolean(LIQUID_PLACING_TAG));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                toggleLiquidPlacing(stack);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }

        if (!isLiquidPlacing(stack)) {
            return super.use(level, player, hand);
        }

        BlockHitResult hit = getPlayerPOVHitResult(
                level,
                player,
                ClipContext.Fluid.SOURCE_ONLY);

        if (hit.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(stack);
        }

        BlockPos hitPos = hit.getBlockPos();
        BlockState hitState = level.getBlockState(hitPos);

        BlockPos placePos = hitPos.relative(hit.getDirection());

        if (!level.mayInteract(player, hitPos) ||
                !player.mayUseItemAt(placePos, hit.getDirection(), stack)) {
            return InteractionResultHolder.fail(stack);
        }

        if (!hitState.getFluidState().isEmpty()) {
            if (this.emptyContents(player, level, placePos, hit, stack)) {
                player.awardStat(Stats.ITEM_USED.get(this));
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
            }
            return InteractionResultHolder.fail(stack);
        }

        return super.use(level, player, hand);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return isLiquidPlacing(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip,
            TooltipFlag isAdvanced) {

        TooltipUtils.addFormattedTooltip(stack, tooltip);

        super.appendHoverText(stack, level, tooltip, isAdvanced);
    }
}
