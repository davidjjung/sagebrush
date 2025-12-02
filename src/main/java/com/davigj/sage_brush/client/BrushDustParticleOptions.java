package com.davigj.sage_brush.client;

import com.davigj.sage_brush.core.registry.SBParticleTypes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ScalableParticleOptionsBase;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;

public class BrushDustParticleOptions extends ScalableParticleOptionsBase {
    public static final MapCodec<BrushDustParticleOptions> CODEC = RecordCodecBuilder.mapCodec(
            p_341566_ -> p_341566_.group(
                            ExtraCodecs.VECTOR3F.fieldOf("color").forGetter(p_253371_ -> p_253371_.color),
                            SCALE.fieldOf("scale").forGetter(ScalableParticleOptionsBase::getScale)
                    )
                    .apply(p_341566_, BrushDustParticleOptions::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, BrushDustParticleOptions> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VECTOR3F, p_319429_ -> p_319429_.color, ByteBufCodecs.FLOAT, ScalableParticleOptionsBase::getScale, BrushDustParticleOptions::new
    );
    private final Vector3f color;

    public BrushDustParticleOptions(Vector3f color, float scale) {
        super(scale);
        this.color = color;
    }

    @Override
    public ParticleType<BrushDustParticleOptions> getType() {
        return SBParticleTypes.DUST.get();
    }

    public Vector3f getColor() {
        return this.color;
    }
}
