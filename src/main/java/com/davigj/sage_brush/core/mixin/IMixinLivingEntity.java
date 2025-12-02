package com.davigj.sage_brush.core.mixin;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface IMixinLivingEntity {
    @Invoker
    SoundEvent callGetHurtSound(DamageSource source);
}
