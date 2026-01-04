package com.blueeagle421.functionality.item.custom.openable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;

import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.config.subcategories.items.TreasureSack;

public class InfernalSackItem extends OpenableItem {

    public InfernalSackItem(Properties pProperties) {
        super(pProperties);
    }

    @SuppressWarnings("removal")
    @Override
    protected List<ItemStack> getLootStack(ServerLevel server, Player player) {
        ResourceLocation barterTable = new ResourceLocation("minecraft", "gameplay/piglin_bartering");

        LootParams params = new LootParams.Builder(server)
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .create(LootContextParamSets.PIGLIN_BARTER);

        LootTable table = server.getServer().getLootData().getLootTable(barterTable);
        List<ItemStack> result = table.getRandomItems(params);

        return result;
    }

    @Override
    protected float getDropXPChance() {
        return config().dropXPChance.get().floatValue();
    }

    @Override
    protected int getBaseXPValue() {
        return config().baseXPValue.get();
    }

    @Override
    protected int getExtraXPValue() {
        return config().extraXPValue.get();
    }

    private TreasureSack config() {
        return FunctionalityConfig.COMMON.items.treasureSack;
    }
}
