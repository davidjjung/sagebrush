package com.davigj.sage_brush.core.mixin;

import com.davigj.sage_brush.core.SBConfig;
import com.davigj.sage_brush.core.other.SBBrushUtil;
import com.davigj.sage_brush.core.other.tags.SBBlockTags;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(BrushItem.class)
public abstract class BrushItemMixin extends Item {
    public BrushItemMixin(Properties p_41383_) {
        super(p_41383_);
    }

    @Shadow
    public abstract HitResult calculateHitResult(LivingEntity user);

    @Final
    @Shadow
    private static double MAX_BRUSH_DISTANCE;

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack p_41398_, @NotNull Player player, @NotNull LivingEntity p_41400_, InteractionHand hand) {
        if (this.calculateHitResult(player).getType() == HitResult.Type.ENTITY) {
            player.startUsingItem(hand);
        }
        return InteractionResult.CONSUME;
    }

    @Inject(method = "onUseTick", at = @At("HEAD"), cancellable = true)
    private void brushEntity(Level level, LivingEntity living, ItemStack stack, int duration, CallbackInfo ci) {
        BrushItem brush = (BrushItem) (Object) this;
        if (living instanceof Player player) {
            HitResult result = this.calculateHitResult(player);
            if (result instanceof EntityHitResult ehr && result.getType() == HitResult.Type.ENTITY) {
                int $$9 = brush.getUseDuration(stack) - duration + 1;
                boolean $$10 = $$9 % 10 == 5;
                if ($$10) {
                    level.playSound(player, player.blockPosition(), SoundEvents.BRUSH_GENERIC, SoundSource.PLAYERS);
                    Entity $$11 = ehr.getEntity();
                    HumanoidArm arm = player.getUsedItemHand() == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
                    Vec3 vec3 = living.getViewVector(0.0F).scale(MAX_BRUSH_DISTANCE);
                    SBBrushUtil.onEntityUseTick(level, stack, $$11, living, vec3, arm);
                }
                ci.cancel();
            }
        }
    }


    @WrapOperation(method = "onUseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BrushItem;spawnDustParticles(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/entity/HumanoidArm;)V"))
    private void blocks(BrushItem instance, Level level, BlockHitResult hitResult, BlockState state, Vec3 vec3, HumanoidArm arm,
                        Operation<Void> original, Level p_273467_, LivingEntity p_273619_, ItemStack p_273316_,
                        @Local BlockPos blockPos) {
        SBBrushUtil.onBlockBrushTick(level, hitResult, state, vec3, arm, blockPos, original, instance, p_273619_, p_273316_);
    }


    @Inject(method = "calculateHitResult", at = @At("HEAD"), cancellable = true)
    private void hitEmBoys(LivingEntity living, CallbackInfoReturnable<HitResult> cir) {
        Vec3 vec3 = living.getViewVector(0.0F).scale(MAX_BRUSH_DISTANCE);
        Level level = living.level();
        Vec3 vec31 = living.getEyePosition();
        Predicate<Entity> predicate = (entity) -> !entity.isSpectator() && entity.isPickable();
        cir.setReturnValue(SBBrushUtil.getBrushHitResult(vec31, living, predicate, vec3, level));
    }

    @Inject(method = "spawnDustParticles", at = @At("HEAD"), cancellable = true)
    private void sparkle(Level level, BlockHitResult hitResult, BlockState state, Vec3 p_278337_, HumanoidArm p_285071_, CallbackInfo ci) {
        if (SBConfig.CLIENT.gleamingParticles.get() && state.is(SBBlockTags.GLEAMING)) {
            SBBrushUtil.gleam(level, hitResult, state, level.getRandom().nextInt(1,3));
            if (SBConfig.CLIENT.purePolish.get()) {
                ci.cancel();
            }
        }
    }

    @WrapOperation(method = "spawnDustParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(II)I"))
    private int j(RandomSource instance, int p_216340_, int p_216341_, Operation<Integer> original, Level p_278327_, BlockHitResult p_278272_, BlockState p_278235_) {
        if (SBConfig.CLIENT.reducedParticles.get() && p_278235_.is(SBBlockTags.REDUCED_DUST)) {
            return instance.nextInt(2, 4);
        } else {
            return original.call(instance, p_216340_, p_216341_);
        }
    }
}
