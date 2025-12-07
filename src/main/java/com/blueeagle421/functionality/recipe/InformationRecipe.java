package com.blueeagle421.functionality.recipe;

import com.blueeagle421.functionality.FunctionalityMod;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class InformationRecipe implements Recipe<SimpleContainer> {
    private final NonNullList<Ingredient> inputs;
    private final NonNullList<ItemStack> outputs;
    private final Component description;
    private final ResourceLocation id;

    public InformationRecipe(NonNullList<Ingredient> inputs, NonNullList<ItemStack> outputs, Component description,
            ResourceLocation id) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.description = description;
        this.id = id;
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        if (pLevel.isClientSide())
            return false;

        for (Ingredient ingredient : inputs) {
            boolean matched = false;
            for (int slot = 0; slot < pContainer.getContainerSize(); slot++) {
                if (ingredient.test(pContainer.getItem(slot))) {
                    matched = true;
                    break;
                }
            }
            if (!matched)
                return false;
        }

        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return inputs;
    }

    public NonNullList<ItemStack> getOutputs() {
        return outputs;
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        return outputs.isEmpty() ? ItemStack.EMPTY : outputs.get(0).copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return assemble(null, pRegistryAccess);
    }

    public Component getDescription() {
        return description;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<InformationRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "information";
    }

    public static class Serializer implements RecipeSerializer<InformationRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        @SuppressWarnings("removal")
        public static final ResourceLocation ID = new ResourceLocation(FunctionalityMod.MOD_ID, Type.ID);

        @Override
        public InformationRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            JsonArray ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(ingredients.size(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            NonNullList<ItemStack> outputs = NonNullList.withSize(inputs.size(), ItemStack.EMPTY);
            for (int i = 0; i < inputs.size(); i++) {
                Ingredient ing = inputs.get(i);
                ItemStack[] possible = ing.getItems();
                outputs.set(i, (possible != null && possible.length > 0) ? possible[0].copy() : ItemStack.EMPTY);
            }

            String descKey = GsonHelper.getAsString(pSerializedRecipe, "description", "");
            Component description = descKey.isEmpty() ? Component.empty() : Component.translatable(descKey);

            return new InformationRecipe(inputs, outputs, description, pRecipeId);
        }

        @Override
        public @Nullable InformationRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            int inSize = pBuffer.readInt();
            NonNullList<Ingredient> inputs = NonNullList.withSize(inSize, Ingredient.EMPTY);
            for (int i = 0; i < inSize; i++) {
                inputs.set(i, Ingredient.fromNetwork(pBuffer));
            }

            int outSize = pBuffer.readInt();
            NonNullList<ItemStack> outputs = NonNullList.withSize(outSize, ItemStack.EMPTY);
            for (int i = 0; i < outSize; i++) {
                outputs.set(i, pBuffer.readItem());
            }

            Component description = pBuffer.readComponent();

            return new InformationRecipe(inputs, outputs, description, pRecipeId);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, InformationRecipe pRecipe) {
            pBuffer.writeInt(pRecipe.inputs.size());
            for (Ingredient ing : pRecipe.getIngredients()) {
                ing.toNetwork(pBuffer);
            }

            pBuffer.writeInt(pRecipe.outputs.size());
            for (ItemStack stack : pRecipe.getOutputs()) {
                pBuffer.writeItemStack(stack, false);
            }

            pBuffer.writeComponent(pRecipe.getDescription());
        }
    }
}
