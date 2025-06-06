package com.davigj.sage_brush.client.particle;

import com.davigj.sage_brush.client.BrushDustParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BrushDustParticle extends ExplodeParticle {
    protected BrushDustParticle(ClientLevel p_106576_, double p_106577_, double p_106578_, double p_106579_, double p_106580_, double p_106581_, double p_106582_, BrushDustParticleOptions p_106443_, SpriteSet p_106583_) {
        super(p_106576_, p_106577_, p_106578_, p_106579_, p_106580_, p_106581_, p_106582_, p_106583_);
        float f = this.random.nextFloat() * 0.4F + 0.6F;
        this.friction = 0.96F;
        this.gravity = -0.05F;
        this.rCol = this.randomizeColor(p_106443_.getColor().x(), f);
        this.gCol = this.randomizeColor(p_106443_.getColor().y(), f);
        this.bCol = this.randomizeColor(p_106443_.getColor().z(), f);
//        this.quadSize = 0.1F + (this.random.nextFloat() * 0.1F);
        this.quadSize = 0.1F * (this.random.nextFloat() * 1.5F + 1.0F);
    }

    protected float randomizeColor(float p_172105_, float p_172106_) {
        return (this.random.nextFloat() * 0.2F + 0.8F) * p_172105_ * p_172106_;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<BrushDustParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet p_106441_) {
            this.sprites = p_106441_;
        }

        public Particle createParticle(BrushDustParticleOptions p_106443_, ClientLevel p_106444_, double p_106445_, double p_106446_, double p_106447_, double p_106448_, double p_106449_, double p_106450_) {
            return new BrushDustParticle(p_106444_, p_106445_, p_106446_, p_106447_, p_106448_, p_106449_, p_106450_, p_106443_, this.sprites);
        }
    }
}
