package com.davigj.sage_brush.client;

import com.davigj.sage_brush.core.registry.SBParticleTypes;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.DustParticleOptionsBase;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class BrushDustParticleOptions extends DustParticleOptionsBase {
    public static final Vector3f REDSTONE_PARTICLE_COLOR = Vec3.fromRGB24(16711680).toVector3f();
    public static final BrushDustParticleOptions REDSTONE = new BrushDustParticleOptions(REDSTONE_PARTICLE_COLOR, 1.0F);
    public static final Codec<BrushDustParticleOptions> CODEC;

    public static final Deserializer<BrushDustParticleOptions> DESERIALIZER = new Deserializer<BrushDustParticleOptions>() {
        public BrushDustParticleOptions fromCommand(ParticleType<BrushDustParticleOptions> p_123689_, StringReader p_123690_) throws CommandSyntaxException {
            Vector3f vector3f = DustParticleOptionsBase.readVector3f(p_123690_);
            p_123690_.expect(' ');
            float f = p_123690_.readFloat();
            return new BrushDustParticleOptions(vector3f, f);
        }

        public BrushDustParticleOptions fromNetwork(ParticleType<BrushDustParticleOptions> p_123692_, FriendlyByteBuf p_123693_) {
            return new BrushDustParticleOptions(DustParticleOptionsBase.readVector3f(p_123693_), p_123693_.readFloat());
        }
    };

    public BrushDustParticleOptions(Vector3f p_175790_, float p_175791_) {
        super(p_175790_, p_175791_);
    }

    public ParticleType<BrushDustParticleOptions> getType() {
        return SBParticleTypes.DUST.get();
    }

    static {
        CODEC = RecordCodecBuilder.create((p_253370_) -> {
            return p_253370_.group(ExtraCodecs.VECTOR3F.fieldOf("color").forGetter((p_253371_) -> {
                return p_253371_.color;
            }), Codec.FLOAT.fieldOf("scale").forGetter((p_175795_) -> {
                return p_175795_.scale;
            })).apply(p_253370_, BrushDustParticleOptions::new);
        });
    }
}