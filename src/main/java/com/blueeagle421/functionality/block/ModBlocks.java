package com.blueeagle421.functionality.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.block.custom.FishTrapBlock;
import com.blueeagle421.functionality.block.custom.GlowTorchBlock;
import com.blueeagle421.functionality.block.custom.GlowWallTorchBlock;
import com.blueeagle421.functionality.particle.ModParticles;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
            FunctionalityMod.MOD_ID);

    public static final RegistryObject<Block> GLOW_TORCH = registerBlock("glow_torch",
            () -> new GlowTorchBlock(
                    BlockBehaviour.Properties.of().noCollission().instabreak().lightLevel((p_50755_) -> {
                        return 14;
                    }).sound(SoundType.WOOD), ModParticles.GLOW_FLAME));

    public static final RegistryObject<Block> WALL_GLOW_TORCH = BLOCKS.register("wall_glow_torch",
            () -> new GlowWallTorchBlock(BlockBehaviour.Properties.copy(GLOW_TORCH.get()),
                    ModParticles.GLOW_FLAME));

    public static final RegistryObject<Block> FISH_TRAP = BLOCKS.register("fish_trap",
            () -> new FishTrapBlock(
                    BlockBehaviour.Properties.of().noOcclusion().instabreak().sound(SoundType.BAMBOO)));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        return toReturn;
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}