package com.blueeagle421.functionality.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BaseAshSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.FastColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GlowSmokeParticle extends BaseAshSmokeParticle {

    protected GlowSmokeParticle(ClientLevel pLevel, double pX, double pY, double pZ,
            double pXSpeed, double pYSpeed, double pZSpeed,
            float pQuadSizeMultiplier, SpriteSet pSprites, int packedColor) {

        super(pLevel, pX, pY, pZ, 0.1F, 0.1F, 0.1F,
                pXSpeed, pYSpeed, pZSpeed, pQuadSizeMultiplier, pSprites,
                0.3F, 8, -0.1F, true);

        // +-7%
        float jitter = (float) (Math.random() * 0.14 - 0.07);

        float r = (FastColor.ARGB32.red(packedColor) / 255f) * (1.0f + jitter);
        float g = (FastColor.ARGB32.green(packedColor) / 255f) * (1.0f + jitter);
        float b = (FastColor.ARGB32.blue(packedColor) / 255f) * (1.0f + jitter);

        // clamp
        r = Math.min(1.0f, Math.max(0.0f, r));
        g = Math.min(1.0f, Math.max(0.0f, g));
        b = Math.min(1.0f, Math.max(0.0f, b));

        this.setColor(r, g, b);
        this.setAlpha(1.0F);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float partialTick) {
        return 15728880;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        private final int packedColor;

        public Provider(SpriteSet sprites) {
            this(sprites, FastColor.ARGB32.color(255, 152, 255, 204));
        }

        public Provider(SpriteSet sprites, int packedColor) {
            this.sprites = sprites;
            this.packedColor = packedColor;
        }

        @Override
        public Particle createParticle(SimpleParticleType pType, ClientLevel level,
                double x, double y, double z,
                double xd, double yd, double zd) {
            return new GlowSmokeParticle(level, x, y, z, xd, yd, zd, 1.0F, this.sprites, this.packedColor);
        }
    }
}
