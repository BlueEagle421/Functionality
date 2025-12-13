package com.blueeagle421.functionality.item;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.config.categories.ItemsCategory;

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
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.OBSIDIAN_BOAT.get()))
                    .title(Component.translatable("creativetab.functionality_tab"))
                    .displayItems((pParameters, pOutput) -> addItemsToTab(pOutput::accept))
                    .build());

    private static void addItemsToTab(Consumer<ItemStack> out) {
        acceptIf(out, () -> true, () -> new ItemStack(ModItems.SLEEPING_HERB.get()));
        acceptIf(out, () -> true, () -> new ItemStack(ModItems.PHANTOM_HERB.get()));
        acceptIf(out, () -> true, () -> new ItemStack(ModItems.CHORUS_HERB.get()));

        // with nbt
        acceptIf(out, () -> true, () -> {
            ItemStack crimsonHerbStack = new ItemStack(ModItems.CRIMSON_HERB.get());
            CrimsonHerbItem.setDuration(crimsonHerbStack, CrimsonHerbItem.DEFAULT_DURATION);
            return crimsonHerbStack;
        });

        // with nbt
        acceptIf(out, () -> true, () -> {
            ItemStack glowingHerbStack = new ItemStack(ModItems.GLOW_HERB.get());
            GlowingHerbItem.setDuration(glowingHerbStack, GlowingHerbItem.DEFAULT_DURATION);
            return glowingHerbStack;
        });

        acceptIf(out, () -> true, () -> new ItemStack(ModItems.GLOW_CROWN.get()));
        acceptIf(out, () -> true, () -> new ItemStack(ModItems.BEAR_VENISON.get()));
        acceptIf(out, () -> true, () -> new ItemStack(ModItems.COOKED_BEAR_VENISON.get()));
        acceptIf(out, () -> true, () -> new ItemStack(ModItems.CHEVON.get()));
        acceptIf(out, () -> true, () -> new ItemStack(ModItems.COOKED_CHEVON.get()));
        acceptIf(out, () -> true, () -> new ItemStack(ModItems.SNIFFON.get()));
        acceptIf(out, () -> true, () -> new ItemStack(ModItems.COOKED_SNIFFON.get()));
        acceptIf(out, () -> true, () -> new ItemStack(ModItems.GILDED_SNIFFON.get()));
        acceptIf(out, () -> true, () -> new ItemStack(ModItems.FROG_LEG.get()));
        acceptIf(out, () -> true, () -> new ItemStack(ModItems.COOKED_FROG_LEG.get()));
        acceptIf(out, () -> true, () -> new ItemStack(ModItems.TERRAPIN.get()));
        acceptIf(out, () -> true, () -> new ItemStack(ModItems.TERRAPIN_SOUP.get()));
        acceptIf(out, () -> true, () -> new ItemStack(ModItems.FINS.get()));
        acceptIf(out, () -> true, () -> new ItemStack(ModItems.OBSIDIAN_FINS.get()));
        acceptIf(out, () -> true, () -> new ItemStack(ModItems.AMETHYST_ARROW.get()));
        acceptIf(out, () -> true, () -> new ItemStack(ModItems.OBSIDIAN_BOAT.get()));
        acceptIf(out, () -> true, () -> new ItemStack(ModItems.INFERNO_GEAR.get()));
        acceptIf(out, () -> true, () -> new ItemStack(ModItems.GLOW_TORCH.get()));
        acceptIf(out, () -> true, () -> new ItemStack(ModItems.FISH_TRAP.get()));
        acceptIf(out, () -> true, () -> new ItemStack(ModItems.LIGHTNING_CHARGER.get()));
        acceptIf(out, () -> true, () -> new ItemStack(ModItems.TREASURE_SACK.get()));

        // with nbt
        acceptIf(out, () -> true, () -> {
            ItemStack rocketStack = new ItemStack(ModItems.PHANTOM_ROCKET.get());
            CompoundTag tag = new CompoundTag();
            tag.putByte("Flight", (byte) 5);
            CompoundTag root = rocketStack.getOrCreateTag();
            root.put("Fireworks", tag);
            return rocketStack;
        });

        acceptIf(out, () -> true, () -> new ItemStack(ModItems.PHEROMONES.get()));
        acceptIf(out, () -> true, () -> new ItemStack(ModItems.PHANTOM_TREAT.get()));
        acceptIf(out, () -> true, () -> new ItemStack(ModItems.PHANTOM_LEAD.get()));

        acceptIf(out, () -> config().ancientSeeker.enabled.get(),
                () -> new ItemStack(ModItems.ANCIENT_SEEKER.get()));

        acceptIf(out, () -> true, () -> new ItemStack(ModItems.HARPOON.get()));
    }

    private static void acceptIf(Consumer<ItemStack> output, BooleanSupplier enabled,
            Supplier<ItemStack> stackSupplier) {
        if (enabled.getAsBoolean()) {
            output.accept(stackSupplier.get());
        }
    }

    private static ItemsCategory config() {
        return FunctionalityConfig.COMMON.items;
    }

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}