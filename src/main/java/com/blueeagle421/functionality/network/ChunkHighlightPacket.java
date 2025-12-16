package com.blueeagle421.functionality.network;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public record ChunkHighlightPacket(BlockPos pos) {

    public static void encode(ChunkHighlightPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
    }

    public static ChunkHighlightPacket decode(FriendlyByteBuf buf) {
        return new ChunkHighlightPacket(buf.readBlockPos());
    }

    public static void handle(ChunkHighlightPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ChunkHighlightState.start(msg.pos);
        });
        ctx.get().setPacketHandled(true);
    }
}
