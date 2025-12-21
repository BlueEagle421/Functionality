package com.blueeagle421.functionality.block.custom;

import java.util.ArrayList;
import java.util.List;

import com.blueeagle421.functionality.particle.ModParticles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.GlowLichenBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BloomLichenBlock extends GlowLichenBlock {

    private static final int TEN_SECONDS_TICKS = 20 * 10;

    public BloomLichenBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult hit) {

        ItemStack held = player.getItemInHand(hand);

        if (held.getItem() == Items.GLASS_BOTTLE) {

            if (!level.isClientSide) {
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }

                ItemStack hastePotion = createHastePotion();
                giveItemToPlayerOrDrop(player, hastePotion);
                potionHarvested(level, pos);
            }

            if (level.isClientSide) {
                spawnHarvestParticlesClient(level, hit);
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (held.getItem() == Items.POTION) {
            boolean changed = addHasteDurationToPotion(held, TEN_SECONDS_TICKS);
            if (changed) {
                if (!level.isClientSide) {
                    potionHarvested(level, pos);
                }

                if (level.isClientSide) {
                    spawnHarvestParticlesClient(level, hit);
                }

                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return InteractionResult.PASS;
    }

    private ItemStack createHastePotion() {
        ItemStack stack = new ItemStack(Items.POTION);

        PotionUtils.setPotion(stack, Potions.WATER);

        List<MobEffectInstance> effects = new ArrayList<>();
        effects.add(new MobEffectInstance(MobEffects.DIG_SPEED, TEN_SECONDS_TICKS, 0, false, true));

        writeEffectsToStack(stack, effects);
        setPotionBaseAndColor(stack, effects);

        stack.setHoverName(Component.translatable("item.functionality.potion.effect.haste"));

        return stack;
    }

    private boolean addHasteDurationToPotion(ItemStack potionStack, int additionalTicks) {
        List<MobEffectInstance> current = PotionUtils.getMobEffects(potionStack);
        boolean hadHaste = false;
        List<MobEffectInstance> newEffects = new ArrayList<>(current.size());

        for (MobEffectInstance inst : current) {
            if (inst.getEffect() == MobEffects.DIG_SPEED) {
                hadHaste = true;
                MobEffectInstance bumped = new MobEffectInstance(
                        inst.getEffect(),
                        inst.getDuration() + additionalTicks,
                        inst.getAmplifier(),
                        inst.isAmbient(),
                        inst.isVisible());
                newEffects.add(bumped);
            } else {
                MobEffectInstance copy = new MobEffectInstance(
                        inst.getEffect(),
                        inst.getDuration(),
                        inst.getAmplifier(),
                        inst.isAmbient(),
                        inst.isVisible());
                newEffects.add(copy);
            }
        }

        if (!hadHaste) {
            return false;
        }

        writeEffectsToStack(potionStack, newEffects);
        setPotionBaseAndColor(potionStack, newEffects);

        potionStack.setHoverName(Component.translatable("item.functionality.potion.effect.haste"));

        return true;
    }

    private void writeEffectsToStack(ItemStack stack, List<MobEffectInstance> effects) {
        CompoundTag tag = stack.getOrCreateTag();
        ListTag listTag = new ListTag();
        for (MobEffectInstance inst : effects) {
            listTag.add(inst.save(new CompoundTag()));
        }
        tag.put("CustomPotionEffects", listTag);
    }

    private void setPotionBaseAndColor(ItemStack stack, List<MobEffectInstance> effects) {
        PotionUtils.setPotion(stack, Potions.WATER);

        int color = PotionUtils.getColor(effects);
        stack.getOrCreateTag().putInt("CustomPotionColor", color);
    }

    private void giveItemToPlayerOrDrop(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    private void potionHarvested(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.destroyBlock(pos, false);
    }

    private void spawnHarvestParticlesClient(Level level, BlockHitResult hit) {
        if (!level.isClientSide)
            return;

        Minecraft mc = Minecraft.getInstance();
        ParticleEngine engine = mc.particleEngine;

        int color = PotionUtils.getColor(
                List.of(new MobEffectInstance(MobEffects.DIG_SPEED, 1)));

        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;

        RandomSource random = level.random;

        double cx = hit.getLocation().x;
        double cy = hit.getLocation().y;
        double cz = hit.getLocation().z;

        for (int i = 0; i < 20; i++) {
            double dx = random.nextGaussian() * 0.15D;
            double dy = random.nextDouble() * 0.2D;
            double dz = random.nextGaussian() * 0.15D;

            Particle particle = engine.createParticle(
                    ParticleTypes.EFFECT,
                    cx,
                    cy,
                    cz,
                    dx,
                    dy,
                    dz);

            if (particle != null) {
                float brightness = 0.75F + random.nextFloat() * 0.25F;
                particle.setColor(r * brightness, g * brightness, b * brightness);
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
        if (!(level instanceof ClientLevel))
            return;
        ClientLevel client = (ClientLevel) level;

        final int ATTEMPTS = 2;
        final int RADIUS = 1;
        for (int i = 0; i < ATTEMPTS; ++i) {
            BlockPos target = pos.offset(
                    Mth.nextInt(rand, -RADIUS, RADIUS),
                    -rand.nextInt(10),
                    Mth.nextInt(rand, -RADIUS, RADIUS));

            BlockState blockstate = client.getBlockState(target);
            if (!blockstate.isCollisionShapeFullBlock(client, target)) {
                double px = target.getX() + rand.nextDouble();
                double py = target.getY() + rand.nextDouble();
                double pz = target.getZ() + rand.nextDouble();
                client.addParticle(ModParticles.BLOOM_LICHEN_AIR.get(), px, py, pz, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}