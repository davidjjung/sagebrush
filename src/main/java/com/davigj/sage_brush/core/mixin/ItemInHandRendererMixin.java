package com.davigj.sage_brush.core.mixin;

import com.davigj.sage_brush.common.item.BrushItem;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseAnimation()Lnet/minecraft/world/item/UseAnim;"))
    private void armPoseStuff(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack stack, float equippedProgress, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, CallbackInfo ci,
                              @Local HumanoidArm arm){
        if (stack.getUseAnimation() == UseAnim.CUSTOM && stack.getItem() instanceof BrushItem) {
            this.raspberryCore$applyBrushTransform(matrixStack, partialTicks, arm, stack, equippedProgress);
        }
    }

    @Unique
    private void raspberryCore$applyBrushTransform(PoseStack p_273513_, float p_273245_, HumanoidArm p_273726_, ItemStack p_272809_, float p_273333_) {
        this.applyItemArmTransform(p_273513_, p_273726_, p_273333_);
        assert this.minecraft.player != null;
        float f = (float)(this.minecraft.player.getUseItemRemainingTicks() % 10);
        float f1 = f - p_273245_ + 1.0F;
        float f2 = 1.0F - f1 / 10.0F;
        float f7 = -15.0F + 75.0F * Mth.cos(f2 * 2.0F * (float)Math.PI);
        if (p_273726_ != HumanoidArm.RIGHT) {
            p_273513_.translate(0.1D, 0.83D, 0.35D);
            p_273513_.mulPose(Vector3f.XP.rotationDegrees(-80.0F));
            p_273513_.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
            p_273513_.mulPose(Vector3f.XP.rotationDegrees(f7));
            p_273513_.translate(-0.3D, 0.22D, 0.35D);
        } else {
            p_273513_.translate(-0.25D, 0.22D, 0.35D);
            p_273513_.mulPose(Vector3f.XP.rotationDegrees(-80.0F));
            p_273513_.mulPose(Vector3f.YP.rotationDegrees(90.0F));
            p_273513_.mulPose(Vector3f.ZP.rotationDegrees(0.0F));
            p_273513_.mulPose(Vector3f.XP.rotationDegrees(f7));
        }
    }

    private void applyItemArmTransform(PoseStack p_109383_, HumanoidArm p_109384_, float p_109385_) {
        int i = p_109384_ == HumanoidArm.RIGHT ? 1 : -1;
        p_109383_.translate((float)i * 0.56F, -0.52F + p_109385_ * -0.6F, -0.72F);
    }

}
