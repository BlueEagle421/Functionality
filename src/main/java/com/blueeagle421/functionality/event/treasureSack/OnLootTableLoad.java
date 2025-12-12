package com.blueeagle421.functionality.event.treasureSack;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.config.FunctionalityConfig;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

@SuppressWarnings("removal")
@Mod.EventBusSubscriber(modid = FunctionalityMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OnLootTableLoad {

    private static final ResourceLocation INJECT_TABLE = new ResourceLocation(FunctionalityMod.MOD_ID,
            "entities/illager");

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation tableId = event.getName();
        if (!"entities".equals(tableId.getPath().split("/")[0]) && !tableId.getPath().startsWith("entities/")) {
            return;
        }

        Set<ResourceLocation> entities = FunctionalityConfig.COMMON.features.treasureSacks
                .getEntitiesAsResourceLocations();
        if (entities.isEmpty())
            return;

        for (ResourceLocation entityId : entities) {
            ResourceLocation expectedLootTableForEntity = new ResourceLocation(entityId.getNamespace(),
                    "entities/" + entityId.getPath());
            if (expectedLootTableForEntity.equals(tableId)) {

                LootPool pool = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootTableReference.lootTableReference(INJECT_TABLE))
                        .name(FunctionalityMod.MOD_ID + ":injected_" + entityId.getPath())
                        .build();

                LootTable lootTable = event.getTable();
                lootTable.addPool(pool);

                return;
            }
        }
    }
}
