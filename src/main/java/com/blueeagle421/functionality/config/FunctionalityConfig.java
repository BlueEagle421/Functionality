package com.blueeagle421.functionality.config;

import com.blueeagle421.functionality.config.categories.FeaturesCategory;
import com.blueeagle421.functionality.config.categories.ItemsCategory;

import net.minecraftforge.common.ForgeConfigSpec;

public class FunctionalityConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final Client CLIENT;

    static {
        ForgeConfigSpec.Builder commonBuilder = new ForgeConfigSpec.Builder();
        COMMON = new Common(commonBuilder);
        COMMON_SPEC = commonBuilder.build();

        ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();
        CLIENT = new Client(clientBuilder);
        CLIENT_SPEC = clientBuilder.build();
    }

    public static class Common {

        public final ItemsCategory items;
        public final FeaturesCategory features;

        Common(ForgeConfigSpec.Builder builder) {
            builder.push("general");

            builder.pop();

            items = new ItemsCategory(builder);
            features = new FeaturesCategory(builder);
        }
    }

    public static class Client {

        Client(ForgeConfigSpec.Builder builder) {
            builder.push("general");

            builder.pop();
        }
    }
}
