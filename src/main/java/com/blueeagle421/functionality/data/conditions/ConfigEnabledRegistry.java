package com.blueeagle421.functionality.data.conditions;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.config.FunctionalityConfig;
import net.minecraftforge.common.ForgeConfigSpec;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BooleanSupplier;

public final class ConfigEnabledRegistry {

    private static final Map<String, BooleanSupplier> REGISTRY = new ConcurrentHashMap<>();
    private static final Set<Object> VISITED = new HashSet<>();

    private ConfigEnabledRegistry() {
    }

    public static void register(String configPath, BooleanSupplier supplier) {
        if (configPath == null || configPath.isBlank() || supplier == null)
            return;
        REGISTRY.put(configPath, supplier);
        FunctionalityMod.LOGGER.info("ConfigEnabledRegistry path: " + configPath + ", supplier: " + supplier);
    }

    public static BooleanSupplier get(String configPath) {
        return REGISTRY.get(configPath);
    }

    public static void scanConfig(String parentPath, Object obj) {
        if (obj == null || VISITED.contains(obj))
            return;

        VISITED.add(obj);

        Class<?> clazz = obj.getClass();
        if (!clazz.getPackageName().startsWith("com.blueeagle421.functionality.config"))
            return;

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);

                String fieldName = camelToSnake(field.getName());
                String currentPath = parentPath.isEmpty()
                        ? fieldName
                        : parentPath + "." + fieldName;

                if (value instanceof ForgeConfigSpec.BooleanValue booleanValue) {
                    register(currentPath, booleanValue::get);
                } else if (value != null
                        && !field.getType().isPrimitive()
                        && !field.getType().isEnum()
                        && !ForgeConfigSpec.class.isAssignableFrom(field.getType())) {

                    scanConfig(currentPath, value);
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static void autoRegisterAll() {
        VISITED.clear();
        scanConfig("", FunctionalityConfig.COMMON);
    }

    private static String camelToSnake(String input) {
        if (input == null || input.isEmpty())
            return input;
        return input
                .replaceAll("([a-z0-9])([A-Z])", "$1_$2")
                .toLowerCase();
    }
}
