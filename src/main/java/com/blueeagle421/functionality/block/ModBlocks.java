package com.blueeagle421.functionality.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GlowLichenBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.block.custom.BloomLichenBlock;
import com.blueeagle421.functionality.block.custom.ChunkLoaderBlock;
import com.blueeagle421.functionality.block.custom.FishTrapBlock;
import com.blueeagle421.functionality.block.custom.GlowTorchBlock;
import com.blueeagle421.functionality.block.custom.GlowWallTorchBlock;
import com.blueeagle421.functionality.block.custom.LightningChargerBlock;
import com.blueeagle421.functionality.particle.ModParticles;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
            FunctionalityMod.MOD_ID);

    public static final RegistryObject<Block> GLOW_TORCH = registerBlock("glow_torch",
            () -> new GlowTorchBlock(
                    BlockBehaviour.Properties.of().noCollission().instabreak()

                            .lightLevel((p_50755_) -> {
                                return 14;
                            }).sound(SoundType.WOOD),

                    ModParticles.GLOW_SMOKE));

    public static final RegistryObject<Block> WALL_GLOW_TORCH = BLOCKS.register("wall_glow_torch",
            () -> new GlowWallTorchBlock(BlockBehaviour.Properties.copy(GLOW_TORCH.get()),
                    ModParticles.GLOW_SMOKE));

    public static final RegistryObject<Block> FISH_TRAP = BLOCKS.register("fish_trap",
            () -> new FishTrapBlock(
                    BlockBehaviour.Properties.of().noOcclusion().instabreak()

                            .sound(SoundType.BAMBOO)));

    public static final RegistryObject<Block> LIGHTNING_CHARGER = BLOCKS.register("lightning_charger",
            () -> new LightningChargerBlock(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)));

    public static final RegistryObject<Block> DRY_LICHEN = BLOCKS.register("dry_lichen", () -> new GlowLichenBlock(
            BlockBehaviour.Properties.copy(Blocks.GLOW_LICHEN).strength(0.08F)
                    .lightLevel(GlowLichenBlock.emission(0))));

    public static final RegistryObject<Block> LICHEN = BLOCKS.register("lichen", () -> new GlowLichenBlock(
            BlockBehaviour.Properties.copy(Blocks.GLOW_LICHEN).strength(0.08F)
                    .lightLevel(GlowLichenBlock.emission(5))));

    public static final RegistryObject<Block> BLOOM_LICHEN = BLOCKS.register("bloom_lichen",
            () -> new BloomLichenBlock(
                    BlockBehaviour.Properties.copy(Blocks.GLOW_LICHEN)
                            .strength(0.08F)
                            .lightLevel(GlowLichenBlock.emission(7))));

    public static final RegistryObject<Block> CHUNK_LOADER = BLOCKS.register("chunk_loader",
            () -> new ChunkLoaderBlock(BlockBehaviour.Properties.copy(Blocks.ENCHANTING_TABLE)));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        return toReturn;
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}