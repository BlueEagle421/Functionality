package com.blueeagle421.functionality.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.RisingParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AncientSeekerParticle extends RisingParticle {
    private final SpriteSet sprites;

    public AncientSeekerParticle(ClientLevel level, double x, double y, double z,
            double xSpeed, double ySpeed, double zSpeed,
            SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;
        this.quadSize *= 1.0F;
        this.hasPhysics = false;

        this.lifetime = 20 + level.random.nextInt(240);

        int idx = level.random.nextInt(4);
        float r, g, b;
        switch (idx) {
            case 0 -> {
                r = 0f / 255f;
                g = 146f / 255f;
                b = 149f / 255f;
            }
            case 1 -> {
                r = 10f / 255f;
                g = 80f / 255f;
                b = 96f / 255f;
            }
            case 2 -> {
                r = 41f / 255f;
                g = 223f / 255f;
                b = 235f / 255f;
            }
            default -> {
                r = 5f / 255f;
                g = 42f / 255f;
                b = 50f / 255f;
            } // #052a32
        }

        this.setColor(r, g, b);
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;

        this.pickSprite(this.sprites);
    }

    ParticleRenderType ALWAYS_VISIBLE = new ParticleRenderType() {
        @SuppressWarnings("deprecation")
        public void begin(BufferBuilder bufferBuilder, TextureManager p_107456_) {
            RenderSystem.depthMask(false);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        public void end(Tesselator tesselator) {
            tesselator.end();
        }

        public String toString() {
            return "ALWAYS_VISIBLE";
        }
    };

    @Override
    public ParticleRenderType getRenderType() {
        return ALWAYS_VISIBLE;
    }

    @Override
    public void move(double x, double y, double z) {
        this.setBoundingBox(this.getBoundingBox().move(x, y, z));
        this.setLocationFromBoundingbox();
    }

    @Override
    public float getQuadSize(float pScaleFactor) {
        float f = ((float) this.age + pScaleFactor) / (float) this.lifetime;
        return this.quadSize * (1.0F - f * f * 0.5F);
    }

    @Override
    public int getLightColor(float pPartialTick) {
        return 0xF000F0;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet sprites) {
            this.sprite = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                double xSpeed, double ySpeed, double zSpeed) {

            AncientSeekerParticle p = new AncientSeekerParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprite);

            return p;
        }
    }
}
