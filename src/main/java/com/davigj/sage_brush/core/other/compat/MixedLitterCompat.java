package com.davigj.sage_brush.core.other.compat;

import com.davigj.sage_brush.core.SBConfig;
import com.davigj.sage_brush.core.other.SBDataMapUtil;
import dev.tazer.mixed_litter.VariantUtil;
import dev.tazer.mixed_litter.variants.Variant;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.neoforged.fml.ModList;

import java.util.List;

import static com.davigj.sage_brush.core.SageBrush.LOGGER;
import static com.davigj.sage_brush.core.other.BrushUtil.getCompatParticle;

public class MixedLitterCompat {

    public static ParticleOptions getParticle(SBDataMapUtil.MLVariantMapData mlData, Entity victim, ParticleOptions particle) {
        if (mlData != null) {
            List<Variant> variants = VariantUtil.getVariants(victim);
            if (variants != null && !variants.isEmpty()) {
                Variant variant = variants.getFirst() != null ? variants.getFirst() : null;
                if (variant != null && variant.arguments() != null) {
                    if (variant.arguments().get("texture") != null) {
                        if (SBConfig.COMMON.mLVariantPrint.get()) {
                            LOGGER.debug("[This is a debug feature.] ML Variant name: " + variant.arguments().get("texture").getAsString());
                            if (variant.arguments().get("baby_texture") != null) {
                                LOGGER.debug("[This is a debug feature.] ML babyTexture name: " + variant.arguments().get("baby_texture").getAsString());
                            }
                        }
                        String texture = variant.arguments().get("texture").getAsString();
                        for (SBDataMapUtil.MLVariantMapData.MLVariantData variantData : mlData.mlVariants()) {
                            if (texture.equals(variantData.texture())) {
                                particle = (ParticleOptions) getCompatParticle(variantData.particle()).get();
                            }
                        }
                        if (!mlData.babyTexture().equals("null") && victim instanceof Mob mob && mob.isBaby() && variant.arguments().get("baby_texture") != null) {
                            if (mlData.babyTexture().equals(variant.arguments().get("baby_texture").getAsString())) {
                                particle = (ParticleOptions) getCompatParticle(mlData.babyTextureParticle()).get();
                            }
                        }
                    }
                }
            }
        }
        return particle;
    }
}
