package com.blueeagle421.functionality.entity.custom;

import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.config.subcategories.features.ThrowableDiscs;
import com.blueeagle421.functionality.sound.ModSounds;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.InteractionHand;

public class ThrownDiscEntity extends ThrowableItemProjectile {

    private static final double OUTGOING_SPEED = 1.2D;
    private static final double RETURN_SPEED = 0.9D;
    private static final double MIN_SPEED = 0.5D;

    private static final double PICKUP_DISTANCE = 0.5D;

    private Vec3 startPos;
    private boolean returning = false;

    private ItemStack discStack = ItemStack.EMPTY;

    // Slot constants: -2 => offhand, -1 => unknown, >=0 => hotbar slot index (0-8)
    private static final int OFFHAND_SLOT = -2;
    private static final int DEFAULT_SLOT = -1;

    private int returnSlot = DEFAULT_SLOT;

    private static final EntityDataAccessor<Boolean> RETURNING = SynchedEntityData.defineId(ThrownDiscEntity.class,
            EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<ItemStack> DISC_STACK = SynchedEntityData.defineId(ThrownDiscEntity.class,
            EntityDataSerializers.ITEM_STACK);

    private static final EntityDataAccessor<Integer> RETURN_SLOT = SynchedEntityData.defineId(ThrownDiscEntity.class,
            EntityDataSerializers.INT);

    public ThrownDiscEntity(EntityType<? extends ThrownDiscEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    public ThrownDiscEntity(EntityType<? extends ThrownDiscEntity> type, LivingEntity shooter, Level level) {
        super(type, shooter, level);
        this.setNoGravity(true);
        this.startPos = shooter.position();
    }

    public void setDiscStack(ItemStack stack) {
        this.discStack = stack.copy();
        this.entityData.set(DISC_STACK, stack.copy());
    }

    public ItemStack getDiscStack() {
        return discStack;
    }

    public void setReturnSlot(int slot) {
        this.returnSlot = slot;
        if (this.entityData != null)
            this.entityData.set(RETURN_SLOT, slot);
    }

    public int getReturnSlot() {
        return this.returnSlot;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DISC_STACK, ItemStack.EMPTY);
        this.entityData.define(RETURNING, false);
        this.entityData.define(RETURN_SLOT, DEFAULT_SLOT);
    }

    @Override
    protected float getGravity() {
        return 0f;
    }

    @Override
    public boolean isInWater() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public ItemStack getItem() {
        if (!this.level().isClientSide)
            return discStack;
        return entityData.get(DISC_STACK);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.MUSIC_DISC_13;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.entityData != null) {
            this.returning = this.entityData.get(RETURNING);
            this.returnSlot = this.entityData.get(RETURN_SLOT);
        }

        if (this.startPos == null)
            this.startPos = this.position();

        if (!this.returning) {
            double distance = this.position().distanceTo(this.startPos);
            if (distance >= config().maxTravelDistance.get())
                startReturn();
        } else {
            this.noPhysics = true;
            this.setNoGravity(true);

            Entity owner = this.getOwner();
            if (owner != null && owner.isAlive()) {
                Vec3 target = owner.position().add(0, owner.getEyeHeight() * 0.5, 0);
                Vec3 toOwner = target.subtract(this.position());
                double distance = toOwner.length();

                if (distance > 0.0001) {
                    double t = Math.min(distance / 5.0, 1.0);

                    double speedFactor = Math.sqrt(t);

                    double motionSpeed = Math.max(speedFactor * RETURN_SPEED, MIN_SPEED);

                    Vec3 motion = toOwner.normalize().scale(motionSpeed);

                    this.setDeltaMovement(motion);
                    this.move(MoverType.SELF, motion);
                }

                if (distance < PICKUP_DISTANCE)
                    getPickedUp(owner);
            } else
                changeToItem(this.getX(), this.getY(), this.getZ());

        }
    }

    private void getPickedUp(Entity owner) {
        if (!(owner instanceof Player player))
            return;

        ItemStack copy = this.discStack.copy();

        if (attemptReturnToOriginalSlot(player, copy)) {
            this.discard();
            return;
        }

        if (player.getInventory().add(copy)) {
            this.discard();
            player.getInventory().setChanged();
            return;
        }

        changeToItem(player.getX(), player.getY() + 0.5, player.getZ());
    }

    private boolean attemptReturnToOriginalSlot(Player player, ItemStack stackToReturn) {
        if (this.returnSlot == OFFHAND_SLOT) {
            ItemStack off = player.getItemInHand(InteractionHand.OFF_HAND);
            if (off.isEmpty()) {
                player.setItemInHand(InteractionHand.OFF_HAND, stackToReturn);
                player.getInventory().setChanged();
                return true;
            } else if (ItemStack.isSameItemSameTags(off, stackToReturn)
                    && off.getCount() + stackToReturn.getCount() <= off.getMaxStackSize()) {
                off.grow(stackToReturn.getCount());
                player.getInventory().setChanged();
                return true;
            }
            return false;
        } else if (this.returnSlot >= 0) {
            int slot = this.returnSlot;
            if (slot < player.getInventory().items.size()) {
                ItemStack slotStack = player.getInventory().items.get(slot);
                if (slotStack.isEmpty()) {
                    player.getInventory().items.set(slot, stackToReturn);
                    player.getInventory().setChanged();
                    return true;
                } else if (ItemStack.isSameItemSameTags(slotStack, stackToReturn)
                        && slotStack.getCount() + stackToReturn.getCount() <= slotStack.getMaxStackSize()) {
                    slotStack.grow(stackToReturn.getCount());
                    player.getInventory().setChanged();
                    return true;
                }
            }
            return false;
        }

        return false;
    }

    private void changeToItem(double x, double y, double z) {
        this.level().addFreshEntity(
                new net.minecraft.world.entity.item.ItemEntity(this.level(),
                        this.getX(), this.getY(), this.getZ(), this.discStack.copy()));
        this.discard();
    }

    private void startReturn() {
        this.returning = true;
        this.entityData.set(RETURNING, true);

        this.setNoGravity(true);
        this.noPhysics = true;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (this.returning)
            return;

        if (result.getEntity() != this.getOwner()) {
            DamageSource source = this.level()
                    .damageSources()
                    .indirectMagic(this, this.getOwner());

            result.getEntity().hurt(source, config().defaultDamage.get());
            float pitch = 1f + (level().random.nextFloat() - 0.5F) * 0.16F;
            playImpactSound(ModSounds.DISC_HIT.get(), result.getLocation(), pitch);

            damageDisc();
            startReturn();
        }

        super.onHitEntity(result);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (this.returning)
            return;

        spawnDustParticles(result);
        BlockState blockstate = this.level().getBlockState(result.getBlockPos());
        playImpactSound(blockstate.getSoundType().getHitSound(), result.getLocation(), 1f);
        float pitch = 1f + (level().random.nextFloat() - 0.5F) * 0.16F;
        playImpactSound(ModSounds.DISC_HIT.get(), result.getLocation(), pitch);

        damageDisc();
        startReturn();
        super.onHitBlock(result);
    }

    private boolean damageDisc() {
        if (this.discStack.isEmpty() || !this.discStack.isDamageableItem())
            return false;

        this.discStack.hurtAndBreak(1, (LivingEntity) this.getOwner(), stack -> {
            breakDisc(this.position());
        });

        return this.discStack.isEmpty();
    }

    private void breakDisc(Vec3 pos) {
        if (!level().isClientSide) {
            level().playSound(
                    null,
                    pos.x, pos.y, pos.z,
                    net.minecraft.sounds.SoundEvents.ITEM_BREAK,
                    net.minecraft.sounds.SoundSource.PLAYERS,
                    1.0F,
                    1.0F);
        }

        this.discard();
    }

    private void playImpactSound(SoundEvent sound, Vec3 pos, float pitch) {
        if (level().isClientSide)
            return;

        level().playSound(null, pos.x, pos.y, pos.z,
                sound,
                net.minecraft.sounds.SoundSource.PLAYERS,
                1F, pitch);
    }

    private void spawnDustParticles(BlockHitResult hitResult) {

        Vec3 hitPos = hitResult.getLocation();
        Level level = level();
        BlockState state = level.getBlockState(hitResult.getBlockPos());

        if (state.isAir())
            return;

        int particleCount = level.getRandom().nextInt(26, 32);

        Direction face = hitResult.getDirection();
        Vec3 normal = Vec3.atLowerCornerOf(face.getNormal());

        for (int i = 0; i < particleCount; i++) {

            double a = (level.getRandom().nextDouble() - 0.5) * 0.2;
            double b = (level.getRandom().nextDouble() - 0.5) * 0.2;

            Vec3 spawnOffset = switch (face.getAxis()) {
                case X -> new Vec3(0, a, b);
                case Y -> new Vec3(a, 0, b);
                case Z -> new Vec3(a, b, 0);
            };

            Vec3 spawnPos = hitPos
                    .add(spawnOffset)
                    .add(normal.scale(0.01)); // to avoid clipping

            double speed = level.getRandom().nextDouble() * 18.0;

            Vec3 randomVel = new Vec3(
                    (level.getRandom().nextDouble() - 0.5) * 1.0,
                    (level.getRandom().nextDouble() - 0.5) * 1.0,
                    (level.getRandom().nextDouble() - 0.5) * 1.0);

            Vec3 velocity = normal.scale(speed).add(randomVel);

            level.addParticle(
                    new BlockParticleOption(ParticleTypes.BLOCK, state),
                    spawnPos.x, spawnPos.y, spawnPos.z,
                    velocity.x, velocity.y, velocity.z);
        }
    }

    @Override
    public void push(Entity entity) {
        if (!this.returning)
            super.push(entity);
    }

    private static ThrowableDiscs config() {
        return FunctionalityConfig.COMMON.features.throwableDiscs;
    }
}
