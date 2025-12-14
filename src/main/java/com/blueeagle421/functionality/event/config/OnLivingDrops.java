package com.blueeagle421.functionality.event.config;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.config.categories.ItemsCategory;
import com.blueeagle421.functionality.item.ModItems;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FunctionalityMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OnLivingDrops {

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {

        if (!(event.getEntity().level() instanceof ServerLevel server))
            return;

        for (ItemEntity itemEntity : event.getDrops()) {

            if (itemEntity.getItem().is(ModItems.BEAR_VENISON.get()))
                if (!config().bearVenison.enabled.get())
                    itemEntity.remove(RemovalReason.DISCARDED);

            if (itemEntity.getItem().is(ModItems.CHEVON.get()))
                if (!config().chevon.enabled.get())
                    itemEntity.remove(RemovalReason.DISCARDED);

            if (itemEntity.getItem().is(ModItems.FROG_LEG.get()))
                if (!config().frogLeg.enabled.get())
                    itemEntity.remove(RemovalReason.DISCARDED);

            if (itemEntity.getItem().is(ModItems.SNIFFON.get()))
                if (!config().sniffon.enabled.get())
                    itemEntity.remove(RemovalReason.DISCARDED);

            if (itemEntity.getItem().is(ModItems.TERRAPIN.get()))
                if (!config().terrapin.enabled.get())
                    itemEntity.remove(RemovalReason.DISCARDED);
        }
    }

    private static ItemsCategory config() {
        return FunctionalityConfig.COMMON.items;
    }
}
