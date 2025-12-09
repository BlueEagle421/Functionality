package com.blueeagle421.functionality.entity.custom;

import com.blueeagle421.functionality.entity.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class AnvilMarkerEntity extends Entity {
    private static final String NBT_BLOCKPOS = "AttachedPos";
    private static final String NBT_FREE_REPAIRS = "FreeRepairs";

    private static final EntityDataAccessor<Integer> DATA_FREE_REPAIRS = SynchedEntityData
            .defineId(AnvilMarkerEntity.class, EntityDataSerializers.INT);

    private BlockPos attachedPos;
    private double yOffset = 0.9d;

    public AnvilMarkerEntity(EntityType<? extends AnvilMarkerEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setInvulnerable(true);
        this.setSilent(true);
        this.setNoGravity(true);
        this.setCustomNameVisible(true);
        this.noCulling = true;
    }

    // Server-side constructor
    public AnvilMarkerEntity(Level level, BlockPos pos, double yOffset, int initialRepairs) {
        this(ModEntities.ANVIL_MARKER.get(), level);
        this.attachedPos = pos.immutable();
        this.yOffset = yOffset;
        this.entityData.set(DATA_FREE_REPAIRS, initialRepairs);
        updatePosition();
        this.setCustomNameVisible(true);
        this.noCulling = true;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_FREE_REPAIRS, 0);
    }

    private void updatePosition() {
        if (attachedPos == null)
            return;

        this.setPos(attachedPos.getX() + 0.5, attachedPos.getY() + yOffset, attachedPos.getZ() + 0.5);
    }

    public int getFreeRepairs() {
        return this.entityData.get(DATA_FREE_REPAIRS);
    }

    public void setFreeRepairs(int amount) {
        this.entityData.set(DATA_FREE_REPAIRS, amount);
    }

    public void incrementFreeRepairs(int delta) {
        setFreeRepairs(getFreeRepairs() + delta);
    }

    public BlockPos getAttachedPos() {
        return attachedPos;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        if (attachedPos != null)
            nbt.putLong(NBT_BLOCKPOS, attachedPos.asLong());
        nbt.putInt(NBT_FREE_REPAIRS, getFreeRepairs());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        if (nbt.contains(NBT_BLOCKPOS))
            attachedPos = BlockPos.of(nbt.getLong(NBT_BLOCKPOS));
        if (nbt.contains(NBT_FREE_REPAIRS))
            this.entityData.set(DATA_FREE_REPAIRS, nbt.getInt(NBT_FREE_REPAIRS));

    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void tick() {
        super.tick();

        this.setDeltaMovement(Vec3.ZERO);

        if (!this.level().isClientSide) {

            if (attachedPos == null || !this.level().getBlockState(attachedPos).is(Blocks.ANVIL)) {
                dropCopperBlocks();
                this.remove(RemovalReason.DISCARDED);
                return;
            }

            updatePosition();
        }

    }

    private void dropCopperBlocks() {
        if (!this.level().isClientSide) {
            int count = getFreeRepairs();
            for (int i = 0; i < count; i++) {
                this.spawnAtLocation(Blocks.COPPER_BLOCK);
            }
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }
}
