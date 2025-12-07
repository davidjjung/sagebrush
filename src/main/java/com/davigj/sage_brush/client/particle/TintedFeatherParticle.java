package com.davigj.sage_brush.client.particle;

import com.davigj.sage_brush.client.TintedFeatherParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class TintedFeatherParticle extends RisingParticle {
    private TintedFeatherParticle(ClientLevel level, double x, double y, double z, double velX, double velY, double velZ, TintedFeatherParticleOptions options) {
        super(level, x, y, z, velX, velY, velZ);
        this.scale(0.6F + (float)level.random.nextInt(5) / 10.0F);
        this.roll = this.oRoll = this.random.nextFloat() * 6.2831855F;
        this.xd = velX * 1.3;
        this.yd = 0.03;
        this.zd = velZ * 1.3;
        this.lifetime = (int)(8.0 / (Math.random() * 0.8 + 0.2)) + 12;

        this.rCol = options.getColor().x();
        this.gCol = options.getColor().y();
        this.bCol = options.getColor().z();
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

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<TintedFeatherParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet p_106441_) {
            this.sprites = p_106441_;
        }

        public Particle createParticle(TintedFeatherParticleOptions p_106443_, ClientLevel p_106444_, double p_106445_, double p_106446_, double p_106447_, double p_106448_, double p_106449_, double p_106450_) {
            TintedFeatherParticle particle = new TintedFeatherParticle(p_106444_, p_106445_, p_106446_, p_106447_, p_106448_, p_106449_, p_106450_, p_106443_);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}
