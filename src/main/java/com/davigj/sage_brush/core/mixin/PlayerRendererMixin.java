package com.davigj.sage_brush.core.mixin;

import com.davigj.sage_brush.common.item.BrushItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.UseAnim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {
    @Unique
    private static final HumanoidModel.ArmPose sagebrush$BRUSH =
            HumanoidModel.ArmPose.create("BRUSH", false, (model, entity, arm) -> {
                model.leftArm.xRot = model.leftArm.xRot * 0.5F - ((float) Math.PI / 5F);
                model.leftArm.yRot = 0.0F;
            });


    @Inject(method = "getArmPose", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseAnimation()Lnet/minecraft/world/item/UseAnim;", shift = At.Shift.AFTER), cancellable = true)
    private static void armPose(AbstractClientPlayer player, InteractionHand hand, CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        UseAnim useanim = player.getItemInHand(hand).getUseAnimation();
        if (useanim == UseAnim.CUSTOM && player.getItemInHand(hand).getItem() instanceof BrushItem) {
            cir.setReturnValue(sagebrush$BRUSH);
        }
    }
}
