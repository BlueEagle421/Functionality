package com.blueeagle421.functionality.config.categories;

import com.blueeagle421.functionality.config.subcategories.items.*;

import net.minecraftforge.common.ForgeConfigSpec;

public class ItemsCategory {

    public final AncientSeeker ancientSeeker;
    public final BearVenison bearVenison;
    public final Chevon chevon;
    public final FrogLeg frogLeg;
    public final Sniffon sniffon;
    public final Terrapin terrapin;

    public ItemsCategory(ForgeConfigSpec.Builder builder) {
        builder.push("items");

        ancientSeeker = new AncientSeeker(builder);
        bearVenison = new BearVenison(builder);
        chevon = new Chevon(builder);
        frogLeg = new FrogLeg(builder);
        sniffon = new Sniffon(builder);
        terrapin = new Terrapin(builder);

        builder.pop();
    }
}
