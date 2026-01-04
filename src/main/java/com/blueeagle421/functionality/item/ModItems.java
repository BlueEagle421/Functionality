package com.blueeagle421.functionality.item;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.block.ModBlocks;
import com.blueeagle421.functionality.entity.custom.ObsidianBoatEntity;
import com.blueeagle421.functionality.item.custom.*;
import com.blueeagle421.functionality.item.custom.food.*;
import com.blueeagle421.functionality.item.custom.openable.*;
import com.blueeagle421.functionality.item.custom.equipment.*;

import net.minecraft.core.Direction;
import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// Adding new item tutorial for future me and other devs

// Every item needs:
// A config class in /config/subcategories/items
// Recipes controlled by ConfigEnabledCondition
// creative tab config check

// Example recipe condition:
//    "conditions": [
//        {
//            "type": "functionality:enabled",
//            "configPath": "items.whispering_herb.enabled"
//        }
//    ]

// It uses reflection to get fields from config
// They are auto converted to snake case for consistency
// Example field from item config class: whisperingHerb -> whispering_herb

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            FunctionalityMod.MOD_ID);

    public static final RegistryObject<Item> INFORMATION = ITEMS.register("information",
            () -> new TooltipItem(new Item.Properties()));

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

    public static final RegistryObject<Item> TERRAPIN = ITEMS.register("terrapin",
            () -> new TooltipItem(new Item.Properties().food(ModFoods.TERRAPIN)));

    public static final RegistryObject<Item> COOKED_TERRAPIN = ITEMS.register("cooked_terrapin",
            () -> new TooltipItem(new Item.Properties().food(ModFoods.TERRAPIN)));

    public static final RegistryObject<Item> TERRAPIN_SOUP = ITEMS.register("terrapin_soup",
            () -> new TooltipBowlFoodItem(new Item.Properties()
                    .stacksTo(1)
                    .food(ModFoods.TERRAPIN_SOUP)));

    public static final RegistryObject<Item> FINS = ITEMS
            .register("fins", () -> new FinsItem(ArmorMaterials.TURTLE, ArmorItem.Type.BOOTS,
                    new Item.Properties()));

    public static final RegistryObject<Item> OBSIDIAN_FINS = ITEMS
            .register("obsidian_fins", () -> new ObsidianFinsItem(
                    ModArmorMaterials.OBSIDIAN, ArmorItem.Type.BOOTS,
                    new Item.Properties()));

    public static final RegistryObject<Item> INFERNO_GEAR = ITEMS
            .register("inferno_gear", () -> new InfernoGearItem(
                    ModArmorMaterials.OBSIDIAN, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties()));

    public static final RegistryObject<Item> WHISPERING_HERB = ITEMS.register("whispering_herb",
            () -> new WhisperingHerbItem(new Item.Properties().food(ModFoods.HERB)));

    public static final RegistryObject<Item> PHANTOM_HERB = ITEMS.register("phantom_herb",
            () -> new PhantomHerbItem(new Item.Properties().food(ModFoods.HERB)));

    public static final RegistryObject<Item> CHORUS_HERB = ITEMS.register("chorus_herb",
            () -> new ChorusHerbItem(new Item.Properties().food(ModFoods.HERB)));

    public static final RegistryObject<Item> CRIMSON_HERB = ITEMS.register("crimson_herb",
            () -> new CrimsonHerbItem(new Item.Properties().food(ModFoods.HERB)));

    public static final RegistryObject<Item> GLOW_HERB = ITEMS.register("glow_herb",
            () -> new GlowHerbItem(new Item.Properties().food(ModFoods.HERB)));

    public static final RegistryObject<Item> GLOW_CROWN = ITEMS.register("glow_crown",
            () -> new GlowCrownItem(ModArmorMaterials.KELP, ArmorItem.Type.HELMET,
                    new Item.Properties()));

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

    public static final RegistryObject<Item> HARPOON = ITEMS.register("harpoon",
            () -> new HarpoonItem(ModToolTiers.FLINT, 2, -1.6F, new Item.Properties()));

    public static final RegistryObject<Item> BIDENT = ITEMS.register("bident",
            () -> new BidentItem(ModToolTiers.PRISMARINE, 3, -1.6F, new Item.Properties()));

    public static final RegistryObject<Item> FISH_TRAP = ITEMS.register("fish_trap",
            () -> new TooltipBlockItem(ModBlocks.FISH_TRAP.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOOM_LICHEN = ITEMS.register("bloom_lichen",
            () -> new TooltipBlockItem(ModBlocks.BLOOM_LICHEN.get(), new Item.Properties()));

    public static final RegistryObject<Item> DRY_LICHEN = ITEMS.register("dry_lichen",
            () -> new TooltipBlockItem(ModBlocks.DRY_LICHEN.get(), new Item.Properties()));

    public static final RegistryObject<Item> LICHEN = ITEMS.register("lichen",
            () -> new TooltipBlockItem(ModBlocks.LICHEN.get(), new Item.Properties()));

    public static final RegistryObject<Item> CHUNK_LOADER = ITEMS.register("chunk_loader",
            () -> new TooltipBlockItem(ModBlocks.CHUNK_LOADER.get(), new Item.Properties()));

    public static final RegistryObject<Item> REPAIR_ALTAR = ITEMS.register("repair_altar",
            () -> new TooltipBlockItem(ModBlocks.REPAIR_ALTAR.get(), new Item.Properties()));

    public static final RegistryObject<Item> PHEROMONES = ITEMS.register("pheromones",
            () -> new PheromonesItem(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> PHANTOM_TREAT = ITEMS.register("phantom_treat",
            () -> new PhantomTreatItem(new Item.Properties()));

    public static final RegistryObject<Item> PHANTOM_ROCKET = ITEMS.register("phantom_rocket",
            () -> new FireworkRocketItem(new Item.Properties()));

    public static final RegistryObject<Item> TREASURE_SACK = ITEMS.register("treasure_sack",
            () -> new TreasureSackItem(new Item.Properties()));

    public static final RegistryObject<Item> INFERNAL_SACK = ITEMS.register("infernal_sack",
            () -> new InfernalSackItem(new Item.Properties()));

    public static final RegistryObject<Item> DISC_SHARDS = ITEMS.register("disc_shards",
            () -> new TooltipItem(new Item.Properties()));

    public static final RegistryObject<Item> VEX_ESSENCE = ITEMS.register("vex_essence",
            () -> new VexEssenceItem(new Item.Properties()));

    public static final RegistryObject<Item> ANCIENT_SEEKER = ITEMS.register("ancient_seeker",
            () -> new AncientSeekerItem(new Item.Properties()));

    public static final RegistryObject<Item> NAUTILUS_BUCKET = ITEMS.register("nautilus_bucket",
            () -> new NautilusBucketItem(
                    new Item.Properties().rarity(Rarity.UNCOMMON).durability(196)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}