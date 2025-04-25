package com.davigj.sage_brush.core.mixin;

import com.kekecreations.arts_and_crafts.common.entity.ACBedBlockEntity;
import com.kekecreations.arts_and_crafts.common.entity.DyedDecoratedPotBlockEntity;
import com.kekecreations.arts_and_crafts.common.item.PaintbrushItem;
import com.kekecreations.arts_and_crafts.common.util.PaintbrushUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Pseudo
@Mixin(PaintbrushItem.class)
public class PaintbrushMixin extends Item {
    private static final int ANIMATION_DURATION = 10;
    private static final int USE_DURATION = 200;
    private static final double MAX_BRUSH_DISTANCE = Math.sqrt(ServerGamePacketListenerImpl.MAX_INTERACTION_DISTANCE) - 1.0D;

    public PaintbrushMixin(Properties p_41383_) {
        super(p_41383_);
    }

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void noUseOnlyUseTick(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Player player = context.getPlayer();
        if (player != null && this.calculateHitResult(player).getType() == HitResult.Type.BLOCK) {
            player.startUsingItem(context.getHand());
        }

        cir.setReturnValue(InteractionResult.CONSUME);
    }

    public UseAnim getUseAnimation(ItemStack p_273490_) {
        return UseAnim.BRUSH;
    }

    public int getUseDuration(ItemStack p_272765_) {
        return USE_DURATION;
    }

    public void onUseTick(Level level, LivingEntity living, ItemStack itemStack, int p_41431_) {
        if (!level.isClientSide() && living instanceof Player player) {
            HitResult hitresult = this.calculateHitResult(player);
            if (hitresult instanceof BlockHitResult blockhitresult && hitresult.getType() == HitResult.Type.BLOCK) {
                int j = this.getUseDuration(itemStack) - p_41431_ + 1;
                boolean flag = j % ANIMATION_DURATION == 5;
                if (flag) {
                    BlockPos pos = blockhitresult.getBlockPos();
                    BlockState blockState = level.getBlockState(pos);
                    InteractionHand hand = player.getUsedItemHand();
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    HumanoidArm humanoidarm = player.getUsedItemHand() == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
                    this.spawnDustParticles(level, blockhitresult, blockState, player.getViewVector(0.0F), humanoidarm);
                    Block finalBlock = PaintbrushUtils.getFinalBlock(level.registryAccess(), blockState, itemStack);
                    if (finalBlock != null && finalBlock != blockState.getBlock()) {
                        if (!finalBlock.isEnabled(finalBlock.requiredFeatures())) {
                        }

                        DecoratedPotBlockEntity.Decorations oldDecorations;
                        if (blockEntity instanceof DyedDecoratedPotBlockEntity) {
                            DyedDecoratedPotBlockEntity dyedDecoratedPotBlockEntity = (DyedDecoratedPotBlockEntity) blockEntity;
                            oldDecorations = dyedDecoratedPotBlockEntity.getDecorations();
                            PaintbrushUtils.paintBlock(level, finalBlock.defaultBlockState(), pos, player, itemStack, hand);
                            PaintbrushUtils.setPotDecorations(level, pos, oldDecorations);
                        }

                        if (blockEntity instanceof DecoratedPotBlockEntity) {
                            DecoratedPotBlockEntity decoratedPotBlockEntity = (DecoratedPotBlockEntity) blockEntity;
                            oldDecorations = decoratedPotBlockEntity.getDecorations();
                            PaintbrushUtils.paintBlock(level, finalBlock.defaultBlockState(), pos, player, itemStack, hand);
                            PaintbrushUtils.setPotDecorations(level, pos, oldDecorations);
                        }

                        if (!(blockEntity instanceof BedBlockEntity) && !(blockEntity instanceof ACBedBlockEntity)) {
                            if (blockEntity instanceof BaseContainerBlockEntity) {
                                BaseContainerBlockEntity container = (BaseContainerBlockEntity) blockEntity;
                                List<ItemStack> itemList = new ArrayList();

                                for (int i = 0; i < container.getContainerSize(); ++i) {
                                    itemList.add(container.getItem(i));
                                    container.setItem(i, ItemStack.EMPTY);
                                }

                                PaintbrushUtils.paintBlock(level, finalBlock.defaultBlockState(), pos, player, itemStack, hand);
                                BlockEntity newBlockEntity = level.getBlockEntity(pos);
                                if (newBlockEntity instanceof BaseContainerBlockEntity) {
                                    BaseContainerBlockEntity newContainer = (BaseContainerBlockEntity) newBlockEntity;

                                    for (int i = 0; i < newContainer.getContainerSize(); ++i) {
                                        newContainer.setItem(i, (ItemStack) itemList.get(i));
                                    }
                                }

                            }
                        } else {
                            PaintbrushUtils.paintBed(level, finalBlock.defaultBlockState(), pos, player, itemStack, hand);
                        }

                        PaintbrushUtils.paintBlock(level, finalBlock.defaultBlockState(), pos, player, itemStack, hand);
                    }
                }
            }
        }
    }

    public HitResult calculateHitResult(LivingEntity p_281264_) {
        return ProjectileUtil.getHitResultOnViewVector(p_281264_, (p_281111_) -> {
            return !p_281111_.isSpectator() && p_281111_.isPickable();
        }, MAX_BRUSH_DISTANCE);
    }
}
