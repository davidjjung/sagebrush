package com.davigj.sage_brush.core.mixin;

import com.teamabnormals.environmental.common.entity.animal.yak.Yak;
import com.teamabnormals.environmental.common.entity.animal.yak.Yaktelligence;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Invoker;

@Pseudo
@Mixin(Yaktelligence.class)
public interface IMixinYaktelligence {
    @Invoker
    static void callRetaliate(Yak yak, LivingEntity target) {
        throw new AssertionError();
    }
}
