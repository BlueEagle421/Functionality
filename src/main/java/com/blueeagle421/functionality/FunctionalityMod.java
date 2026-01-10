package com.blueeagle421.functionality;

import com.blueeagle421.functionality.block.ModBlocks;
import com.blueeagle421.functionality.block.entity.ModBlockEntities;
import com.blueeagle421.functionality.client.InfernoGearHumanoidModel;
import com.blueeagle421.functionality.client.ObsidianBoatRenderer;
import com.blueeagle421.functionality.client.particle.AncientSeekerParticle;
import com.blueeagle421.functionality.client.particle.CustomSuspendedParticle;
import com.blueeagle421.functionality.client.particle.GlowSmokeParticle;
import com.blueeagle421.functionality.client.renderer.AmethystArrowRenderer;
import com.blueeagle421.functionality.client.renderer.ChunkLoaderRenderer;
import com.blueeagle421.functionality.client.renderer.CurioArmorLikeRenderer;
import com.blueeagle421.functionality.client.renderer.CurioArmorLikeRenderer.CurioModel;
import com.blueeagle421.functionality.client.renderer.RepairAltarRenderer;
import com.blueeagle421.functionality.client.renderer.ThrownDiscRenderer;
import com.blueeagle421.functionality.client.screen.RepairAltarScreen;
import com.blueeagle421.functionality.compat.CurioCompat;
import com.blueeagle421.functionality.compat.FarmersDelightCompat;
import com.blueeagle421.functionality.compat.ModCompatManager;
import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.data.conditions.ConfigEnabledCondition;
import com.blueeagle421.functionality.data.conditions.ConfigEnabledRegistry;
import com.blueeagle421.functionality.effect.ModEffects;
import com.blueeagle421.functionality.entity.ModEntities;
import com.blueeagle421.functionality.item.ModCreativeTabs;
import com.blueeagle421.functionality.item.ModItems;
import com.blueeagle421.functionality.item.custom.equipment.NautilusBucketItem;
import com.blueeagle421.functionality.loot.ModLootModifiers;
import com.blueeagle421.functionality.menu.ModMenus;
import com.blueeagle421.functionality.particle.ModParticles;
import com.blueeagle421.functionality.recipe.ModRecipes;
import com.blueeagle421.functionality.sound.ModSounds;
import com.blueeagle421.functionality.utils.CauldronUtils;
import com.blueeagle421.functionality.worldgen.ModFeatures;
import com.mojang.logging.LogUtils;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

import org.slf4j.Logger;

@SuppressWarnings("removal")
@Mod(FunctionalityMod.MOD_ID)
public class FunctionalityMod {
    public static final String MOD_ID = "functionality";
    public static final Logger LOGGER = LogUtils.getLogger();

    public FunctionalityMod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FunctionalityConfig.COMMON_SPEC,
                "functionality-common.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, FunctionalityConfig.CLIENT_SPEC,
                "functionality-client.toml");

        ConfigEnabledCondition.register();

        ConfigEnabledRegistry.autoRegisterAll();

        ModCreativeTabs.register(modEventBus);
        ModParticles.register(modEventBus);
        ModMenus.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModEntities.register(modEventBus);
        ModFeatures.register(modEventBus);
        ModEffects.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModLootModifiers.register(modEventBus);
        ModSounds.register(modEventBus);

        ModCompatManager.setupModCompatPreInit();

        if (ModCompatManager.farmersDelightPresent)
            FarmersDelightCompat.register(modEventBus);

        if (ModCompatManager.curiosPresent)
            CurioCompat.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::loadComplete);
        modEventBus.addListener(this::onConfigLoad);
        modEventBus.addListener(this::onConfigReload);
    }

    private void onConfigLoad(final ModConfigEvent.Loading event) {
        CauldronUtils.reloadConfig();
        FunctionalityConfig.COMMON.features.throwableDiscs.reloadStats();
    }

    private void onConfigReload(final ModConfigEvent.Reloading event) {
        CauldronUtils.reloadConfig();
        FunctionalityConfig.COMMON.features.throwableDiscs.reloadStats();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    private void loadComplete(final FMLLoadCompleteEvent event) {

    }

    public static final ModelLayerLocation CURIO_ARMOR = new ModelLayerLocation(
            new ResourceLocation(MOD_ID, "curio_armor"), "main");

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

            EntityRenderers.register(ModEntities.OBSIDIAN_BOAT.get(),
                    pContext -> new ObsidianBoatRenderer(pContext, false));

            EntityRenderers.register(ModEntities.AMETHYST_ARROW.get(), AmethystArrowRenderer::new);
            EntityRenderers.register(ModEntities.THROWN_DISC.get(), context -> new ThrownDiscRenderer(context));
            BlockEntityRenderers.register(ModBlockEntities.REPAIR_ALTAR.get(), RepairAltarRenderer::new);

            if (ModCompatManager.curiosPresent) {
                CuriosRendererRegistry.register(CurioCompat.FINS.get(), () -> new CurioArmorLikeRenderer());
                CuriosRendererRegistry.register(CurioCompat.OBSIDIAN_FINS.get(), () -> new CurioArmorLikeRenderer());
                CuriosRendererRegistry.register(CurioCompat.GLOW_CROWN.get(), () -> new CurioArmorLikeRenderer());
            }

            MenuScreens.register(
                    ModMenus.REPAIR_ALTAR_MENU.get(),
                    RepairAltarScreen::new);

            ItemProperties.register(ModItems.NAUTILUS_BUCKET.get(),
                    new ResourceLocation(MOD_ID, "mode"),
                    (stack, level, entity, seed) -> NautilusBucketItem.isLiquidPlacing(stack) ? 1.0F : 0.0F);

            event.enqueueWork(() -> {
                net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(
                        ModBlockEntities.CHUNK_LOADER.get(),
                        ChunkLoaderRenderer::new);
            });
        }

        @SubscribeEvent
        public static void registerParticleProvider(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(ModParticles.GLOW_SMOKE.get(), GlowSmokeParticle.Provider::new);
            event.registerSpriteSet(ModParticles.ANCIENT_SEEKER.get(), AncientSeekerParticle.Provider::new);
            event.registerSpriteSet(ModParticles.BLOOM_LICHEN_AIR.get(),
                    CustomSuspendedParticle.BloomLichenAirProvider::new);
        }

        @SubscribeEvent
        public static void onModelRegistry(ModelEvent.RegisterAdditional event) {
            event.register(new ResourceLocation(FunctionalityMod.MOD_ID, "block/chunk_loader_top"));
        }

        @SubscribeEvent
        public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(InfernoGearHumanoidModel.LAYER_LOCATION,
                    InfernoGearHumanoidModel::createBodyLayer);
            event.registerLayerDefinition(RepairAltarRenderer.LAYER_LOCATION, RepairAltarRenderer::createLayer);
            event.registerLayerDefinition(CURIO_ARMOR, CurioModel::createLayer);
        }
    }
}