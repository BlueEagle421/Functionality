package com.blueeagle421.functionality.event;

import java.util.List;
import java.util.Optional;

import com.blueeagle421.functionality.entity.custom.AnvilMarkerEntity;
import com.blueeagle421.functionality.utils.AnvilMarkerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import oshi.util.tuples.Triplet;

public class RepairEvent {
    public static Triplet<Integer, Integer, ItemStack> onRepairEvent(AnvilMenu anvilMenu, ItemStack leftStack,
            ItemStack rightStack, ItemStack outputStack, String itemName, int baseCost, Player player) {

        if (!(player.level() instanceof ServerLevel server))
            return null;

        if (rightStack.getItem().equals(Items.ENCHANTED_BOOK))
            return null;

        if (leftStack.getItem().equals(rightStack.getItem()))
            return null;

        BlockPos anchored = findNearAnvilPos(server, player);

        var found = AnvilMarkerUtils.findMatchingMarkers(server, anchored);
        if (found.isEmpty())
            return null;

        var leftItem = leftStack.getItem();
        if (!leftItem.isValidRepairItem(leftStack, rightStack))
            return null;

        ItemStack newOutput = null;

        int currentDamage = leftStack.getDamageValue();
        int maxDamage = leftStack.getMaxDamage();
        int repairAmount = maxDamage;

        currentDamage -= repairAmount;
        if (currentDamage < 0)
            currentDamage = 0;

        ItemStack newOutputStack = leftStack.copy();
        newOutputStack.setDamageValue(currentDamage);
        newOutput = newOutputStack;

        return new Triplet<Integer, Integer, ItemStack>(1, 1, newOutput);
    }

    private static BlockPos findNearAnvilPos(ServerLevel server, Player player) {
        final double radius = 5.0D;

        AABB search = new AABB(player.blockPosition()).inflate(radius);

        List<AnvilMarkerEntity> nearby = server.getEntitiesOfClass(AnvilMarkerEntity.class, search,
                e -> e.getAttachedPos() != null && server.getBlockState(e.getAttachedPos())
                        .is(net.minecraft.world.level.block.Blocks.ANVIL));

        if (!nearby.isEmpty()) {
            Optional<AnvilMarkerEntity> opt = nearby.stream()
                    .min((a, b) -> Double.compare(a.distanceToSqr(player), b.distanceToSqr(player)));
            if (opt.isPresent())
                return opt.get().getAttachedPos();
        }

        return null;
    }
}
