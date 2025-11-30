package com.blueeagle421.functionality.item;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.block.ModBlocks;
import com.blueeagle421.functionality.entity.custom.ObsidianBoatEntity;
import com.blueeagle421.functionality.item.custom.AmethystArrowItem;
import com.blueeagle421.functionality.item.custom.FrogLegItem;
import com.blueeagle421.functionality.item.custom.GlowTorchItem;
import com.blueeagle421.functionality.item.custom.ObsidianBoatItem;
import com.blueeagle421.functionality.item.custom.TooltipItem;

import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
        public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
                        FunctionalityMod.MOD_ID);

        public static final RegistryObject<Item> BEAR_VENISON = ITEMS.register("bear_venison",
                        () -> new TooltipItem(new Item.Properties().food(ModFoods.BEAR_VENISON)));

        public static final RegistryObject<Item> COOKED_BEAR_VENISON = ITEMS.register("cooked_bear_venison",
                        () -> new TooltipItem(new Item.Properties().food(ModFoods.COOKED_BEAR_VENISON)));

        public static final RegistryObject<Item> CHEVON = ITEMS.register("chevon",
                        () -> new TooltipItem(new Item.Properties().food(ModFoods.CHEVON)));

        public static final RegistryObject<Item> COOKED_CHEVON = ITEMS.register("cooked_chevon",
                        () -> new TooltipItem(new Item.Properties().food(ModFoods.COOKED_CHEVON)));

        public static final RegistryObject<Item> SNIFFON = ITEMS.register("sniffon",
                        () -> new TooltipItem(new Item.Properties().food(ModFoods.SNIFFON)));

        public static final RegistryObject<Item> COOKED_SNIFFON = ITEMS.register("cooked_sniffon",
                        () -> new TooltipItem(new Item.Properties().food(ModFoods.COOKED_SNIFFON)));

        public static final RegistryObject<Item> GILDED_SNIFFON = ITEMS.register("gilded_sniffon",
                        () -> new TooltipItem(new Item.Properties().food(ModFoods.GILDED_SNIFFON)));

        public static final RegistryObject<Item> FROG_LEG = ITEMS.register("frog_leg",
                        () -> new TooltipItem(new Item.Properties().food(ModFoods.FROG_LEG)));

        public static final RegistryObject<Item> COOKED_FROG_LEG = ITEMS.register("cooked_frog_leg",
                        () -> new FrogLegItem(new Item.Properties().food(ModFoods.COOKED_FROG_LEG)));

        public static final RegistryObject<Item> AMETHYST_ARROW = ITEMS.register("amethyst_arrow",
                        () -> new AmethystArrowItem(new Item.Properties()));

        public static final RegistryObject<Item> OBSIDIAN_BOAT = ITEMS.register("obsidian_boat",
                        () -> new ObsidianBoatItem(false, ObsidianBoatEntity.Type.OBSIDIAN,
                                        new Item.Properties().fireResistant()));

        public static final RegistryObject<Item> GLOW_TORCH = ITEMS.register("glow_torch",
                        () -> new GlowTorchItem(
                                        ModBlocks.GLOW_TORCH.get(),
                                        ModBlocks.WALL_GLOW_TORCH.get(),
                                        new Item.Properties(),
                                        Direction.DOWN));

        public static final RegistryObject<Item> FISH_TRAP = ITEMS.register("fish_trap",
                        () -> new BlockItem(ModBlocks.FISH_TRAP.get(), new Item.Properties()));

        public static void register(IEventBus eventBus) {
                ITEMS.register(eventBus);
        }
}