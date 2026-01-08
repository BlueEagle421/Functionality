package com.blueeagle421.functionality.compat;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.item.custom.equipment.FinsItem;
import com.blueeagle421.functionality.item.custom.equipment.GlowCrownItem;
import com.blueeagle421.functionality.item.custom.equipment.InfernoGearItem;
import com.blueeagle421.functionality.item.custom.equipment.ObsidianFinsItem;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.curios.api.CuriosApi;

public class CurioCompat {

    public static void setupCompat() {
        ModCompatManager.curiosPresent = true;
    }

    // ITEMS

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            FunctionalityMod.MOD_ID);

    public static final RegistryObject<Item> GLOW_CROWN = ITEMS
            .register("glow_crown", () -> new GlowCrownItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> FINS = ITEMS
            .register("fins", () -> new FinsItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> OBSIDIAN_FINS = ITEMS
            .register("obsidian_fins", () -> new ObsidianFinsItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> INFERNO_GEAR = ITEMS
            .register("inferno_gear", () -> new InfernoGearItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public class Utils {

        public static boolean hasInfernoGear(Entity entity) {
            return hasCurio(entity, InfernoGearItem.class);
        }

        public static boolean hasObsidianFins(Entity entity) {
            return hasCurio(entity, ObsidianFinsItem.class);
        }

        public static boolean hasCurio(Entity entity, Class<? extends Item> itemType) {
            if (!ModCompatManager.curiosPresent)
                return false;

            if (!(entity instanceof LivingEntity livingEntity))
                return false;

            return CuriosApi.getCuriosInventory(
                    livingEntity)
                    .map(curios -> {
                        IItemHandler handler = curios.getEquippedCurios();
                        for (int i = 0; i < handler.getSlots(); i++) {
                            ItemStack stack = handler.getStackInSlot(i);
                            if (!stack.isEmpty() && itemType.isInstance(stack.getItem()))
                                return true;
                        }
                        return false;
                    })
                    .orElse(false);
        }

    }
}
