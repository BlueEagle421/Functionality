package com.blueeagle421.functionality.config.data;

import java.util.Locale;
import java.util.Optional;

import com.blueeagle421.functionality.FunctionalityMod;

public class DiscSpec {
    public final String itemId; // namespace:item_name
    public final float damage;
    public final double range;

    public DiscSpec(String itemId, float damage, double range) {
        this.itemId = itemId.toLowerCase(Locale.ROOT);
        this.damage = damage;
        this.range = range;
    }

    public static Optional<DiscSpec> parse(String raw, double defaultRange) {
        try {
            String trimmed = raw.trim();
            if (trimmed.isEmpty())
                return Optional.empty();

            String[] parts = trimmed.split(";", 3);
            if (parts.length < 2)
                return Optional.empty(); // need at least item;damage

            String itemId = parts[0].trim();
            float damage = Float.parseFloat(parts[1].trim());
            double range = parts.length >= 3 ? Double.parseDouble(parts[2].trim()) : defaultRange;

            return Optional.of(new DiscSpec(itemId, damage, range));
        } catch (Exception e) {
            FunctionalityMod.LOGGER.warn("Failed to parse disc line '{}': {}", raw, e.toString());
            return Optional.empty();
        }
    }
}
