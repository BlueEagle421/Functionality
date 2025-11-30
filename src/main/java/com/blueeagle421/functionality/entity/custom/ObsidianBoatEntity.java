package com.blueeagle421.functionality.entity.custom;

import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import java.util.function.IntFunction;
import com.blueeagle421.functionality.item.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ObsidianBoatEntity extends Boat {

    private static final EntityDataAccessor<Integer> DATA_ID_TYPE = SynchedEntityData.defineId(Boat.class,
            EntityDataSerializers.INT);

    public ObsidianBoatEntity(EntityType<? extends ObsidianBoatEntity> type, Level level) {
        super(type, level);

    }

    public ObsidianBoatEntity(Level level, double x, double y, double z) {
        this(com.blueeagle421.functionality.entity.ModEntities.OBSIDIAN_BOAT.get(), level);
        this.setPos(x, y, z);
    }

    private static final double LAVA_BUOYANCY_OFFSET = 0.4D;
    private static final double MAX_RISE_PER_TICK = 0.6D;
    private static final double SMOOTHING = 0.5D;

    @Override
    public void tick() {

        super.tick();

        if (this.level() == null)
            return;
        Double lavaSurface = findHighestLavaSurface();
        if (lavaSurface != null) {

            double desiredY = lavaSurface - (double) this.getBbHeight() + 0.101D + LAVA_BUOYANCY_OFFSET;
            double currentY = this.getY();
            double dy = desiredY - currentY;

            if (dy > 0.0001D) {

                double rise = dy * SMOOTHING;

                if (rise > MAX_RISE_PER_TICK)
                    rise = MAX_RISE_PER_TICK;

                if (rise < 0.01D)
                    rise = Math.min(dy, 0.01D);

                Vec3 vel = this.getDeltaMovement();
                double newVy = Math.max(vel.y, rise);
                this.setDeltaMovement(vel.x, newVy, vel.z);

                if (dy > 1.0D) {
                    double snap = Math.min(dy, MAX_RISE_PER_TICK);
                    this.setPos(this.getX(), currentY + snap, this.getZ());
                }
            } else {

            }
        }

    }

    private Double findHighestLavaSurface() {
        if (this.level() == null)
            return null;

        AABB aabb = this.getBoundingBox();

        int minX = Mth.floor(aabb.minX);
        int maxX = Mth.ceil(aabb.maxX);
        int minZ = Mth.floor(aabb.minZ);
        int maxZ = Mth.ceil(aabb.maxZ);

        int sampleX = Math.min(3, Math.max(1, maxX - minX));
        int sampleZ = Math.min(3, Math.max(1, maxZ - minZ));

        double[] xs = new double[sampleX];
        double[] zs = new double[sampleZ];
        for (int i = 0; i < sampleX; i++) {
            xs[i] = minX + (maxX - minX) * ((double) i / (sampleX - 1 == 0 ? 1 : sampleX - 1));
        }
        for (int j = 0; j < sampleZ; j++) {
            zs[j] = minZ + (maxZ - minZ) * ((double) j / (sampleZ - 1 == 0 ? 1 : sampleZ - 1));
        }

        int topY = Mth.floor(aabb.maxY) + 1;
        int bottomY = Mth.floor(aabb.minY) - 1;

        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
        Double bestSurface = null;

        for (double sx : xs) {
            for (double sz : zs) {
                int xi = Mth.floor(sx);
                int zi = Mth.floor(sz);

                for (int y = topY; y >= bottomY; y--) {
                    mpos.set(xi, y, zi);
                    FluidState fs = this.level().getFluidState(mpos);
                    if (fs.is(FluidTags.LAVA)) {
                        float frac = fs.getHeight(this.level(), mpos);
                        double surfaceY = (double) mpos.getY() + (double) frac;
                        if (bestSurface == null || surfaceY > bestSurface)
                            bestSurface = surfaceY;
                        break;
                    }
                }
            }
        }

        return bestSurface;
    }

    @Override
    public void destroy(DamageSource source) {

        if (this.level().isClientSide)
            return;

        ItemStack boatStack = new ItemStack(this.getDropItem());

        Entity passenger = source.getEntity();
        if (passenger instanceof Player player) {
            boolean inserted = player.getInventory().add(boatStack);
            if (inserted) {
                this.discard();
                return;
            }
        }

        this.spawnAtLocation(boatStack);

        this.discard();
    }

    @Override
    public Item getDropItem() {
        return switch (getModVariant()) {
            case OBSIDIAN -> ModItems.OBSIDIAN_BOAT.get();
        };
    }

    public void setVariant(Type pVariant) {
        this.entityData.set(DATA_ID_TYPE, pVariant.ordinal());
    }

    public Type getModVariant() {
        return Type.byId(this.entityData.get(DATA_ID_TYPE));
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_TYPE, Type.OBSIDIAN.ordinal());
    }

    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putString("Type", this.getModVariant().getSerializedName());
    }

    protected void readAdditionalSaveData(CompoundTag pCompound) {
        if (pCompound.contains("Type", 8)) {
            this.setVariant(Type.byName(pCompound.getString("Type")));
        }
    }

    @Override
    public boolean isUnderWater() {
        return super.isUnderWater() || this.level().getFluidState(this.blockPosition()).is(FluidTags.LAVA);
    }

    @Override
    public boolean isInWater() {
        return super.isInWater() || this.level().getFluidState(this.blockPosition()).is(FluidTags.LAVA);
    }

    @Override
    public boolean canBoatInFluid(FluidState state) {
        return state.is(FluidTags.WATER) || state.is(FluidTags.LAVA);
    }

    public static enum Type implements StringRepresentable {
        OBSIDIAN(Blocks.OBSIDIAN, "obsidian");

        private final String name;
        private final Block planks;

        @SuppressWarnings("deprecation")
        public static final StringRepresentable.EnumCodec<ObsidianBoatEntity.Type> CODEC = StringRepresentable.fromEnum(
                ObsidianBoatEntity.Type::values);
        private static final IntFunction<ObsidianBoatEntity.Type> BY_ID = ByIdMap.continuous(Enum::ordinal, values(),
                ByIdMap.OutOfBoundsStrategy.ZERO);

        private Type(Block pPlanks, String pName) {
            this.name = pName;
            this.planks = pPlanks;
        }

        public String getSerializedName() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

        public Block getPlanks() {
            return this.planks;
        }

        public String toString() {
            return this.name;
        }

        public static ObsidianBoatEntity.Type byId(int pId) {
            return BY_ID.apply(pId);
        }

        public static ObsidianBoatEntity.Type byName(String pName) {
            return CODEC.byName(pName, OBSIDIAN);
        }
    }
}
