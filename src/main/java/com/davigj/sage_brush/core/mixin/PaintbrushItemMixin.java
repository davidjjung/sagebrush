package com.davigj.sage_brush.core.mixin;

import com.kekecreations.arts_and_crafts.common.entity.ACBedBlockEntity;
import com.kekecreations.arts_and_crafts.common.entity.DyedDecoratedPotBlockEntity;
import com.kekecreations.arts_and_crafts.common.item.PaintbrushItem;
import com.kekecreations.arts_and_crafts.common.util.PaintbrushUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Pseudo
@Mixin(PaintbrushItem.class)
public class PaintbrushItemMixin extends Item {
    private static final int ANIMATION_DURATION = 10;
    private static final int USE_DURATION = 200;
    private static final double MAX_BRUSH_DISTANCE = Math.sqrt(ServerGamePacketListenerImpl.MAX_INTERACTION_DISTANCE) - 1.0D;

    public PaintbrushItemMixin(Properties p_41383_) {
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
        if (living instanceof Player player) {
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
//                    SBBrushUtil.blockParticleFX(level, blockhitresult, player.getViewVector(0.0F), humanoidarm, new BrushDustParticleOptions(Vec3.fromRGB24(DyeColor.BROWN.getFireworkColor()).toVector3f(), 1.0F), 1, 3);
                    this.sagebrush$spawnDustParticles(level, blockhitresult, blockState, player.getViewVector(0.0F), humanoidarm);
                    level.playSound(player, pos, SoundEvents.BRUSH_GENERIC, SoundSource.BLOCKS);
                    if (!level.isClientSide()) {
                        Block finalBlock = PaintbrushUtils.getFinalBlock(level.registryAccess(), blockState, itemStack);
                        if (finalBlock != null && finalBlock != blockState.getBlock()) {
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
    }

    public HitResult calculateHitResult(LivingEntity p_281264_) {
        return ProjectileUtil.getHitResultOnViewVector(p_281264_, (p_281111_) -> {
            return !p_281111_.isSpectator() && p_281111_.isPickable();
        }, MAX_BRUSH_DISTANCE);
    }

    @Unique
    public void sagebrush$spawnDustParticles(Level level, BlockHitResult hitResult, BlockState state, Vec3 vec3, HumanoidArm arm) {
        int $$6 = arm == HumanoidArm.RIGHT ? 1 : -1;
        int $$7 = level.getRandom().nextInt(7, 12);
        BlockParticleOption $$8 = new BlockParticleOption(ParticleTypes.BLOCK, state);
        Direction $$9 = hitResult.getDirection();
        vec3 = vec3.scale(0.1);
        Vec3 $$11 = hitResult.getLocation();

        for(int $$12 = 0; $$12 < $$7; ++$$12) {
            level.addParticle($$8, $$11.x - (double)($$9 == Direction.WEST ? 1.0E-6F : 0.0F), $$11.y, $$11.z - (double)($$9 == Direction.NORTH ? 1.0E-6F : 0.0F), vec3.z() * (double)$$6 * 3.0 * level.getRandom().nextDouble(), 0.0, -vec3.x() * (double)$$6 * 3.0 * level.getRandom().nextDouble());
        }

    }
}
