package com.blueeagle421.functionality.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blueeagle421.functionality.block.ModBlocks;
import com.blueeagle421.functionality.config.FunctionalityConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.RandomSource;

@Mixin(MultifaceSpreader.DefaultSpreaderConfig.class)
public class MixinMultifaceSpreader_DefaultSpreaderConfig {

    @Inject(method = "getStateForPlacement", at = @At("RETURN"), cancellable = true)
    private void onGetStateForPlacement(BlockState currentState, BlockGetter level, BlockPos pos,
            Direction lookingDirection, CallbackInfoReturnable<BlockState> cir) {

        BlockState returned = cir.getReturnValue();
        var config = FunctionalityConfig.COMMON.features.betterLichens;

        if (!config.enabled.get() || returned == null || !returned.is(Blocks.GLOW_LICHEN))
            return;

        RandomSource random = RandomSource.create();
        if (random.nextFloat() <= config.lichenOverrideChance.get()) {
            cir.setReturnValue(ModBlocks.BLOOM_LICHEN.get().defaultBlockState());
        }
    }
}