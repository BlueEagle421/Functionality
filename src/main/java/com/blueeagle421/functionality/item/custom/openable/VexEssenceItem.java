package com.blueeagle421.functionality.item.custom.openable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.config.subcategories.items.VexEssence;

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

    private VexEssence config() {
        return FunctionalityConfig.COMMON.items.vexEssence;
    }
}
