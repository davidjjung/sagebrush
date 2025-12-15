package com.davigj.sage_brush.core.mixin;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Bee.class)
public interface BeeAccessor {
    @Invoker
    void callSetHasNectar(boolean hasNectar);
}
