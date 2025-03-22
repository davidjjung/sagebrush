package com.davigj.sage_brush.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class EnlargedBlossomParticle extends BlossomParticle {
    private EnlargedBlossomParticle(ClientLevel level, double x, double y, double z, double velX, double velY, double velZ) {
        super(level, x, y, z, velX, velY, velZ);
        this.scale(1.9F + (float)level.random.nextInt(4) / 10.0F);
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel world, double x, double y, double z, double velX, double velY, double velZ) {
            EnlargedBlossomParticle particle = new EnlargedBlossomParticle(world, x, y, z, velX, velY, velZ);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}
