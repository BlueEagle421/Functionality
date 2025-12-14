package com.blueeagle421.functionality.data.conditions;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import java.util.function.BooleanSupplier;

public class ConfigEnabledCondition implements ICondition {

    public static final ResourceLocation ID = new ResourceLocation("functionality", "enabled");

    private final String configPath;

    public ConfigEnabledCondition(String configPath) {
        this.configPath = configPath;
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public boolean test(IContext context) {
        BooleanSupplier supplier = ConfigEnabledRegistry.get(configPath);
        return supplier == null || supplier.getAsBoolean();
    }

    public static class Serializer implements IConditionSerializer<ConfigEnabledCondition> {

        @Override
        public void write(JsonObject json, ConfigEnabledCondition value) {
            json.addProperty("configPath", value.configPath);
        }

        @Override
        public ConfigEnabledCondition read(JsonObject json) {
            String path = json.has("configPath") ? json.get("configPath").getAsString() : "";
            return new ConfigEnabledCondition(path);
        }

        @Override
        public ResourceLocation getID() {
            return ConfigEnabledCondition.ID;
        }
    }

    public static void register() {
        CraftingHelper.register(new Serializer());
    }
}
