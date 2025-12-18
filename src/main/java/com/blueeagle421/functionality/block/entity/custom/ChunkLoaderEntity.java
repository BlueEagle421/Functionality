package com.blueeagle421.functionality.block.entity.custom;

import org.jetbrains.annotations.Nullable;

import com.blueeagle421.functionality.block.entity.ModBlockEntities;
import com.blueeagle421.functionality.network.ChunkHighlightClient;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ChunkLoaderEntity extends BlockEntity {

    private boolean nineChunks = false;

    public ChunkLoaderEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public ChunkLoaderEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CHUNK_LOADER.get(), pos, state);
    }

    public void toggleMode(Player player, BlockPos pos) {
        nineChunks = !nineChunks;

        if (level instanceof ServerLevel server) {
            updateTickets(server);
            setChanged();

            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public boolean isNineChunks() {
        return nineChunks;
    }

    public int radius() {
        if (nineChunks)
            return 1;

        return 0;
    }

    private void updateTickets(ServerLevel server) {
        ChunkPos center = new ChunkPos(worldPosition);

        removeTickets(server);
        int radius = nineChunks ? 1 : 0; // radius 0 = single chunk, radius 1 = 3x3
        server.getChunkSource().addRegionTicket(TicketType.FORCED, center, radius, center);
    }

    private void removeTickets(ServerLevel server) {
        ChunkPos center = new ChunkPos(worldPosition);
        int radius = nineChunks ? 1 : 0;
        server.getChunkSource().removeRegionTicket(TicketType.FORCED, center, radius, center);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        handleUpdateTag(packet.getTag());
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.load(tag);

        if (level != null && level.isClientSide) {
            ChunkHighlightClient.update(worldPosition.immutable(), this.radius());
        }
    }

    @Override
    public void load(net.minecraft.nbt.CompoundTag tag) {
        super.load(tag);
        this.nineChunks = tag.getBoolean("NineChunks");
        if (level instanceof ServerLevel server) {
            updateTickets(server);
        }
    }

    @Override
    protected void saveAdditional(net.minecraft.nbt.CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("NineChunks", nineChunks);
    }

    @Override
    public void setRemoved() {
        if (this.level != null && this.level.isClientSide) {
            ChunkHighlightClient.removeChunksForLoader(this.worldPosition.immutable());
        }
        if (!this.remove && level instanceof ServerLevel server) {
            removeTickets(server);
        }
        super.setRemoved();
    }

}
