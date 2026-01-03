package com.blueeagle421.functionality.item.custom.openable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class VexEssenceItem extends OpenableItem {

    public VexEssenceItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected List<ItemStack> getLootStack(ServerLevel server, Player player) {
        return List.of();
    }

    @Override
    protected float getDropXPChance() {
        return 1f;
    }

    @Override
    protected int getBaseXPValue() {
        return 7;
    }

    @Override
    protected int getExtraXPValue() {
        return 6;
    }
}
