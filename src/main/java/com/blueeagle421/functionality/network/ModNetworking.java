package com.blueeagle421.functionality.network;

import com.blueeagle421.functionality.FunctionalityMod;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

@SuppressWarnings("removal")
public class ModNetworking {

    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(FunctionalityMod.MOD_ID, "network"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals);

    private static int packetId = 0;

    public static void register() {
        CHANNEL.registerMessage(
                packetId++,
                ChunkHighlightPacket.class,
                ChunkHighlightPacket::encode,
                ChunkHighlightPacket::decode,
                ChunkHighlightPacket::handle);
    }

    public static void sendChunkHighlight(Player player, BlockPos pos, int radius) {
        if (player instanceof ServerPlayer sp) {
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp),
                    new ChunkHighlightPacket(pos, radius));
        }
    }
}