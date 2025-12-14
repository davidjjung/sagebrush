package com.davigj.sage_brush.core.other.compat;

import com.davigj.sage_brush.core.SBConfig;
import com.davigj.sage_brush.core.other.SBDataMapUtil;
import dev.tazer.mixed_litter.VariantUtil;
import dev.tazer.mixed_litter.variants.Variant;
import io.netty.util.internal.logging.Log4JLogger;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.neoforged.fml.ModList;

import java.util.List;

import static com.davigj.sage_brush.core.SageBrush.LOGGER;
import static com.davigj.sage_brush.core.other.BrushUtil.getCompatParticle;

public class MixedLitterCompat {
    public static boolean MIXED_LITTER = ModList.get().isLoaded("mixed_litter");

    public static ParticleOptions getParticle(SBDataMapUtil.MLVariantMapData mlData, Entity victim, ParticleOptions particle) {
        if (mlData != null) {
            List<Variant> variants = VariantUtil.getVariants(victim);
            if (variants != null && !variants.isEmpty()) {
                Variant variant = variants.getFirst() != null ? variants.getFirst() : null;
                if (variant != null && variant.arguments() != null) {
                    if (variant.arguments().get("texture") != null) {
                        if (SBConfig.COMMON.mLVariantPrint.get()) {
                            LOGGER.debug("[This is a debug feature.] ML Variant name: " + variant.arguments().get("texture").getAsString());
                        }
                        String texture = variant.arguments().get("texture").getAsString();
                        for (SBDataMapUtil.MLVariantMapData.MLVariantData variantData : mlData.mlVariants()) {
                            if (texture.equals(variantData.texture())) {
                                particle = (ParticleOptions) getCompatParticle(variantData.particle()).get();
                            }
                        }
                    }
                }
            }
        }
        return particle;
    }
}
