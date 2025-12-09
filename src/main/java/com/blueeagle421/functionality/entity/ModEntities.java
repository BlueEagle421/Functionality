package com.blueeagle421.functionality.entity;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.entity.custom.AnvilMarkerEntity;
import com.blueeagle421.functionality.entity.custom.ObsidianBoatEntity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {

        public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister
                        .create(ForgeRegistries.ENTITY_TYPES, FunctionalityMod.MOD_ID);

        public static final RegistryObject<EntityType<ObsidianBoatEntity>> OBSIDIAN_BOAT = ENTITY_TYPES.register(
                        "obsidian_boat",
                        () -> EntityType.Builder.<ObsidianBoatEntity>of(
                                        ObsidianBoatEntity::new, MobCategory.MISC).fireImmune()
                                        .sized(1.375f, 0.5625f).build("obsidian_boat"));

        public static final RegistryObject<EntityType<AnvilMarkerEntity>> ANVIL_MARKER = ENTITY_TYPES.register(
                        "ghost_marker",
                        () -> EntityType.Builder.<AnvilMarkerEntity>of(AnvilMarkerEntity::new, MobCategory.MISC)
                                        .sized(0.1f, 0.1f)
                                        .build("ghost_marker"));

        public static void register(IEventBus eventBus) {
                ENTITY_TYPES.register(eventBus);
        }

}