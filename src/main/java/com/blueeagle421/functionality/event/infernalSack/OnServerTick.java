package com.blueeagle421.functionality.event.infernalSack;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.data.InfernalSackChunkData;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

@Mod.EventBusSubscriber(modid = FunctionalityMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OnServerTick {

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;

        MinecraftServer server = event.getServer();

        if (server == null)
            return;

        for (ServerLevel level : server.getAllLevels()) {
            // 24000L - day duration
            long day = level.getDayTime() / 24000L;

            // read or create data
            InfernalSackChunkData data = level.getDataStorage()
                    .computeIfAbsent(InfernalSackChunkData::load, InfernalSackChunkData::new,
                            InfernalSackChunkData.storageKey());

            // day already checked
            if (data.getLastProcessedDay() == day)
                continue;

            // new day
            if (data.getLastProcessedDay() < day) {
                data.dailyRegen();
                data.setLastProcessedDay(day);
            }
        }
    }
}