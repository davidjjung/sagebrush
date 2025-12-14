package com.davigj.sage_brush.core.mixin;

import net.minecraft.world.entity.animal.armadillo.Armadillo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Armadillo.class)
public class ArmadilloMixin {
    @Inject(method = "brushOffScute", at = @At("HEAD"), cancellable = true)
    private void newWorldOrder(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
