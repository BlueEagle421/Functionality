package com.blueeagle421.functionality.recipe;

import com.blueeagle421.functionality.FunctionalityMod;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister
            .create(ForgeRegistries.RECIPE_SERIALIZERS, FunctionalityMod.MOD_ID);

    public static final RegistryObject<RecipeSerializer<InformationRecipe>> INFORMATION_SERIALIZER = SERIALIZERS
            .register("information", () -> InformationRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}