package com.blueeagle421.functionality.datagen;

import com.blueeagle421.functionality.FunctionalityMod;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

public class ModGlobalLootModifiersProvider extends GlobalLootModifierProvider {
        public ModGlobalLootModifiersProvider(PackOutput output) {
                super(output, FunctionalityMod.MOD_ID);
        }

        @Override
        protected void start() {

        }
}
