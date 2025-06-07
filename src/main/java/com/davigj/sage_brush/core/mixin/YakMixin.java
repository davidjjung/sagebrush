package com.davigj.sage_brush.core.mixin;

import com.davigj.sage_brush.core.SBConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.teamabnormals.environmental.common.entity.animal.Yak;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@Mixin(Yak.class)
public abstract class YakMixin extends Animal {
    protected YakMixin(EntityType<? extends Animal> p_27557_, Level p_27558_) {
        super(p_27557_, p_27558_);
    }

    @ModifyExpressionValue(method = "onSheared", at = @At(value = "CONSTANT", args = "intValue=4"), remap = false)
    private int yakWrap(int i) {
        return SBConfig.COMMON.yakShearDropsBase.get();
    }

    @ModifyArg(method = "onSheared", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"), index = 0, remap = false)
    private int yakRandomWrap(int i) {
        return this.random.nextInt(SBConfig.COMMON.yakShearDropsExtra.get());
    }
}
