package com.blueeagle421.functionality.client.particle;

import java.util.Optional;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleGroup;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CustomSuspendedParticle extends TextureSheetParticle {
    CustomSuspendedParticle(ClientLevel pLevel, SpriteSet pSprites, double pX, double pY, double pZ, double pXSpeed,
            double pYSpeed, double pZSpeed) {
        super(pLevel, pX, pY - 0.125D, pZ, pXSpeed, pYSpeed, pZSpeed);
        this.setSize(0.01F, 0.01F);
        this.pickSprite(pSprites);
        this.quadSize *= this.random.nextFloat() * 0.6F + 0.6F;
        this.lifetime = (int) (16.0D / (Math.random() * 0.8D + 0.2D));
        this.hasPhysics = false;
        this.friction = 1.0F;
        this.gravity = 0.0F;
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public int getLightColor(float partialTick) {
        float ageFactor = 1.0F - ((float) this.age / (float) this.lifetime);
        int light = (int) (15 * ageFactor);
        return (light << 20) | (light << 4);
    }

    @OnlyIn(Dist.CLIENT)
    public static class BloomLichenAirProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public BloomLichenAirProvider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ,
                double pXSpeed, double pYSpeed, double pZSpeed) {
            CustomSuspendedParticle particle = new CustomSuspendedParticle(pLevel, this.sprite, pX, pY, pZ,
                    0.0D,
                    (double) -0.8F, 0.0D) {
                public Optional<ParticleGroup> getParticleGroup() {
                    return Optional.of(ParticleGroup.SPORE_BLOSSOM);
                }
            };
            particle.lifetime = Mth.randomBetweenInclusive(pLevel.random, 500, 1000);
            particle.gravity = 0.01F;
            particle.setColor(0.9F, 0.89F, 0.77F);
            return particle;
        }
    }
}