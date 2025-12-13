package com.blueeagle421.functionality.item.custom;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.config.subcategories.items.AncientSeeker;
import com.blueeagle421.functionality.particle.ModParticles;
import com.blueeagle421.functionality.utils.TooltipUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class AncientSeekerItem extends Item {

    public AncientSeekerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack == null)
            stack = ItemStack.EMPTY;

        if (world.isClientSide())
            return InteractionResultHolder.sidedSuccess(stack, true);

        if (!player.isCreative() && player.experienceLevel < config().levelsCost.get()) {
            player.displayClientMessage(
                    Component
                            .translatable("message.functionality.ancient_seeker.not_enough_xp",
                                    config().levelsCost.get())
                            .withStyle(ChatFormatting.RED),
                    true);

            return InteractionResultHolder.fail(stack);
        }

        if (!player.isCreative())
            player.giveExperienceLevels(-config().levelsCost.get());

        BlockPos playerPos = player.blockPosition();

        BlockPos nearest = findNearestDebris(world, playerPos);

        if (nearest != null && world instanceof ServerLevel server)
            searchSucceeded(server, player, nearest);
        else
            searchFailed(world, player);

        stack.shrink(1);

        return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
    }

    private void searchSucceeded(ServerLevel server, Player player, BlockPos foundPos) {
        // cool sound #1
        server.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 1f, 1.0f);

        // cool sound #2
        server.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.PLAYERS, 1f, 1.0f);

        // cool sound #3
        server.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 1f, 1.0f);

        // cool particles
        spawnParticles(server, foundPos);
    }

    private void searchFailed(Level level, Player player) {
        // just fail sound
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.WARDEN_LISTENING_ANGRY, SoundSource.PLAYERS, 0.6f, 1.0f);
    }

    private BlockPos findNearestDebris(Level world, BlockPos origin) {
        BlockPos nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        int radius = config().searchRadius.get();

        // holy this code is NESTED
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
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

    private void spawnParticles(ServerLevel server, BlockPos pos) {
        int particlesCount = 50;
        float blockHalf = 0.5f;

        for (int i = 0; i < particlesCount; i++) {
            double px = pos.getX() + blockHalf + (server.random.nextDouble() - blockHalf);
            double py = pos.getY() + blockHalf + (server.random.nextDouble() - blockHalf);
            double pz = pos.getZ() + blockHalf + (server.random.nextDouble() - blockHalf);
            server.sendParticles(ModParticles.ANCIENT_SEEKER.get(), px, py, pz, 1, 0, 0, 0, 0);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip,
            TooltipFlag isAdvanced) {

        TooltipUtils.addFormattedTooltip(stack, tooltip);

        super.appendHoverText(stack, level, tooltip, isAdvanced);
    }

    private AncientSeeker config() {
        return FunctionalityConfig.COMMON.items.ancientSeeker;
    }
}
