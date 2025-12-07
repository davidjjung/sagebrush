package com.davigj.sage_brush.core.other.compat;

import com.davigj.sage_brush.core.SBConfig;
import com.davigj.sage_brush.core.mixin.IMixinYaktelligence;
import com.teamabnormals.environmental.common.entity.animal.yak.Yak;
import com.teamabnormals.environmental.core.registry.EnvironmentalItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.fml.ModList;

public class EnvironmentalCompat {
    public static final Item yakHair;
    public static final Item yakPants;

    public static boolean isYak(Entity entity) {
        return ModList.get().isLoaded("environmental") && entity instanceof Yak;
    }

    public static void handleYak(LivingEntity yak, LivingEntity perp) {
        if (EnvironmentalCompat.isYak(yak)) {
            yak.playSound(SoundEvents.SHEEP_SHEAR);
            ((Yak)yak).setSheared(true);
            if (SBConfig.COMMON.aggroReal.get() && perp.getItemBySlot(EquipmentSlot.LEGS).getItem() != EnvironmentalCompat.yakPants) {
                IMixinYaktelligence.callRetaliate((Yak) yak, perp);
            }
        }
    }

    static {
        yakHair = ModList.get().isLoaded("environmental") ? EnvironmentalItems.YAK_HAIR.get() : Items.STRING;
        yakPants = ModList.get().isLoaded("environmental") ? EnvironmentalItems.YAK_PANTS.get() : Items.LEATHER_LEGGINGS;
    }


}
