package com.davigj.sage_brush.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class FeatherParticle extends RisingParticle {
    private FeatherParticle(ClientLevel level, double x, double y, double z, double velX, double velY, double velZ) {
        super(level, x, y, z, velX, velY, velZ);
        this.scale(0.6F + (float)level.random.nextInt(6) / 10.0F);
        this.roll = this.oRoll = this.random.nextFloat() * 6.2831855F;
        this.xd = velX * 1.3;
        this.yd = 0.03;
        this.zd = velZ * 1.3;
        this.lifetime = (int)(8.0 / (Math.random() * 0.8 + 0.2)) + 12;
    }

    public void tick() {
        if (this.age > this.lifetime / 2) {
            this.setAlpha(1.0F - ((float)this.age - (float)(this.lifetime / 2)) / (float)this.lifetime);
        }

        super.tick();
        if (this.age == 1) {
            this.yd = 0.03 + (double)this.random.nextInt(3) / 100.0;
        } else if (this.age <= 10) {
            this.yd -= 0.012;
        }

        if (this.onGround) {
            this.setParticleSpeed(0.0, 0.0, 0.0);
            this.setPos(this.xo, this.yo + 0.05, this.zo);
        }

    }

    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel world, double x, double y, double z, double velX, double velY, double velZ) {
            FeatherParticle particle = new FeatherParticle(world, x, y, z, velX, velY, velZ);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}
