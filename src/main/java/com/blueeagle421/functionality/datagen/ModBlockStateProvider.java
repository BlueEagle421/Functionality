package com.blueeagle421.functionality.datagen;

import com.blueeagle421.functionality.FunctionalityMod;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BlockStateProvider {
        public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
                super(output, FunctionalityMod.MOD_ID, exFileHelper);
        }

        @Override
        protected void registerStatesAndModels() {

        }

        public void hangingSignBlock(Block signBlock, Block wallSignBlock, ResourceLocation texture) {
                ModelFile sign = models().sign(name(signBlock), texture);
                hangingSignBlock(signBlock, wallSignBlock, sign);
        }

        public void hangingSignBlock(Block signBlock, Block wallSignBlock, ModelFile sign) {
                simpleBlock(signBlock, sign);
                simpleBlock(wallSignBlock, sign);
        }

        private String name(Block block) {
                return key(block).getPath();
        }

        private ResourceLocation key(Block block) {
                return ForgeRegistries.BLOCKS.getKey(block);
        }

        @SuppressWarnings("removal")
        private void leavesBlock(RegistryObject<Block> blockRegistryObject) {
                simpleBlockWithItem(blockRegistryObject.get(),
                                models().singleTexture(
                                                ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath(),
                                                new ResourceLocation("minecraft:block/leaves"),
                                                "all", blockTexture(blockRegistryObject.get())).renderType("cutout"));
        }

        private void blockItem(RegistryObject<Block> blockRegistryObject) {
                simpleBlockItem(blockRegistryObject.get(), new ModelFile.UncheckedModelFile(FunctionalityMod.MOD_ID +
                                ":block/" + ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath()));
        }

        private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
                simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
        }
}
