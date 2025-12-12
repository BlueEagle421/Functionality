package com.blueeagle421.functionality.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Supplier;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.item.ModItems;

@Mod.EventBusSubscriber(modid = FunctionalityMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OnLivingDrops {

        @SubscribeEvent
        public static void onLivingDrops(LivingDropsEvent event) {
                handleEntityDrop(event, AbstractIllager.class,
                                () -> new ItemStack(ModItems.TREASURE_SACK.get()), 0.4f, 1, 0.5f, 1);
        }

        private static void handleEntityDrop(LivingDropsEvent event,
                        Class<? extends LivingEntity> entityClass,
                        Supplier<ItemStack> stackSupplier,
                        float baseChance,
                        int baseAmount,
                        float extraChance,
                        int extraTriesAmount) {

                if (!entityClass.isInstance(event.getEntity()))
                        return;

                Level level = event.getEntity().level();

                if (level.isClientSide())
                        return;

                int looting = event.getLootingLevel();

                int amount = 0;

                if (level.random.nextFloat() < baseChance)
                        amount += baseAmount;

                for (int i = 0; i < extraTriesAmount; i++)
                        if (level.random.nextFloat() < extraChance)
                                amount += 1;

                for (int i = 0; i < looting; i++)
                        if (level.random.nextBoolean())
                                amount++;

                for (int i = 0; i < amount; i++) {
                        ItemEntity drop = new ItemEntity(
                                        level,
                                        event.getEntity().getX(),
                                        event.getEntity().getY(),
                                        event.getEntity().getZ(),
                                        stackSupplier.get());

                        event.getDrops().add(drop);
                }
        }
}