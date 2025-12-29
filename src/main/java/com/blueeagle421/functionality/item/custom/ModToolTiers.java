package com.blueeagle421.functionality.item.custom;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.TierSortingRegistry;

import java.util.List;

import com.blueeagle421.functionality.FunctionalityMod;

@SuppressWarnings("removal")
public class ModToolTiers {

    public static final Tier FLINT = TierSortingRegistry.registerTier(
            new ForgeTier(
                    1,
                    130,
                    3.0f,
                    2f,
                    18,
                    Tags.Blocks.NEEDS_WOOD_TOOL,
                    () -> Ingredient.of(Items.FLINT)),
            new ResourceLocation(FunctionalityMod.MOD_ID, "flint"),
            List.of(Tiers.WOOD),
            List.of(Tiers.STONE));

    public static final Tier PRISMARINE = TierSortingRegistry.registerTier(
            new ForgeTier(
                    2,
                    400,
                    5.5f,
                    2.5f,
                    16,
                    Tags.Blocks.NEEDS_GOLD_TOOL,
                    () -> Ingredient.of(Items.PRISMARINE_SHARD)),
            new ResourceLocation(FunctionalityMod.MOD_ID, "prismarine"),
            List.of(Tiers.STONE),
            List.of(Tiers.IRON));
}