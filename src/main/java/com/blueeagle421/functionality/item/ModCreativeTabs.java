package com.blueeagle421.functionality.item;

import com.blueeagle421.functionality.FunctionalityMod;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, FunctionalityMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> TUTORIAL_TAB = CREATIVE_MODE_TABS.register("functionality_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.BEAR_VENISON.get()))
                    .title(Component.translatable("creativetab.functionality_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.SLEEPING_HERB.get());
                        pOutput.accept(ModItems.PHANTOM_HERB.get());
                        pOutput.accept(ModItems.CHORUS_HERB.get());
                        pOutput.accept(ModItems.BEAR_VENISON.get());
                        pOutput.accept(ModItems.COOKED_BEAR_VENISON.get());
                        pOutput.accept(ModItems.CHEVON.get());
                        pOutput.accept(ModItems.COOKED_CHEVON.get());
                        pOutput.accept(ModItems.SNIFFON.get());
                        pOutput.accept(ModItems.COOKED_SNIFFON.get());
                        pOutput.accept(ModItems.GILDED_SNIFFON.get());
                        pOutput.accept(ModItems.FROG_LEG.get());
                        pOutput.accept(ModItems.COOKED_FROG_LEG.get());
                        pOutput.accept(ModItems.TERRAPIN.get());
                        pOutput.accept(ModItems.TERRAPIN_SOUP.get());
                        pOutput.accept(ModItems.FINS.get());
                        pOutput.accept(ModItems.AMETHYST_ARROW.get());
                        pOutput.accept(ModItems.OBSIDIAN_BOAT.get());
                        pOutput.accept(ModItems.GLOW_TORCH.get());
                        pOutput.accept(ModItems.FISH_TRAP.get());
                        pOutput.accept(ModItems.LIGHTNING_CHARGER.get());
                        pOutput.accept(ModItems.ANCIENT_SEEKER.get());

                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}