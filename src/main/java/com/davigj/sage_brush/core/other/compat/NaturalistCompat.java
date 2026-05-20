package com.davigj.sage_brush.core.other.compat;

import com.davigj.sage_brush.core.SBConfig;
import com.davigj.sage_brush.core.other.SBDataMapUtil;
import com.starfish_studios.naturalist.server.entity.mob.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.neoforged.fml.ModList;

import static com.davigj.sage_brush.core.SageBrush.LOGGER;
import static com.davigj.sage_brush.core.other.BrushUtil.getCompatParticle;

public class NaturalistCompat {
    public static boolean NATURALIST = ModList.get().isLoaded("naturalist");

    public static ParticleOptions getParticle(SBDataMapUtil.NaturalistVariantMapData naturalistData, Entity victim, ParticleOptions particle) {
        if (naturalistData == null) return particle;
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
        } else if (victim instanceof Dragonfly dragonfly) {
            int variantPath = dragonfly.getVariant();
            if (SBConfig.COMMON.variantPrint.get()) {
                LOGGER.debug("[This is a debug feature, enabled in the config.] Variant name: " + variantPath);
            }
            for (SBDataMapUtil.NaturalistVariantMapData.NaturalistVariantData variantData : naturalistData.variants()) {
                if ((Integer.toString(variantPath)).equals(variantData.texture())) {
                    particle = (ParticleOptions) getCompatParticle(variantData.particle()).get();
                }
            }
        } else if (victim instanceof Lizard lizard) {
            int variantPath = lizard.getVariant();
            if (SBConfig.COMMON.variantPrint.get()) {
                LOGGER.debug("[This is a debug feature, enabled in the config.] Variant name: " + variantPath);
            }
            for (SBDataMapUtil.NaturalistVariantMapData.NaturalistVariantData variantData : naturalistData.variants()) {
                if ((Integer.toString(variantPath)).equals(variantData.texture())) {
                    particle = (ParticleOptions) getCompatParticle(variantData.particle()).get();
                }
            }
        } else if (victim instanceof Tortoise tortoise) {
            int variantPath = tortoise.getVariant();
            if (SBConfig.COMMON.variantPrint.get()) {
                LOGGER.debug("[This is a debug feature, enabled in the config.] Variant name: " + variantPath);
            }
            for (SBDataMapUtil.NaturalistVariantMapData.NaturalistVariantData variantData : naturalistData.variants()) {
                if ((Integer.toString(variantPath)).equals(variantData.texture())) {
                    particle = (ParticleOptions) getCompatParticle(variantData.particle()).get();
                }
            }
        }
        return particle;
    }

    public static String getVariantPath(Entity victim) {
        if (victim instanceof Butterfly butterfly) {
            return butterfly.getVariant().getName();
        }
        return "null";
    }

    public static int getSnailColor(int color, Entity victim) {
        if (victim instanceof Snail snail) {
            return snail.getColor().getFireworkColor();
        }
        return color;
    }
}
