package com.davigj.sage_brush.common.item;

import com.davigj.sage_brush.core.SBConfig;
import com.davigj.sage_brush.core.other.SBBrushUtil;
import com.davigj.sage_brush.core.other.tags.SBBlockTags;
import com.davigj.sage_brush.core.registry.SBSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

import static net.minecraft.world.entity.projectile.ProjectileUtil.getEntityHitResult;

public class BrushItem extends Item {
    public static final int ANIMATION_DURATION = 10;
    private static final int USE_DURATION = 200;
    public static final double MAX_BRUSH_DISTANCE = Math.sqrt(ServerGamePacketListenerImpl.MAX_INTERACTION_DISTANCE) - 1.0D;

    public BrushItem(Properties p_272907_) {
        super(p_272907_);
    }

    public InteractionResult useOn(UseOnContext p_272607_) {
        Player player = p_272607_.getPlayer();
        if (player != null && this.calculateHitResult(player).getType() == HitResult.Type.BLOCK) {
            player.startUsingItem(p_272607_.getHand());
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack p_41398_, @NotNull Player player, @NotNull LivingEntity p_41400_, InteractionHand hand) {
        if (this.calculateHitResult(player).getType() == HitResult.Type.ENTITY) {
            player.startUsingItem(hand);
        }
        return InteractionResult.CONSUME;
    }

    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack p_273490_) {
        return UseAnim.CUSTOM;
    }

    public int getUseDuration(ItemStack p_272765_) {
        return 200;
    }

    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int duration) {
        BrushItem brush = (BrushItem) (Object) this;
        if (living instanceof Player player) {
            HitResult result = this.calculateHitResult(player);
            if (result instanceof EntityHitResult ehr && result.getType() == HitResult.Type.ENTITY) {
                int $$9 = brush.getUseDuration(stack) - duration + 1;
                boolean $$10 = $$9 % 10 == 5;
                if ($$10) {
                    level.playSound(player, player.blockPosition(), SBSounds.BRUSH_GENERIC.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                    Entity $$11 = ehr.getEntity();
                    HumanoidArm arm = player.getUsedItemHand() == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
                    Vec3 vec3 = living.getViewVector(0.0F).scale(MAX_BRUSH_DISTANCE);
                    SBBrushUtil.onEntityUseTick(level, stack, $$11, living, vec3, arm, ehr);
                }
                return;
            }
        }
        if (duration >= 0 && living instanceof Player player) {
            HitResult hitresult = this.calculateHitResult(living);
            if (hitresult instanceof BlockHitResult blockhitresult) {
                if (hitresult.getType() == HitResult.Type.BLOCK) {
                    int i = this.getUseDuration(stack) - duration + 1;
                    boolean flag = i % 10 == 5;
                    if (flag) {
                        BlockPos blockpos = blockhitresult.getBlockPos();
                        BlockState blockstate = level.getBlockState(blockpos);
                        HumanoidArm humanoidarm = living.getUsedItemHand() == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
                        if (SBConfig.CLIENT.specializedParticles.get()) {
                            SBBrushUtil.onBlockBrushTick(level, blockhitresult, blockstate, living.getViewVector(0.0F), humanoidarm, blockpos, living, stack);
                        } else {
                            this.spawnDustParticles(level, blockhitresult, blockstate, living.getViewVector(0.0F), humanoidarm);
                        }
//                        Block $$18 = blockstate.getBlock();
                        SoundEvent soundevent = SBSounds.BRUSH_GENERIC.get();
                        level.playSound(player, blockpos, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
//                        if (!level.isClientSide()) {
//                            BlockEntity blockentity = level.getBlockEntity(blockpos);
//                            if (blockentity instanceof BrushableBlockEntity) {
//                                BrushableBlockEntity brushableblockentity = (BrushableBlockEntity)blockentity;
//                                boolean flag1 = brushableblockentity.brush(p_273467_.getGameTime(), player, blockhitresult.getDirection());
//                                if (flag1) {
//                                    EquipmentSlot equipmentslot = p_273316_.equals(player.getItemBySlot(EquipmentSlot.OFFHAND)) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
//                                    p_273316_.hurtAndBreak(1, p_273619_, (p_279044_) -> {
//                                        p_279044_.broadcastBreakEvent(equipmentslot);
//                                    });
//                                }
//                            }
//                        }
                    }

                    return;
                }
            }

            living.releaseUsingItem();
        } else {
            living.releaseUsingItem();
        }
    }

    public HitResult calculateHitResult(LivingEntity living) {
        Vec3 vec3 = living.getViewVector(0.0F).scale(MAX_BRUSH_DISTANCE);
        Level level = living.level;
        Vec3 vec31 = living.getEyePosition();
        Predicate<Entity> predicate = (entity) -> !entity.isSpectator() && entity.isPickable();
        return SBBrushUtil.getBrushHitResult(vec31, living, predicate, vec3, level);
    }

    public static HitResult getHitResultOnViewVector(Entity p_278281_, Predicate<Entity> p_278306_, double p_278293_) {
        Vec3 vec3 = p_278281_.getViewVector(0.0F).scale(p_278293_);
        Level level = p_278281_.level;
        Vec3 vec31 = p_278281_.getEyePosition();
        return getHitResult(vec31, p_278281_, p_278306_, vec3, level);
    }

    private static HitResult getHitResult(Vec3 p_278237_, Entity p_278320_, Predicate<Entity> p_278257_, Vec3 p_278342_, Level p_278321_) {
        Vec3 vec3 = p_278237_.add(p_278342_);
        HitResult hitresult = p_278321_.clip(new ClipContext(p_278237_, vec3, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, p_278320_));
        if (hitresult.getType() != HitResult.Type.MISS) {
            vec3 = hitresult.getLocation();
        }

        HitResult hitresult1 = getEntityHitResult(p_278321_, p_278320_, p_278237_, vec3, p_278320_.getBoundingBox().expandTowards(p_278342_).inflate(1.0D), p_278257_);
        if (hitresult1 != null) {
            hitresult = hitresult1;
        }

        return hitresult;
    }


    public void spawnDustParticles(Level level, BlockHitResult hitResult, BlockState state, Vec3 p_278337_, HumanoidArm p_285071_) {
        if (SBConfig.CLIENT.gleamingParticles.get() && state.is(SBBlockTags.GLEAMING)) {
            SBBrushUtil.gleam(level, hitResult, state, level.getRandom().nextInt(1,3));
            if (SBConfig.CLIENT.purePolish.get()) {
                return;
            }
        }
        int i = p_285071_ == HumanoidArm.RIGHT ? 1 : -1;
        int j = SBConfig.CLIENT.reducedParticles.get() && state.is(SBBlockTags.REDUCED_DUST) ? level.getRandom().nextInt(2, 4) : level.getRandom().nextInt(7, 12);
        BlockParticleOption blockparticleoption = new BlockParticleOption(ParticleTypes.BLOCK, state);
        Direction direction = hitResult.getDirection();
        DustParticlesDelta brushitem$dustparticlesdelta = DustParticlesDelta.fromDirection(p_278337_, direction);
        Vec3 vec3 = hitResult.getLocation();

        for(int k = 0; k < j; ++k) {
            level.addParticle(blockparticleoption, vec3.x - (double)(direction == Direction.WEST ? 1.0E-6F : 0.0F), vec3.y, vec3.z - (double)(direction == Direction.NORTH ? 1.0E-6F : 0.0F), brushitem$dustparticlesdelta.xd() * (double)i * 3.0D * level.getRandom().nextDouble(), 0.0D, brushitem$dustparticlesdelta.zd() * (double)i * 3.0D * level.getRandom().nextDouble());
        }

    }

    public static void spawnStaticDust(Level p_278327_, BlockHitResult p_278272_, BlockState p_278235_, Vec3 p_278337_, HumanoidArm p_285071_) {
        double d0 = 3.0D;
        int i = p_285071_ == HumanoidArm.RIGHT ? 1 : -1;
        int j = p_278327_.getRandom().nextInt(7, 12);
        BlockParticleOption blockparticleoption = new BlockParticleOption(ParticleTypes.BLOCK, p_278235_);
        Direction direction = p_278272_.getDirection();
        DustParticlesDelta brushitem$dustparticlesdelta = DustParticlesDelta.fromDirection(p_278337_, direction);
        Vec3 vec3 = p_278272_.getLocation();

        for(int k = 0; k < j; ++k) {
            p_278327_.addParticle(blockparticleoption, vec3.x - (double)(direction == Direction.WEST ? 1.0E-6F : 0.0F), vec3.y, vec3.z - (double)(direction == Direction.NORTH ? 1.0E-6F : 0.0F), brushitem$dustparticlesdelta.xd() * (double)i * 3.0D * p_278327_.getRandom().nextDouble(), 0.0D, brushitem$dustparticlesdelta.zd() * (double)i * 3.0D * p_278327_.getRandom().nextDouble());
        }

    }

    static record DustParticlesDelta(double xd, double yd, double zd) {
        private static final double ALONG_SIDE_DELTA = 1.0D;
        private static final double OUT_FROM_SIDE_DELTA = 0.1D;

        public static DustParticlesDelta fromDirection(Vec3 p_273421_, Direction p_272987_) {
            double d0 = 0.0D;
            DustParticlesDelta brushitem$dustparticlesdelta;
            switch (p_272987_) {
                case DOWN:
                case UP:
                    brushitem$dustparticlesdelta = new DustParticlesDelta(p_273421_.z(), 0.0D, -p_273421_.x());
                    break;
                case NORTH:
                    brushitem$dustparticlesdelta = new DustParticlesDelta(1.0D, 0.0D, -0.1D);
                    break;
                case SOUTH:
                    brushitem$dustparticlesdelta = new DustParticlesDelta(-1.0D, 0.0D, 0.1D);
                    break;
                case WEST:
                    brushitem$dustparticlesdelta = new DustParticlesDelta(-0.1D, 0.0D, -1.0D);
                    break;
                case EAST:
                    brushitem$dustparticlesdelta = new DustParticlesDelta(0.1D, 0.0D, 1.0D);
                    break;
                default:
                    throw new IncompatibleClassChangeError();
            }

            return brushitem$dustparticlesdelta;
        }
    }
}