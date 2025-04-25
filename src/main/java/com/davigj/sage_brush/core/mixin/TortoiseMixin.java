package com.davigj.sage_brush.core.mixin;

import com.davigj.sage_brush.core.SBConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.uraneptus.sullysmod.common.entities.Tortoise;
import com.uraneptus.sullysmod.core.registry.SMItems;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(Tortoise.class)
public class TortoiseMixin {
    @WrapOperation(method = "ageBoundaryReached", at = @At(value = "INVOKE", target = "Lcom/uraneptus/sullysmod/common/entities/Tortoise;spawnAtLocation(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private ItemEntity bonusSpawn(Tortoise instance, ItemStack stack, Operation<ItemEntity> original) {
        if (SBConfig.COMMON.torScute.get()) {
                instance.spawnAtLocation(new ItemStack((ItemLike)SMItems.TORTOISE_SCUTE.get(), SBConfig.COMMON.torScuteBabyDrops.get() - 2));
        }
        return original.call(instance, stack);
    }
}
