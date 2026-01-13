package com.blueeagle421.functionality.compat;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.client.ModModelLayers;
import com.blueeagle421.functionality.client.renderer.CurioArmorLikeRenderer;
import com.blueeagle421.functionality.client.renderer.CurioArmorLikeRenderer.CurioModel;
import com.blueeagle421.functionality.client.renderer.CurioInfernoGearRenderer;
import com.blueeagle421.functionality.item.custom.curio.FinsItem;
import com.blueeagle421.functionality.item.custom.curio.GlowCrownItem;
import com.blueeagle421.functionality.item.custom.curio.InfernoGearItem;
import com.blueeagle421.functionality.item.custom.curio.ObsidianFinsItem;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

public class CurioCompat {

    public static void setupCompat() {
        ModCompatManager.curiosPresent = true;
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
        CuriosRendererRegistry.register(
                CurioCompat.FINS.get(),
                CurioArmorLikeRenderer::new);
        CuriosRendererRegistry.register(
                CurioCompat.OBSIDIAN_FINS.get(),
                CurioArmorLikeRenderer::new);
        CuriosRendererRegistry.register(
                CurioCompat.GLOW_CROWN.get(),
                CurioArmorLikeRenderer::new);
        CuriosRendererRegistry.register(
                CurioCompat.INFERNO_GEAR.get(),
                CurioInfernoGearRenderer::new);
    }

    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.CURIO_ARMOR, CurioModel::createLayer);
    }

    // ITEMS

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            FunctionalityMod.MOD_ID);

    public static final RegistryObject<Item> GLOW_CROWN = ITEMS
            .register("glow_crown", () -> new GlowCrownItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> FINS = ITEMS
            .register("fins", () -> new FinsItem(new Item.Properties().durability(200)));

    public static final RegistryObject<Item> OBSIDIAN_FINS = ITEMS
            .register("obsidian_fins", () -> new ObsidianFinsItem(new Item.Properties().durability(300)));

    public static final RegistryObject<Item> INFERNO_GEAR = ITEMS
            .register("inferno_gear",
                    () -> new InfernoGearItem(new Item.Properties().durability(600)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public class Utils {

        public static boolean hasInfernoGear(Entity entity) {
            return getCurio(entity, InfernoGearItem.class) != null;
        }

        public static boolean hasObsidianFins(Entity entity) {
            return getCurio(entity, ObsidianFinsItem.class) != null;
        }

        public static ItemStack getCurio(Entity entity, Class<? extends Item> itemType) {
            if (!ModCompatManager.curiosPresent)
                return null;

            if (!(entity instanceof LivingEntity living))
                return null;

            return CuriosApi.getCuriosInventory(living)
                    .resolve()
                    .map(curios -> {
                        IItemHandler handler = curios.getEquippedCurios();
                        for (int i = 0; i < handler.getSlots(); i++) {
                            ItemStack stack = handler.getStackInSlot(i);
                            if (!stack.isEmpty() && itemType.isInstance(stack.getItem()))
                                return stack;
                        }
                        return null;
                    })
                    .orElse(null);
        }

        public static void durabilityTick(LivingEntity entity, ItemStack stack, int lastsForTicks, String tag) {

            int maxDurability = stack.getMaxDamage();

            if (maxDurability <= 0 || lastsForTicks <= 0)
                return;

            int ticksSwimming = stack.getOrCreateTag().getInt(tag);
            ticksSwimming++;
            stack.getOrCreateTag().putInt(tag, ticksSwimming);

            double damagePerTick = (double) maxDurability / lastsForTicks;
            int damageShouldBe = (int) Math.floor(ticksSwimming * damagePerTick);
            int currentDamage = stack.getDamageValue();

            int damageToApply = damageShouldBe - currentDamage;

            hurtAndBreak(entity, stack, damageToApply);
        }

        public static void hurtAndBreak(LivingEntity e, ItemStack stack, int damage) {
            stack.hurtAndBreak(damage, e, p -> {
                CurioCompat.Utils.playCurioBreakEffects(e, stack);
            });
        }

        public static void playCurioBreakEffects(Entity entity, ItemStack stack) {
            entity.level().playSound(
                    null,
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    SoundEvents.ITEM_BREAK,
                    entity.getSoundSource(),
                    1.0F,
                    1.0F);

            if (entity.level() instanceof ServerLevel serverLevel) {
                int count = 20;
                double spread = 0.2;
                double speed = 0.1;

                double px = entity.getX();
                double py = entity.getY() + 1.0;
                double pz = entity.getZ();

                ItemParticleOption particle = new ItemParticleOption(
                        ParticleTypes.ITEM,
                        stack.copy());

                serverLevel.sendParticles(particle, px, py, pz, count, spread, spread, spread, speed);
            }
        }
    }
}
