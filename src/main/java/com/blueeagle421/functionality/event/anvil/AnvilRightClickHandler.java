package com.blueeagle421.functionality.event.anvil;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.entity.custom.AnvilMarkerEntity;
import com.blueeagle421.functionality.utils.AnvilMarkerUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FunctionalityMod.MOD_ID)
public class AnvilRightClickHandler {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {

        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        ItemStack stack = player.getItemInHand(hand);

        if (!level.getBlockState(pos).is(Blocks.ANVIL))
            return;

        if (stack.getItem() != Items.COPPER_BLOCK)
            return;

        if (level.isClientSide()) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }

        if (!(level instanceof ServerLevel server))
            return;

        var found = AnvilMarkerUtils.findMatchingMarkers(server, pos);

        if (!found.isEmpty()) {
            AnvilMarkerEntity marker = found.get(0);
            marker.incrementFreeRepairs(1);
        } else {
            AnvilMarkerUtils.spawnMarker(server, pos, 0.9, 1);
        }

        if (!player.isCreative()) {
            stack.shrink(1);
        }

        spawnParticles(server, pos);

        server.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 0.65f, 1.5f);

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    private static void spawnParticles(ServerLevel server, BlockPos pos) {
        int count = 12;
        double spread = 0.25d;
        double speed = 0.08d;

        double px = pos.getX() + 0.5;
        double py = pos.getY() + 0.95;
        double pz = pos.getZ() + 0.5;

        ItemParticleOption particle = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.COPPER_BLOCK));

        server.sendParticles(particle, px, py, pz, count, spread, spread, spread, speed);
    }
}
