package com.blueeagle421.functionality.block.custom;

import com.blueeagle421.functionality.particle.ModParticles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.GlowLichenBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BloomLichenBlock extends GlowLichenBlock {

    public BloomLichenBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
        if (!(level instanceof ClientLevel))
            return;
        ClientLevel client = (ClientLevel) level;

        final int ATTEMPTS = 2;
        final int RADIUS = 1;
        for (int i = 0; i < ATTEMPTS; ++i) {
            BlockPos target = pos.offset(
                    Mth.nextInt(rand, -RADIUS, RADIUS),
                    -rand.nextInt(10),
                    Mth.nextInt(rand, -RADIUS, RADIUS));

            BlockState blockstate = client.getBlockState(target);
            if (!blockstate.isCollisionShapeFullBlock(client, target)) {
                double px = target.getX() + rand.nextDouble();
                double py = target.getY() + rand.nextDouble();
                double pz = target.getZ() + rand.nextDouble();
                client.addParticle(ModParticles.BLOOM_LICHEN_AIR.get(), px, py, pz, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}