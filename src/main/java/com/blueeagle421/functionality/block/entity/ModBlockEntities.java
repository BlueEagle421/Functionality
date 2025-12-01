package com.blueeagle421.functionality.block.entity;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.block.*;
import com.blueeagle421.functionality.block.entity.custom.FishTrapEntity;
import com.blueeagle421.functionality.block.entity.custom.LightningChargerEntity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister
            .create(ForgeRegistries.BLOCK_ENTITY_TYPES, FunctionalityMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<FishTrapEntity>> FISH_TRAP = BLOCK_ENTITIES.register("fish_trap",
            () -> BlockEntityType.Builder.of(FishTrapEntity::new, ModBlocks.FISH_TRAP.get()).build(null));

    public static final RegistryObject<BlockEntityType<LightningChargerEntity>> LIGHTNING_CHARGER = BLOCK_ENTITIES
            .register("lightning_charger",
                    () -> BlockEntityType.Builder.of(LightningChargerEntity::new, ModBlocks.FISH_TRAP.get())
                            .build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}