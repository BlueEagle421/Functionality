package com.blueeagle421.functionality;

import com.blueeagle421.functionality.block.ModBlocks;
import com.blueeagle421.functionality.block.entity.ModBlockEntities;
import com.blueeagle421.functionality.client.ObsidianBoatRenderer;
import com.blueeagle421.functionality.client.particle.AncientSeekerParticle;
import com.blueeagle421.functionality.client.particle.GlowFlameParticle;
import com.blueeagle421.functionality.entity.ModEntities;
import com.blueeagle421.functionality.item.ModCreativeTabs;
import com.blueeagle421.functionality.item.ModItems;
import com.blueeagle421.functionality.particle.ModParticles;
import com.blueeagle421.functionality.worldgen.ModFeatures;
import com.mojang.logging.LogUtils;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.slf4j.Logger;

@Mod(FunctionalityMod.MOD_ID)
public class FunctionalityMod {
    public static final String MOD_ID = "functionality";
    public static final Logger LOGGER = LogUtils.getLogger();

    public FunctionalityMod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        ModCreativeTabs.register(modEventBus);

        ModParticles.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModEntities.register(modEventBus);
        ModFeatures.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(ModItems.BEAR_VENISON);
            event.accept(ModItems.COOKED_BEAR_VENISON);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

            EntityRenderers.register(ModEntities.OBSIDIAN_BOAT.get(),
                    pContext -> new ObsidianBoatRenderer(pContext, false));
        }

        @SubscribeEvent
        public static void registerParticleProvider(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(ModParticles.GLOW_FLAME.get(), GlowFlameParticle.Provider::new);
            event.registerSpriteSet(ModParticles.ANCIENT_SEEKER.get(), AncientSeekerParticle.Provider::new);
        }
    }
}