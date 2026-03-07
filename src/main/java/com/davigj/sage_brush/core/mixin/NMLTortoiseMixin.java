package com.davigj.sage_brush.core.mixin;

import com.davigj.sage_brush.core.SBConfig;
import com.farcr.nomansland.common.entity.tortoise.Tortoise;
import com.farcr.nomansland.common.registry.items.NMLItems;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(Tortoise.class)
public class NMLTortoiseMixin {
    @WrapOperation(method = "ageBoundaryReached", at = @At(value = "INVOKE", target = "Lcom/farcr/nomansland/common/entity/tortoise/Tortoise;spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private ItemEntity bonusSpawn(Tortoise instance, ItemStack stack, float v, Operation<ItemEntity> original) {
        instance.spawnAtLocation(new ItemStack((ItemLike) NMLItems.STURDY_SCUTE.get(), SBConfig.COMMON.torScuteBabyDrops.get() - 2));
        return original.call(instance, stack, v);
    }
}