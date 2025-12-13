package com.blueeagle421.functionality.mixin;

import com.blueeagle421.functionality.event.CauldronEventHandler;
import com.blueeagle421.functionality.utils.CauldronUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LayeredCauldronBlock.class)
public class LayeredCauldronBlockMixin {

    @Inject(method = "lowerFillLevel(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V", at = @At("TAIL"))
    private static void onLowerFillLevel(BlockState pState, Level pLevel, BlockPos pPos, CallbackInfo ci) {

        if (pLevel == null)
            return;

        if (!(pLevel instanceof ServerLevel serverLevel))
            return;

        if (!CauldronUtils.isInfiniteWaterSource(serverLevel, pPos))
            return;

        CauldronEventHandler.scheduleFill(serverLevel, pPos);
    }

}
