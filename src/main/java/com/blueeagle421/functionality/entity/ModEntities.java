package com.blueeagle421.functionality.entity;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.entity.custom.ObsidianBoatEntity;
import com.blueeagle421.functionality.entity.custom.ThrownDiscEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("removal")
public class ModEntities {

        public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister
                        .create(ForgeRegistries.ENTITY_TYPES, FunctionalityMod.MOD_ID);

        public static final RegistryObject<EntityType<ObsidianBoatEntity>> OBSIDIAN_BOAT = ENTITY_TYPES.register(
                        "obsidian_boat",
                        () -> EntityType.Builder.<ObsidianBoatEntity>of(
                                        ObsidianBoatEntity::new, MobCategory.MISC).fireImmune()
                                        .sized(1.375f, 0.5625f).build("obsidian_boat"));

        public static final RegistryObject<EntityType<ThrownDiscEntity>> THROWN_DISC = ENTITY_TYPES.register(
                        "thrown_disc",
                        () -> EntityType.Builder.<ThrownDiscEntity>of(ThrownDiscEntity::new, MobCategory.MISC)
                                        .sized(0.5f, 0.5f)
                                        .clientTrackingRange(8)
                                        .build(new ResourceLocation(FunctionalityMod.MOD_ID, "thrown_disc")
                                                        .toString()));

        public static void register(IEventBus eventBus) {
                ENTITY_TYPES.register(eventBus);
        }

}