package com.davigj.sage_brush.core.other.compat;

import com.crispytwig.naturalist.server.entity.variant.DataDrivenVariantAnimal;
import com.davigj.sage_brush.core.SBConfig;
import com.davigj.sage_brush.core.other.SBDataMapUtil;
import com.crispytwig.naturalist.server.entity.mob.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import static com.davigj.sage_brush.core.SageBrush.LOGGER;
import static com.davigj.sage_brush.core.other.BrushUtil.getCompatParticle;

public class NaturalistCompat {

    public static ParticleOptions getParticle(SBDataMapUtil.NaturalistVariantMapData naturalistData, Entity victim, ParticleOptions particle) {
        if (naturalistData == null) return particle;
        if (victim instanceof DataDrivenVariantAnimal variantHolder) {
            String variantPath = variantHolder.getVariantString();
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
            return butterfly.getVariantString();
        }
        return "null";
    }

    public static int getSnailColor(int color, Entity victim) {
        if (victim instanceof Snail snail) {
            return snail.getColor().getFireworkColor();
        }
        return color;
    }

    public static void handleBear(LivingEntity victim, LivingEntity perp) {
        if (victim instanceof Bear bear) {
            bear.level().playSound((Player)null, bear, SoundEvents.ITEM_PICKUP, perp != null ? SoundSource.PLAYERS : SoundSource.BLOCKS, 1.0F, 1.0F);
            bear.setSheared(true);
            if (SBConfig.COMMON.aggroReal.get() && perp != null) {
                bear.setPersistentAngerTarget(perp.getUUID());
            }
        }
    }
}
