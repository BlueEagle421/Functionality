package com.blueeagle421.functionality.event;

import com.blueeagle421.functionality.FunctionalityMod;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FunctionalityMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)

public class ClientEvents {
    final static Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    public static void fogDensityEvent(ViewportEvent.RenderFog event) {
        Player player = mc.player;
        if (player == null)
            return;

        if (mc.level == null)
            return;

        BlockState state = mc.level.getBlockState(new BlockPos(player.blockPosition().above(1)));

        if (!state.is(Blocks.LAVA))
            return;

        event.setNearPlaneDistance(16.0f);
        event.setFarPlaneDistance(32.0f);
        event.setCanceled(true);
    }
}
