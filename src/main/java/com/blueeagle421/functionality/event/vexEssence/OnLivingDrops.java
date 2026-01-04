package com.blueeagle421.functionality.event.vexEssence;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.item.custom.openable.VexEssenceItem;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;

@Mod.EventBusSubscriber(modid = FunctionalityMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OnLivingDrops {

    private static final String ESSENCE_COUNT_KEY = FunctionalityMod.MOD_ID + ":vex_essence_count";

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel))
            return;

        if (!(event.getEntity() instanceof Vex vex))
            return;

        if (!config().items.vexEssence.enabled.get()) {
            discardEssenceDrops(event);
            return;
        }

        Mob owner = vex.getOwner();

        if (owner == null || owner.isDeadOrDying()) {
            discardEssenceDrops(event);
            return;
        }

        handleOwnerLimitedDrops(event, owner);
    }

    private static void handleOwnerLimitedDrops(LivingDropsEvent event, Entity owner) {
        CompoundTag data = owner.getPersistentData();
        int current = data.getInt(ESSENCE_COUNT_KEY);
        int allowedLeft = config().features.vexEssenceDrops.maxPerOwner.get() - current;

        if (allowedLeft <= 0) {
            discardEssenceDrops(event);
            return;
        }

        int remainingToAllow = allowedLeft;
        Iterator<ItemEntity> iter = event.getDrops().iterator();
        while (iter.hasNext()) {
            ItemEntity itemEntity = iter.next();
            if (itemEntity == null)
                continue;

            if (itemEntity.getItem().getItem() instanceof VexEssenceItem) {
                int stackCount = itemEntity.getItem().getCount();
                if (remainingToAllow <= 0) {
                    itemEntity.remove(RemovalReason.DISCARDED);
                } else if (stackCount <= remainingToAllow) {
                    remainingToAllow -= stackCount;
                } else {
                    itemEntity.getItem().setCount(remainingToAllow);
                    remainingToAllow = 0;
                }
            }
        }

        int added = allowedLeft - remainingToAllow;
        if (added > 0)
            data.putInt(ESSENCE_COUNT_KEY, current + added);
    }

    private static void discardEssenceDrops(LivingDropsEvent event) {
        for (ItemEntity itemEntity : event.getDrops())
            if (itemEntity.getItem().getItem() instanceof VexEssenceItem)
                itemEntity.remove(RemovalReason.DISCARDED);
    }

    private static FunctionalityConfig.Common config() {
        return FunctionalityConfig.COMMON;
    }
}
