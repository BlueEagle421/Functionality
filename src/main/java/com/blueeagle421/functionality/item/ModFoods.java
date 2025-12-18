package com.blueeagle421.functionality.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoods {

        public static final FoodProperties BEAR_VENISON = new FoodProperties.Builder()
                        .nutrition(3).saturationMod(0.4f)
                        .meat()
                        .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3f)
                        .build();

        public static final FoodProperties COOKED_BEAR_VENISON = new FoodProperties.Builder()
                        .nutrition(8).saturationMod(0.9f)
                        .meat()
                        .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 2400, 0), 1f)
                        .alwaysEat()
                        .build();

        public static final FoodProperties CHEVON = new FoodProperties.Builder()
                        .nutrition(3).saturationMod(0.3f)
                        .meat()
                        .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3f)
                        .build();

        public static final FoodProperties SNIFFON = new FoodProperties.Builder()
                        .nutrition(3).saturationMod(0.4f)
                        .meat()
                        .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3f)
                        .build();

        public static final FoodProperties COOKED_SNIFFON = new FoodProperties.Builder()
                        .nutrition(8).saturationMod(0.9f)
                        .meat()
                        .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 1200, 0), 1f)
                        .alwaysEat()
                        .build();

        public static final FoodProperties GILDED_SNIFFON = new FoodProperties.Builder()
                        .nutrition(8).saturationMod(0.9f)
                        .meat()
                        .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 2400, 0), 1f)
                        .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 600, 0), 1f)
                        .alwaysEat()
                        .build();

        public static final FoodProperties COOKED_CHEVON = new FoodProperties.Builder()
                        .nutrition(5).saturationMod(0.6f)
                        .meat()
                        .effect(() -> new MobEffectInstance(MobEffects.JUMP, 1200, 0), 1f)
                        .alwaysEat()
                        .build();

        public static final FoodProperties FROG_LEG = new FoodProperties.Builder()
                        .nutrition(1).saturationMod(0.3f)
                        .meat()
                        .fast()
                        .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3f)
                        .effect(() -> new MobEffectInstance(MobEffects.POISON, 600, 0), 1)
                        .build();

        public static final FoodProperties COOKED_FROG_LEG = new FoodProperties.Builder()
                        .nutrition(5).saturationMod(0.6f)
                        .meat()
                        .fast()
                        .build();

        public static final FoodProperties TERRAPIN = new FoodProperties.Builder()
                        .nutrition(3).saturationMod(0.4f)
                        .meat()
                        .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3f)
                        .effect(() -> new MobEffectInstance(MobEffects.POISON, 300, 0), 1f)
                        .build();

        public static final FoodProperties TERRAPIN_SOUP = new FoodProperties.Builder()
                        .nutrition(8).saturationMod(0.6f)
                        .effect(() -> new MobEffectInstance(MobEffects.CONDUIT_POWER, 3600, 1), 1f)
                        .alwaysEat()
                        .build();

        public static final FoodProperties SLEEPING_HERB = new FoodProperties.Builder()
                        .nutrition(1).saturationMod(0.3f)
                        .effect(() -> new MobEffectInstance(MobEffects.CONDUIT_POWER, 3600, 0), 1f)
                        .fast()
                        .alwaysEat()
                        .build();

        public static final FoodProperties PHANTOM_HERB = new FoodProperties.Builder()
                        .nutrition(1).saturationMod(0.3f)
                        .fast()
                        .alwaysEat()
                        .build();

        public static final FoodProperties CHORUS_HERB = new FoodProperties.Builder()
                        .nutrition(1).saturationMod(0.3f)
                        .fast()
                        .alwaysEat()
                        .build();

        public static final FoodProperties GLOW_HERB = new FoodProperties.Builder()
                        .nutrition(1).saturationMod(0.3f)
                        .fast()
                        .alwaysEat()
                        .build();

}