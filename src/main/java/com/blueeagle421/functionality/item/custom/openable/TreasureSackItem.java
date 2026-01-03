package com.blueeagle421.functionality.item.custom.openable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.config.subcategories.items.TreasureSack;

public class TreasureSackItem extends OpenableItem {

    @SuppressWarnings("removal")
    private static final ResourceLocation LOOT_TABLE = new ResourceLocation("functionality", "gameplay/treasure_sack");

    public TreasureSackItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected List<ItemStack> getLootStack(ServerLevel server, Player player) {
        LootTable table = server.getServer().getLootData().getLootTable(LOOT_TABLE);

        LootParams.Builder builder = new LootParams.Builder(
                server)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(player.blockPosition()))
                .withParameter(LootContextParams.THIS_ENTITY, player);

        return table.getRandomItems(builder.create(LootContextParamSets.GIFT));
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
