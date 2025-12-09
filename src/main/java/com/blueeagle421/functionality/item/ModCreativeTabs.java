package com.blueeagle421.functionality.item;

import com.blueeagle421.functionality.FunctionalityMod;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import com.blueeagle421.functionality.item.custom.*;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, FunctionalityMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> TUTORIAL_TAB = CREATIVE_MODE_TABS.register("functionality_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.OBSIDIAN_BOAT.get()))
                    .title(Component.translatable("creativetab.functionality_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.SLEEPING_HERB.get());
                        pOutput.accept(ModItems.PHANTOM_HERB.get());
                        pOutput.accept(ModItems.CHORUS_HERB.get());
                        { // with nbt data
                            ItemStack glowingHerbStack = new ItemStack(ModItems.GLOW_HERB.get());
                            GlowingHerbItem.setDuration(glowingHerbStack, GlowingHerbItem.DEFAULT_DURATION);
                            pOutput.accept(glowingHerbStack);
                        }
                        pOutput.accept(ModItems.GLOW_CROWN.get());
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
                        pOutput.accept(ModItems.OBSIDIAN_FINS.get());
                        pOutput.accept(ModItems.AMETHYST_ARROW.get());
                        pOutput.accept(ModItems.OBSIDIAN_BOAT.get());
                        pOutput.accept(ModItems.INFERNO_GEAR.get());
                        pOutput.accept(ModItems.GLOW_TORCH.get());
                        pOutput.accept(ModItems.FISH_TRAP.get());
                        pOutput.accept(ModItems.LIGHTNING_CHARGER.get());
                        pOutput.accept(ModItems.TREASURE_SACK.get());
                        { // with nbt data
                            ItemStack rocketStack = new ItemStack(ModItems.PHANTOM_ROCKET.get());
                            CompoundTag tag = new CompoundTag();
                            tag.putByte("Flight", (byte) 5);
                            CompoundTag root = rocketStack.getOrCreateTag();
                            root.put("Fireworks", tag);
                            pOutput.accept(rocketStack);
                        }
                        pOutput.accept(ModItems.PHEROMONES.get());
                        pOutput.accept(ModItems.PHANTOM_TREAT.get());
                        pOutput.accept(ModItems.PHANTOM_LEAD.get());
                        pOutput.accept(ModItems.ANCIENT_SEEKER.get());
                        pOutput.accept(ModItems.HARPOON.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}