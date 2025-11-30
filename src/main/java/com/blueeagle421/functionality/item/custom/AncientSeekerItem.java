package com.blueeagle421.functionality.item.custom;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.blueeagle421.functionality.particle.ModParticles;
import com.blueeagle421.functionality.utils.TooltipUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class AncientSeekerItem extends Item {

    private static final int REQUIRED_LEVELS = 2;
    private static final int SEARCH_RADIUS = 24;

    public AncientSeekerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack == null)
            stack = ItemStack.EMPTY;

        if (world.isClientSide()) {
            return InteractionResultHolder.sidedSuccess(stack, true);
        }

        if (!player.isCreative() && player.experienceLevel < REQUIRED_LEVELS) {
            player.displayClientMessage(
                    Component.translatable("message.functionality.ancient_seeker.not_enough_xp", REQUIRED_LEVELS)
                            .withStyle(ChatFormatting.RED),
                    true);

            return InteractionResultHolder.fail(stack);
        }

        if (!player.isCreative())
            player.giveExperienceLevels(-REQUIRED_LEVELS);

        BlockPos playerPos = player.blockPosition();

        BlockPos nearest = findNearestDebris(world, playerPos);

        if (nearest != null && world instanceof ServerLevel serverWorld) {

            serverWorld.playSound(
                    null,
                    player.getX(), player.getY(), player.getZ(),
                    net.minecraft.sounds.SoundEvents.SOUL_ESCAPE,
                    net.minecraft.sounds.SoundSource.PLAYERS,
                    1f,
                    1.0f);

            serverWorld.playSound(
                    null,
                    player.getX(), player.getY(), player.getZ(),
                    net.minecraft.sounds.SoundEvents.SCULK_BLOCK_SPREAD,
                    net.minecraft.sounds.SoundSource.PLAYERS,
                    1f,
                    1.0f);

            spawnParticles(serverWorld, nearest);

        } else {
            world.playSound(
                    null,
                    player.getX(), player.getY(), player.getZ(),
                    net.minecraft.sounds.SoundEvents.WARDEN_LISTENING_ANGRY,
                    net.minecraft.sounds.SoundSource.PLAYERS,
                    0.6f,
                    1.0f);
        }

        stack.shrink(1);

        return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
    }

    private BlockPos findNearestDebris(Level world, BlockPos origin) {
        BlockPos nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (int x = -SEARCH_RADIUS; x <= SEARCH_RADIUS; x++) {
            for (int y = -SEARCH_RADIUS; y <= SEARCH_RADIUS; y++) {
                for (int z = -SEARCH_RADIUS; z <= SEARCH_RADIUS; z++) {
                    BlockPos pos = origin.offset(x, y, z);
                    if (world.getBlockState(pos).is(Blocks.ANCIENT_DEBRIS)) {
                        double distance = origin.distSqr(pos);
                        if (distance < nearestDistance) {
                            nearestDistance = distance;
                            nearest = pos;
                        }
                    }
                }
            }
        }

        return nearest;
    }

    private void spawnParticles(ServerLevel serverWorld, BlockPos pos) {
        for (int i = 0; i < 50; i++) {
            double px = pos.getX() + 0.5 + (serverWorld.random.nextDouble() - 0.5);
            double py = pos.getY() + 0.5 + (serverWorld.random.nextDouble() - 0.5);
            double pz = pos.getZ() + 0.5 + (serverWorld.random.nextDouble() - 0.5);
            serverWorld.sendParticles(ModParticles.ANCIENT_SEEKER.get(), px, py, pz, 1, 0, 0, 0, 0);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip,
            TooltipFlag isAdvanced) {

        TooltipUtils.addFormattedTooltip(stack, tooltip);

        super.appendHoverText(stack, level, tooltip, isAdvanced);
    }
}
