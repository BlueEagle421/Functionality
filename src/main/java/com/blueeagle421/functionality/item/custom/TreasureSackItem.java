package com.blueeagle421.functionality.item.custom;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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

        if (!(level instanceof ServerLevel serverLevel))
            return InteractionResultHolder.sidedSuccess(stackInHand, true);

        var drops = treasureLootStack(serverLevel, player);

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

        if (!player.getAbilities().instabuild) {
            stackInHand.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(stackInHand, false);
    }

    private List<ItemStack> treasureLootStack(ServerLevel level, Player player) {
        LootTable table = level.getServer().getLootData().getLootTable(LOOT_TABLE);

        LootParams.Builder builder = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(player.blockPosition()))
                .withParameter(LootContextParams.THIS_ENTITY, player);

        return table.getRandomItems(builder.create(LootContextParamSets.GIFT));
    }
}
