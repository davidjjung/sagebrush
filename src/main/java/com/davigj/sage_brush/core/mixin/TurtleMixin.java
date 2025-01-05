package com.davigj.sage_brush.core.mixin;

import com.davigj.sage_brush.core.SBConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Turtle.class)
public class TurtleMixin {
    @WrapOperation(method = "ageBoundaryReached", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Turtle;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;I)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private ItemEntity bonusSpawn(Turtle instance, ItemLike itemLike, int i, Operation<ItemEntity> original) {
        if (SBConfig.COMMON.scute.get()) {
            for (int j = 1; j < SBConfig.COMMON.scuteBabyDrops.get(); j++) {
                original.call(instance, itemLike, i);
            }
        }
        return original.call(instance, itemLike, i);
    }
}
