package com.davigj.sage_brush.core.other.compat;

import com.davigj.sage_brush.core.SBConfig;
import com.davigj.sage_brush.core.other.SBDataMapUtil;
import com.starfish_studios.naturalist.server.entity.mob.Butterfly;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.neoforged.fml.ModList;

import static com.davigj.sage_brush.core.SageBrush.LOGGER;
import static com.davigj.sage_brush.core.other.BrushUtil.getCompatParticle;

public class NaturalistCompat {
    public static boolean NATURALIST = ModList.get().isLoaded("naturalist");

    public static ParticleOptions getParticle(SBDataMapUtil.NaturalistVariantMapData naturalistData, Entity victim, ParticleOptions particle) {
        if (victim instanceof Butterfly butterfly) {
            String variantPath = butterfly.getVariant().getName();
            if (variantPath != null) {
                if (SBConfig.COMMON.variantPrint.get()) {
                    LOGGER.debug("[This is a debug feature, enabled in the config.] Variant name: " + variantPath);
                }
                for (SBDataMapUtil.NaturalistVariantMapData.NaturalistVariantData variantData : naturalistData.variants()) {
                    if (variantPath.equals(variantData.texture())) {
                        particle = (ParticleOptions) getCompatParticle(variantData.particle()).get();
                    }
                }
            }
        }

        return particle;
    }

    public static String getVariantPath(Entity victim) {
        if (victim instanceof Butterfly butterfly) {
            String variantPath = butterfly.getVariant().getName();
            return variantPath;
        }
        return "null";
    }
}
