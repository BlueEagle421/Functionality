package com.blueeagle421.functionality.compat;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.item.custom.equipment.FinsItem;
import com.blueeagle421.functionality.item.custom.equipment.GlowCrownItem;
import com.blueeagle421.functionality.item.custom.equipment.InfernoGearItem;
import com.blueeagle421.functionality.item.custom.equipment.ObsidianFinsItem;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.curios.api.CuriosApi;

public class CurioCompat {

    public static void setupCompat() {
        ModCompatManager.curiosPresent = true;
    }

    // ITEMS

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            FunctionalityMod.MOD_ID);

    public static final RegistryObject<Item> GLOW_CROWN = ITEMS
            .register("glow_crown", () -> new GlowCrownItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> FINS = ITEMS
            .register("fins", () -> new FinsItem(new Item.Properties().durability(200)));

    public static final RegistryObject<Item> OBSIDIAN_FINS = ITEMS
            .register("obsidian_fins", () -> new ObsidianFinsItem(new Item.Properties().durability(200)));

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

        public static void playCurioBreakEffects(Player player, ItemStack stack) {
            player.level().playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.ITEM_BREAK,
                    player.getSoundSource(),
                    1.0F,
                    1.0F);

            if (player.level() instanceof ServerLevel serverLevel) {
                int count = 20;
                double spread = 0.2;
                double speed = 0.1;

                double px = player.getX();
                double py = player.getY() + 1.0;
                double pz = player.getZ();

                ItemParticleOption particle = new ItemParticleOption(ParticleTypes.ITEM,
                        new ItemStack(FinsItem.class.cast(stack.getItem())));

                serverLevel.sendParticles(particle, px, py, pz, count, spread, spread, spread, speed);
            }
        }
    }
}
