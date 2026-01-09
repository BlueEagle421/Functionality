package com.blueeagle421.functionality.compat;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.item.ModFoods;
import com.blueeagle421.functionality.utils.TooltipUtils;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import vectorwing.farmersdelight.common.FoodValues;
import vectorwing.farmersdelight.common.item.ConsumableItem;
import vectorwing.farmersdelight.common.registry.ModEffects;

public class FarmersDelightCompat {

    public static void setupCompat() {
        ModCompatManager.farmersDelightPresent = true;
    }

    // FOOD PROPERTIES

    public static final FoodProperties BEAR_SOUP_PROP = (new FoodProperties.Builder())
            .nutrition(14).saturationMod(0.75f)
            .effect(() -> new MobEffectInstance(ModEffects.COMFORT.get(), FoodValues.LONG_DURATION, 0), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60 * ModFoods.TICKS_PER_SECOND, 0), 1f)
            .alwaysEat()
            .build();

    public static final FoodProperties TERRAPIN_SOUP_PROP = new FoodProperties.Builder()
            .nutrition(12).saturationMod(0.75f)
            .effect(() -> new MobEffectInstance(ModEffects.COMFORT.get(), FoodValues.MEDIUM_DURATION, 0), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.CONDUIT_POWER, 110 * ModFoods.TICKS_PER_SECOND, 1), 1f)
            .alwaysEat()
            .build();

    // ITEMS

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            FunctionalityMod.MOD_ID);

    public static Item.Properties bowlFoodItem(FoodProperties food) {
        return new Item.Properties().food(food).craftRemainder(Items.BOWL).stacksTo(16);
    }

    public static final RegistryObject<Item> BEAR_SOUP = ITEMS.register("bear_soup",
            () -> new TooltipConsumableItem(bowlFoodItem(BEAR_SOUP_PROP)));

    public static final RegistryObject<Item> TERRAPIN_SOUP = ITEMS.register("terrapin_soup",
            () -> new TooltipConsumableItem(bowlFoodItem(TERRAPIN_SOUP_PROP)));

    // Why this has to be static? Another example of C# supremacy 8)
    public static class TooltipConsumableItem extends ConsumableItem {

        public TooltipConsumableItem(Properties properties) {
            super(properties, false, false);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level,
                List<Component> tooltip, TooltipFlag isAdvanced) {

            TooltipUtils.addFormattedTooltip(stack, tooltip);
            super.appendHoverText(stack, level, tooltip, isAdvanced);
        }
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
