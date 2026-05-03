package com.blueeagle421.functionality.config.subcategories.features;

import com.blueeagle421.functionality.FunctionalityMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.*;

public class Tooltips {
    public final ForgeConfigSpec.BooleanValue enabled;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> itemBlacklist;
    public final ForgeConfigSpec.BooleanValue blacklistInverted;

    public Tooltips(ForgeConfigSpec.Builder builder) {
        builder.push("tooltips");

        enabled = builder
                .comment("If false no item in the mod will have it's tooltip displayed.")
                .define("enabled", true);

        List<String> defaults = Arrays.asList();

        this.itemBlacklist = builder
                .comment("List of item IDs (namespace:path) that should not have their tooltip displayed.")
                .defineList("itemBlacklist", defaults, o -> o instanceof String);

        blacklistInverted = builder
                .comment("If true, all items in the mod will not have their tooltips unless on blacklist.")
                .define("blacklistInverted", false);

        builder.pop();
    }

    @SuppressWarnings("removal")
    public Set<ResourceLocation> getItemsAsResourceLocations() {
        List<? extends String> rawList = this.itemBlacklist.get();
        if (rawList == null || rawList.isEmpty())
            return Collections.emptySet();

        Set<ResourceLocation> out = new HashSet<>();
        for (String raw : rawList) {
            if (raw == null)
                continue;
            String s = raw.trim();

            if (s.isEmpty())
                continue;

            try {
                ResourceLocation rl = new ResourceLocation(s);
                out.add(rl);
            } catch (Exception e) {
                FunctionalityMod.LOGGER.warn("Invalid item id in config itemBlacklist: '{}'. Ignoring.", raw);
            }
        }
        return out;
    }
}
