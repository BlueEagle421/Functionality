package com.blueeagle421.functionality.utils;

import java.util.List;

import com.blueeagle421.functionality.entity.custom.AnvilMarkerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;

public class AnvilMarkerUtils {

    public static void spawnMarker(ServerLevel level, BlockPos pos, double yOffset, int initialRepairs) {
        if (level == null)
            return;

        boolean already = !level.getEntitiesOfClass(AnvilMarkerEntity.class, new AABB(pos),
                e -> e.getAttachedPos() != null && e.getAttachedPos().equals(pos)).isEmpty();

        if (already)
            return;

        AnvilMarkerEntity marker = new AnvilMarkerEntity(level, pos, yOffset, initialRepairs);
        level.addFreshEntity(marker);
    }

    public static List<AnvilMarkerEntity> findMatchingMarkers(ServerLevel server, BlockPos anvilPos) {
        List<AnvilMarkerEntity> found = server.getEntitiesOfClass(AnvilMarkerEntity.class,
                new AABB(anvilPos), e -> {
                    BlockPos attached = e.getAttachedPos();
                    return attached != null && attached.equals(anvilPos);
                });

        return found;
    }
}
