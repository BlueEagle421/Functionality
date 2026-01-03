package com.blueeagle421.functionality.item.custom.openable;

import java.util.List;

import com.blueeagle421.functionality.item.custom.TooltipItem;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class OpenableItem extends TooltipItem {

    public OpenableItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stackInHand = player.getItemInHand(hand);

        if (level.isClientSide)
            return InteractionResultHolder.sidedSuccess(stackInHand, true);

        if (!(level instanceof ServerLevel server))
            return InteractionResultHolder.sidedSuccess(stackInHand, true);

        var drops = getLootStack(server, player);

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

        if (server.random.nextFloat() > getDropXPChance())
            return;

        int extra = getExtraXPValue();
        int xpAmount = getBaseXPValue();

        if (extra > 0) {
            xpAmount += server.random.nextInt(extra);
            xpAmount += server.random.nextInt(extra);
        }

        if (xpAmount > 0)
            ExperienceOrb.award(server, player.position(), xpAmount);
    }

    protected abstract List<ItemStack> getLootStack(ServerLevel server, Player player);

    protected abstract float getDropXPChance();

    protected abstract int getBaseXPValue();

    protected abstract int getExtraXPValue();

}
