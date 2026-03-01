package com.blueeagle421.functionality.block.custom;

import java.util.ArrayList;
import java.util.List;

import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.config.subcategories.features.HastePotionHarvesting;
import com.blueeagle421.functionality.particle.ModParticles;

import net.minecraft.ChatFormatting;
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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.GlowLichenBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BloomLichenBlock extends GlowLichenBlock {

    public static final String HASTE_POTION_KEY = "item.functionality.potion.effect.haste";

    public BloomLichenBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult hit) {

        if (!config().enabled.get())
            return InteractionResult.PASS;

        ItemStack held = player.getItemInHand(hand);
        int ticksPerHarvest = config().ticksDurationPerHarvest.get();
        int maxPotionTicks = config().maxPotionDuration.get();

        if (held.getItem() == Items.GLASS_BOTTLE)
            return handleGlassBottleUse(held, player, level, pos, hand, hit, ticksPerHarvest);

        if (held.getItem() == Items.POTION)
            return handlePotionUse(held, level, pos, player, hit, ticksPerHarvest, maxPotionTicks);

        return InteractionResult.PASS;
    }

    private InteractionResult handleGlassBottleUse(ItemStack held, Player player, Level level, BlockPos pos,
            InteractionHand hand, BlockHitResult hit, int ticksPerHarvest) {

        if (!level.isClientSide) {
            if (!tryConsumeXp(player, level))
                return InteractionResult.FAIL;

            if (!player.getAbilities().instabuild)
                held.shrink(1);

            ItemStack hastePotion = createHastePotion(ticksPerHarvest, 0);
            giveItemToPlayerOrDrop(player, hastePotion);
            playPotionSound(level, pos);
            destroySelf(level, pos);
        } else {
            if (!player.isCreative() && !hasEnoughXp(player))
                return InteractionResult.FAIL;

            spawnHarvestParticlesClient(level, hit);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private InteractionResult handlePotionUse(ItemStack held, Level level, BlockPos pos, Player player,
            BlockHitResult hit, int ticksPerHarvest, int maxPotionTicks) {

        int currentHasteDuration = getCurrentHasteDuration(held);

        if (currentHasteDuration >= maxPotionTicks)
            return InteractionResult.PASS;

        if (!level.isClientSide) {
            if (!tryConsumeXp(player, level))
                return InteractionResult.FAIL;

            boolean changed = addHasteDurationToPotion(held, ticksPerHarvest, maxPotionTicks);
            if (changed) {
                playPotionSound(level, pos);
                destroySelf(level, pos);
            } else {
                return InteractionResult.FAIL;
            }
        } else {
            if (!player.isCreative() && !hasEnoughXp(player))
                return InteractionResult.FAIL;

            spawnHarvestParticlesClient(level, hit);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    protected int getExpToNextLevel(int level) {
        if (level >= 31) {
            return 9 * level - 158;
        } else if (level >= 16) {
            return 5 * level - 38;
        } else {
            return 2 * level + 7;
        }
    }

    protected int getExpFromLevel(int level) {
        if (level <= 16) {
            return level * level + 6 * level;
        } else if (level <= 31) {
            // 2.5 * level^2 - 40.5 * level + 360
            return (int) (2.5 * level * level - 40.5 * level + 360.0);
        } else {
            // 4.5 * level^2 - 162.5 * level + 2220
            return (int) (4.5 * level * level - 162.5 * level + 2220.0);
        }
    }

    protected int getPlayerTotalExperience(Player player) {
        int level = player.experienceLevel;
        float progress = player.experienceProgress; // 0.0 - 1.0
        int base = getExpFromLevel(level);
        int toNext = getExpToNextLevel(level);
        int progressPoints = Math.round(progress * toNext);
        return base + progressPoints;
    }

    protected boolean hasEnoughXp(Player p) {
        if (p.isCreative())
            return true;

        return getPlayerTotalExperience(p) >= getXpCost();
    }

    protected boolean tryConsumeXp(Player player, Level level) {
        if (player.isCreative())
            return true;

        if (level.isClientSide)
            return true;

        int xpCost = getXpCost();

        if (!hasEnoughXp(player)) {
            player.displayClientMessage(
                    Component.translatable("message.functionality.bloom_lichen.not_enough_xp", xpCost)
                            .withStyle(ChatFormatting.RED),
                    true);
            return false;
        }

        player.giveExperiencePoints(-xpCost);
        return true;
    }

    public int getXpCost() {
        return config().xpCostPerHarvest.get();
    }

    private int getCurrentHasteDuration(ItemStack potion) {
        return PotionUtils.getMobEffects(potion).stream()
                .filter(e -> e.getEffect() == MobEffects.DIG_SPEED)
                .mapToInt(MobEffectInstance::getDuration)
                .max()
                .orElse(0);
    }

    protected ItemStack createHastePotion(int durationTicks, int amplifier) {
        ItemStack stack = new ItemStack(Items.POTION);
        PotionUtils.setPotion(stack, Potions.WATER);

        List<MobEffectInstance> effects = new ArrayList<>();
        effects.add(new MobEffectInstance(MobEffects.DIG_SPEED, durationTicks, amplifier, false, true));

        writeEffectsToStack(stack, effects);
        setPotionBaseAndColor(stack, effects);

        stack.setHoverName(Component.translatable(HASTE_POTION_KEY));

        return stack;
    }

    private boolean addHasteDurationToPotion(ItemStack potionStack, int additionalTicks, int maxTicks) {
        List<MobEffectInstance> current = PotionUtils.getMobEffects(potionStack);
        boolean hadHaste = false;
        List<MobEffectInstance> newEffects = new ArrayList<>(current.size());

        for (MobEffectInstance inst : current) {
            if (inst.getEffect() == MobEffects.DIG_SPEED) {
                hadHaste = true;
                int newDuration = Math.min(inst.getDuration() + additionalTicks, maxTicks);
                MobEffectInstance bumped = new MobEffectInstance(
                        inst.getEffect(),
                        newDuration,
                        inst.getAmplifier(),
                        inst.isAmbient(),
                        inst.isVisible());
                newEffects.add(bumped);
            } else {
                newEffects.add(copyEffect(inst));
            }
        }

        if (!hadHaste)
            return false;

        writeEffectsToStack(potionStack, newEffects);
        setPotionBaseAndColor(potionStack, newEffects);

        potionStack.setHoverName(Component.translatable(HASTE_POTION_KEY));
        return true;
    }

    private MobEffectInstance copyEffect(MobEffectInstance inst) {
        return new MobEffectInstance(
                inst.getEffect(),
                inst.getDuration(),
                inst.getAmplifier(),
                inst.isAmbient(),
                inst.isVisible());
    }

    protected void writeEffectsToStack(ItemStack stack, List<MobEffectInstance> effects) {
        CompoundTag tag = stack.getOrCreateTag();
        ListTag listTag = new ListTag();

        for (MobEffectInstance inst : effects)
            listTag.add(inst.save(new CompoundTag()));

        tag.put(PotionUtils.TAG_CUSTOM_POTION_EFFECTS, listTag);
    }

    protected void setPotionBaseAndColor(ItemStack stack, List<MobEffectInstance> effects) {
        PotionUtils.setPotion(stack, Potions.WATER);
        int color = PotionUtils.getColor(effects);
        stack.getOrCreateTag().putInt(PotionUtils.TAG_CUSTOM_POTION_COLOR, color);
    }

    protected void giveItemToPlayerOrDrop(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    protected void playPotionSound(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    protected void destroySelf(Level level, BlockPos pos) {
        level.destroyBlock(pos, false);
    }

    protected void spawnHarvestParticlesClient(Level level, BlockHitResult hit) {
        if (!level.isClientSide)
            return;

        Minecraft mc = Minecraft.getInstance();
        ParticleEngine engine = mc.particleEngine;

        int color = PotionUtils.getColor(List.of(new MobEffectInstance(MobEffects.DIG_SPEED, 1)));
        float[] rgb = getRGBFromColor(color);

        RandomSource random = level.random;
        double cx = hit.getLocation().x;
        double cy = hit.getLocation().y;
        double cz = hit.getLocation().z;

        spawnEffectParticles(engine, random, cx, cy, cz, rgb);
    }

    private float[] getRGBFromColor(int color) {
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        return new float[] { r, g, b };
    }

    private void spawnEffectParticles(ParticleEngine engine, RandomSource random, double cx, double cy, double cz,
            float[] rgb) {
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
                particle.setColor(rgb[0] * brightness, rgb[1] * brightness, rgb[2] * brightness);
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {

        if (!(level instanceof ClientLevel))
            return;

        ClientLevel client = (ClientLevel) level;

        if (!shouldAnimate(rand))
            return;

        trySpawnAmbientParticles(client, pos, rand);
    }

    private boolean shouldAnimate(RandomSource rand) {
        return rand.nextInt(5) == 0;
    }

    private void trySpawnAmbientParticles(ClientLevel client, BlockPos pos, RandomSource rand) {
        final int ATTEMPTS = 2;
        final int RADIUS = 1;
        for (int i = 0; i < ATTEMPTS; ++i) {
            BlockPos target = pos.offset(
                    Mth.nextInt(rand, -RADIUS, RADIUS),
                    Mth.nextInt(rand, -RADIUS, RADIUS),
                    Mth.nextInt(rand, -RADIUS, RADIUS));

            spawnAmbientParticleIfPossible(client, rand, target);
        }
    }

    private void spawnAmbientParticleIfPossible(ClientLevel client, RandomSource rand, BlockPos target) {
        BlockState blockstate = client.getBlockState(target);
        if (!blockstate.isCollisionShapeFullBlock(client, target)) {
            double px = target.getX() + rand.nextDouble();
            double py = target.getY() + rand.nextDouble();
            double pz = target.getZ() + rand.nextDouble();
            client.addParticle(ModParticles.BLOOM_LICHEN_AIR.get(), px, py, pz, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return false;
    }

    protected static HastePotionHarvesting config() {
        return FunctionalityConfig.COMMON.features.hastePotionHarvesting;
    }
}