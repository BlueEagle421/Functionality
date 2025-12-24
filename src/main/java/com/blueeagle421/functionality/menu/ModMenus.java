package com.blueeagle421.functionality.menu;

import com.blueeagle421.functionality.FunctionalityMod;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {

        public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES,
                        FunctionalityMod.MOD_ID);

        public static final RegistryObject<MenuType<RepairAltarMenu>> REPAIR_ALTAR_MENU = MENUS.register(
                        "repair_altar",
                        () -> IForgeMenuType.<RepairAltarMenu>create(RepairAltarMenu::new));

        public static void register(IEventBus eventBus) {
                MENUS.register(eventBus);
        }
}