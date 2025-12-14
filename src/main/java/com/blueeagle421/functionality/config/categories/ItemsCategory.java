package com.blueeagle421.functionality.config.categories;

import com.blueeagle421.functionality.config.subcategories.items.*;

import net.minecraftforge.common.ForgeConfigSpec;

public class ItemsCategory {

    public final AmethystArrow amethystArrow;
    public final AncientSeeker ancientSeeker;
    public final BearVenison bearVenison;
    public final Chevon chevon;
    public final FrogLeg frogLeg;
    public final Sniffon sniffon;
    public final Terrapin terrapin;
    public final ChorusHerb chorusHerb;
    public final CrimsonHerb crimsonHerb;
    public final Fins fins;
    public final FishTrap fishTrap;
    public final GlowCrown glowCrown;
    public final GlowHerb glowHerb;
    public final GlowTorch glowTorch;
    public final Harpoon harpoon;
    public final InfernoGear infernoGear;
    public final LightningCharger lightningCharger;
    public final ObsidianBoat obsidianBoat;
    public final ObsidianFins obsidianFins;
    public final PhantomHerb phantomHerb;
    public final PhantomLead phantomLead;
    public final PhantomRocket phantomRocket;
    public final PhantomTreat phantomTreat;
    public final Pheromones pheromones;
    public final SleepingHerb sleepingHerb;
    public final TreasureSack treasureSack;
    public final DiscShards discShards;

    public ItemsCategory(ForgeConfigSpec.Builder builder) {
        builder.push("items");

        amethystArrow = new AmethystArrow(builder);
        ancientSeeker = new AncientSeeker(builder);
        bearVenison = new BearVenison(builder);
        chevon = new Chevon(builder);
        frogLeg = new FrogLeg(builder);
        sniffon = new Sniffon(builder);
        terrapin = new Terrapin(builder);
        chorusHerb = new ChorusHerb(builder);
        crimsonHerb = new CrimsonHerb(builder);
        fins = new Fins(builder);
        fishTrap = new FishTrap(builder);
        glowCrown = new GlowCrown(builder);
        glowHerb = new GlowHerb(builder);
        glowTorch = new GlowTorch(builder);
        harpoon = new Harpoon(builder);
        infernoGear = new InfernoGear(builder);
        lightningCharger = new LightningCharger(builder);
        obsidianBoat = new ObsidianBoat(builder);
        obsidianFins = new ObsidianFins(builder);
        phantomHerb = new PhantomHerb(builder);
        phantomLead = new PhantomLead(builder);
        phantomRocket = new PhantomRocket(builder);
        phantomTreat = new PhantomTreat(builder);
        pheromones = new Pheromones(builder);
        sleepingHerb = new SleepingHerb(builder);
        treasureSack = new TreasureSack(builder);
        discShards = new DiscShards(builder);

        builder.pop();
    }
}
