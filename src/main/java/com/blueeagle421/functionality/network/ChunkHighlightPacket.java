package com.blueeagle421.functionality.network;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public record ChunkHighlightPacket(BlockPos pos, int radius) {

    public static void encode(ChunkHighlightPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeByte(msg.radius);
    }

    public static ChunkHighlightPacket decode(FriendlyByteBuf buf) {
        return new ChunkHighlightPacket(buf.readBlockPos(), buf.readByte());
    }

    public static void handle(ChunkHighlightPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ChunkHighlightState.start(msg.pos, msg.radius);
        });
        ctx.get().setPacketHandled(true);
    }
}
