package com.blueeagle421.functionality.item;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.config.categories.ItemsCategory;
import com.blueeagle421.functionality.config.subcategories.features.BetterLichens;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import com.blueeagle421.functionality.item.custom.food.*;

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

        // with nbt
        acceptIf(out, () -> config().whisperingHerb.enabled.get(), () -> {
            ItemStack herbStack = new ItemStack(ModItems.WHISPERING_HERB.get());
            EffectHerbItem.setDuration(herbStack, WhisperingHerbItem.DEFAULT_DURATION);
            return herbStack;
        });

        acceptIf(out, () -> config().phantomHerb.enabled.get(), () -> new ItemStack(ModItems.PHANTOM_HERB.get()));
        acceptIf(out, () -> config().chorusHerb.enabled.get(), () -> new ItemStack(ModItems.CHORUS_HERB.get()));

        // with nbt
        acceptIf(out, () -> config().crimsonHerb.enabled.get(), () -> {
            ItemStack crimsonHerbStack = new ItemStack(ModItems.CRIMSON_HERB.get());
            EffectHerbItem.setDuration(crimsonHerbStack, CrimsonHerbItem.DEFAULT_DURATION);
            return crimsonHerbStack;
        });

        // with nbt
        acceptIf(out, () -> config().glowHerb.enabled.get(), () -> {
            ItemStack glowingHerbStack = new ItemStack(ModItems.GLOW_HERB.get());
            EffectHerbItem.setDuration(glowingHerbStack, GlowHerbItem.DEFAULT_DURATION);
            return glowingHerbStack;
        });

        acceptIf(out, () -> config().glowCrown.enabled.get(), () -> new ItemStack(ModItems.GLOW_CROWN.get()));
        acceptIf(out, () -> config().bearVenison.enabled.get(), () -> new ItemStack(ModItems.BEAR_VENISON.get()));
        acceptIf(out, () -> config().bearVenison.enabled.get(),
                () -> new ItemStack(ModItems.COOKED_BEAR_VENISON.get()));
        acceptIf(out, () -> config().chevon.enabled.get(), () -> new ItemStack(ModItems.CHEVON.get()));
        acceptIf(out, () -> config().chevon.enabled.get(), () -> new ItemStack(ModItems.COOKED_CHEVON.get()));
        acceptIf(out, () -> config().sniffon.enabled.get(), () -> new ItemStack(ModItems.SNIFFON.get()));
        acceptIf(out, () -> config().sniffon.enabled.get(), () -> new ItemStack(ModItems.COOKED_SNIFFON.get()));
        acceptIf(out, () -> config().sniffon.enabled.get(), () -> new ItemStack(ModItems.GILDED_SNIFFON.get()));
        acceptIf(out, () -> config().frogLeg.enabled.get(), () -> new ItemStack(ModItems.FROG_LEG.get()));
        acceptIf(out, () -> config().frogLeg.enabled.get(), () -> new ItemStack(ModItems.COOKED_FROG_LEG.get()));
        acceptIf(out, () -> config().terrapin.enabled.get(), () -> new ItemStack(ModItems.TERRAPIN.get()));
        acceptIf(out, () -> config().terrapin.enabled.get(), () -> new ItemStack(ModItems.TERRAPIN_SOUP.get()));
        acceptIf(out, () -> config().fins.enabled.get(), () -> new ItemStack(ModItems.FINS.get()));
        acceptIf(out, () -> config().obsidianFins.enabled.get(), () -> new ItemStack(ModItems.OBSIDIAN_FINS.get()));
        acceptIf(out, () -> config().amethystArrow.enabled.get(), () -> new ItemStack(ModItems.AMETHYST_ARROW.get()));
        acceptIf(out, () -> config().obsidianBoat.enabled.get(), () -> new ItemStack(ModItems.OBSIDIAN_BOAT.get()));
        acceptIf(out, () -> config().infernoGear.enabled.get(), () -> new ItemStack(ModItems.INFERNO_GEAR.get()));
        acceptIf(out, () -> config().glowTorch.enabled.get(), () -> new ItemStack(ModItems.GLOW_TORCH.get()));
        acceptIf(out, () -> config().fishTrap.enabled.get(), () -> new ItemStack(ModItems.FISH_TRAP.get()));
        acceptIf(out, () -> config().repairAltar.enabled.get(),
                () -> new ItemStack(ModItems.REPAIR_ALTAR.get()));
        acceptIf(out, () -> config().chunkLoader.enabled.get(),
                () -> new ItemStack(ModItems.CHUNK_LOADER.get()));
        acceptIf(out, () -> config().treasureSack.enabled.get(), () -> new ItemStack(ModItems.TREASURE_SACK.get()));
        acceptIf(out, () -> config().discShards.enabled.get(), () -> new ItemStack(ModItems.DISC_SHARDS.get()));

        // with nbt
        acceptIf(out, () -> config().phantomRocket.enabled.get(), () -> {
            ItemStack rocketStack = new ItemStack(ModItems.PHANTOM_ROCKET.get());
            CompoundTag tag = new CompoundTag();
            tag.putByte("Flight", (byte) 5);
            CompoundTag root = rocketStack.getOrCreateTag();
            root.put("Fireworks", tag);
            return rocketStack;
        });

        acceptIf(out, () -> config().pheromones.enabled.get(), () -> new ItemStack(ModItems.PHEROMONES.get()));
        acceptIf(out, () -> config().phantomTreat.enabled.get(), () -> new ItemStack(ModItems.PHANTOM_TREAT.get()));
        acceptIf(out, () -> config().ancientSeeker.enabled.get(), () -> new ItemStack(ModItems.ANCIENT_SEEKER.get()));
        acceptIf(out, () -> config().harpoon.enabled.get(), () -> new ItemStack(ModItems.HARPOON.get()));
        acceptIf(out, () -> config().nautilusBucket.enabled.get(), () -> new ItemStack(ModItems.NAUTILUS_BUCKET.get()));
        acceptIf(out, () -> lichenConfig().enabled.get(), () -> new ItemStack(ModItems.DRY_LICHEN.get()));
        acceptIf(out, () -> lichenConfig().enabled.get(), () -> new ItemStack(ModItems.LICHEN.get()));
        acceptIf(out, () -> lichenConfig().enabled.get(), () -> new ItemStack(ModItems.BLOOM_LICHEN.get()));
    }

    private static void acceptIf(Consumer<ItemStack> output, BooleanSupplier enabled,
            Supplier<ItemStack> stackSupplier) {

        if (enabled.getAsBoolean())
            output.accept(stackSupplier.get());

    }

    private static ItemsCategory config() {
        return FunctionalityConfig.COMMON.items;
    }

    private static BetterLichens lichenConfig() {
        return FunctionalityConfig.COMMON.features.betterLichens;
    }

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}