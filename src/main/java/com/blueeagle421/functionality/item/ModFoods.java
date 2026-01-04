package com.blueeagle421.functionality.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoods {

    private static final int TICKS_PER_SECOND = 20;
    private static final int HUNGER_DURATION = 30 * TICKS_PER_SECOND;

    public static final FoodProperties BEAR_VENISON = new FoodProperties.Builder()
            .nutrition(3).saturationMod(0.4f)
            .meat()
            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, HUNGER_DURATION, 0), 0.3f)
            .build();

    public static final FoodProperties COOKED_BEAR_VENISON = new FoodProperties.Builder()
            .nutrition(8).saturationMod(0.9f)
            .meat()
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 25 * TICKS_PER_SECOND, 0), 1f)
            .alwaysEat()
            .build();

    public static final FoodProperties CHEVON = new FoodProperties.Builder()
            .nutrition(3).saturationMod(0.3f)
            .meat()
            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, HUNGER_DURATION, 0), 0.3f)
            .build();

    public static final FoodProperties COOKED_CHEVON = new FoodProperties.Builder()
            .nutrition(5).saturationMod(0.6f)
            .meat()
            .effect(() -> new MobEffectInstance(MobEffects.JUMP, 30 * TICKS_PER_SECOND, 1), 1f)
            .alwaysEat()
            .build();

    public static final FoodProperties SNIFFON = new FoodProperties.Builder()
            .nutrition(3).saturationMod(0.4f)
            .meat()
            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, HUNGER_DURATION, 0), 0.3f)
            .build();

    public static final FoodProperties COOKED_SNIFFON = new FoodProperties.Builder()
            .nutrition(8).saturationMod(0.9f)
            .meat()
            .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 45 * TICKS_PER_SECOND, 0), 1f)
            .alwaysEat()
            .build();

    public static final FoodProperties GILDED_SNIFFON = new FoodProperties.Builder()
            .nutrition(8).saturationMod(0.9f)
            .meat()
            .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 45 * TICKS_PER_SECOND, 1), 1f)
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 45 * TICKS_PER_SECOND, 0), 1f)
            .alwaysEat()
            .build();

    public static final FoodProperties FROG_LEG = new FoodProperties.Builder()
            .nutrition(1).saturationMod(0.3f)
            .meat()
            .fast()
            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, HUNGER_DURATION, 0), 0.3f)
            .effect(() -> new MobEffectInstance(MobEffects.POISON, 30 * TICKS_PER_SECOND, 0), 1)
            .build();

    public static final FoodProperties COOKED_FROG_LEG = new FoodProperties.Builder()
            .nutrition(5).saturationMod(0.6f)
            .meat()
            .fast()
            .alwaysEat()
            .build();

    public static final FoodProperties TERRAPIN = new FoodProperties.Builder()
            .nutrition(3).saturationMod(0.4f)
            .meat()
            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, HUNGER_DURATION, 0), 0.3f)
            .effect(() -> new MobEffectInstance(MobEffects.POISON, 15 * TICKS_PER_SECOND, 0), 1f)
            .build();

    public static final FoodProperties COOKED_TERRAPIN = new FoodProperties.Builder()
            .nutrition(6).saturationMod(0.6f)
            .meat()
            .effect(() -> new MobEffectInstance(MobEffects.CONDUIT_POWER, 25 * TICKS_PER_SECOND, 0), 1f)
            .build();

    public static final FoodProperties TERRAPIN_SOUP = new FoodProperties.Builder()
            .nutrition(8).saturationMod(0.6f)
            .effect(() -> new MobEffectInstance(MobEffects.CONDUIT_POWER, 110 * TICKS_PER_SECOND, 1), 1f)
            .alwaysEat()
            .build();

    public static final FoodProperties TERRAPIN_STICK = new FoodProperties.Builder()
            .nutrition(9).saturationMod(0.6f)
            .effect(() -> new MobEffectInstance(MobEffects.CONDUIT_POWER, 55 * TICKS_PER_SECOND, 2), 1f)
            .alwaysEat()
            .build();

    public static final FoodProperties HERB = new FoodProperties.Builder()
            .nutrition(1).saturationMod(0.3f)
            .fast()
            .alwaysEat()
            .build();
}