package com.blueeagle421.functionality.item.custom;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.config.subcategories.items.TreasureSack;

public class TreasureSackItem extends TooltipItem {

    @SuppressWarnings("removal")
    private static final ResourceLocation LOOT_TABLE = new ResourceLocation("functionality", "gameplay/treasure_sack");

    public TreasureSackItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stackInHand = player.getItemInHand(hand);

        if (level.isClientSide)
            return InteractionResultHolder.sidedSuccess(stackInHand, true);

        if (!(level instanceof ServerLevel server))
            return InteractionResultHolder.sidedSuccess(stackInHand, true);

        var drops = treasureLootStack(server, player);

        for (ItemStack drop : drops) {
            if (drop.isEmpty())
                continue;

            boolean added = player.getInventory().add(drop.copy());

            if (!added) {
                ItemEntity itemEntity = new ItemEntity(level, player.getX(), player.getY() + 0.5, player.getZ(),
                        drop.copy());
                level.addFreshEntity(itemEntity);
            }
        }

        dropXP(server, player);

        if (!player.getAbilities().instabuild)
            stackInHand.shrink(1);

        return InteractionResultHolder.sidedSuccess(stackInHand, false);
    }

    private void dropXP(ServerLevel server, Player player) {

        if (server.random.nextFloat() > config().dropXPChance.get())
            return;

        int base = config().baseXPValue.get();
        int extra = config().extraXPValue.get();

        int xpAmount = base;

        if (extra > 0) {
            xpAmount += server.random.nextInt(extra);
            xpAmount += server.random.nextInt(extra);
        }

        if (xpAmount > 0)
            ExperienceOrb.award(server, player.position(), xpAmount);
    }

    private List<ItemStack> treasureLootStack(ServerLevel level, Player player) {
        LootTable table = level.getServer().getLootData().getLootTable(LOOT_TABLE);

        LootParams.Builder builder = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(player.blockPosition()))
                .withParameter(LootContextParams.THIS_ENTITY, player);

        return table.getRandomItems(builder.create(LootContextParamSets.GIFT));
    }

    private TreasureSack config() {
        return FunctionalityConfig.COMMON.items.treasureSack;
    }
}
