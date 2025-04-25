package com.davigj.sage_brush.core.other;

import com.davigj.sage_brush.core.SBConfig;
import com.davigj.sage_brush.core.SageBrush;
import com.davigj.sage_brush.core.other.tags.SBBlockTags;
import com.davigj.sage_brush.core.other.tags.SBEntityTypeTags;
import com.davigj.sage_brush.core.registry.SBParticleTypes;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedData;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedDataManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.davigj.sage_brush.core.other.tags.SBEntityTypeTags.*;
import static net.minecraft.world.entity.projectile.ProjectileUtil.getEntityHitResult;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.LAYERS;

public class SBBrushUtil {
    public static final TrackedDataManager manager = TrackedDataManager.INSTANCE;

    public static void onEntityUseTick(Level level, ItemStack stack, Entity victim, LivingEntity player, Vec3 velocity, HumanoidArm arm) {
        if (victim instanceof TamableAnimal tamable && tamable.isOwnedBy(player)) {
            if (level.isClientSide && SBConfig.CLIENT.petHearts.get()) {
                entityParticleFX(level, tamable, velocity, arm, ParticleTypes.HEART, 0, 2);
            } else if (tamable.getRandom().nextDouble() < 0.2 && SBConfig.COMMON.regen.get()) {
                tamable.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60));
                damageItem(stack, player);
            }
//            tamable.lookAt(player, 30.0F, 30.0F);
        }
        if (victim instanceof Panda panda) {
            if (level.isClientSide) {
                entityParticleFX(level, panda, velocity, arm, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SNOW_BLOCK.defaultBlockState()), 2, 4);
            } else {
                double achoo = panda.getRandom().nextDouble();
                if (achoo > 0.85) {
                    if (((panda.isBaby() || panda.isWeak()) || !SBConfig.COMMON.weakAndSick.get()) &&
                            panda.canPerformAction() && !panda.isSneezing() && SBConfig.COMMON.pandaSneeze.get()) {
                        panda.sneeze(true);
                        damageItem(stack, player);
                    }
                    if ((!(panda.isPlayful() || panda.isLazy())) || !SBConfig.COMMON.lazyAndPlayful.get()) {
                        snagBrush(panda, player, 1 - achoo, SBConfig.COMMON.pandaSnagChance.get());
                    }
                }
            }
            return;
        }
        if (victim.getType().is(SBEntityTypeTags.FEATHERED) || victim.getType().is(SBEntityTypeTags.WORSE_FEATHERED) || victim.getType().is(COSMETIC_FEATHERED)) {
            if (level.isClientSide && SBConfig.CLIENT.molt.get()) {
                visualMoltFX(level, victim, velocity, arm);
            } else if (SBConfig.COMMON.featheredMolt.get()) {
                if (victim.getType().is(SBEntityTypeTags.FEATHERED)) {
                    pluck(stack, player, victim, SageBrush.FEATHER_TIMER, SBConfig.COMMON.moltTimer.get());
                } else if (victim.getType().is(SBEntityTypeTags.WORSE_FEATHERED)) {
                    pluck(stack, player, victim, SageBrush.WORSE_FEATHER_TIMER, SBConfig.COMMON.worseMoltTimer.get());
                }
                if (victim instanceof LivingEntity)
                    snagBrush((LivingEntity) victim, player, level.getRandom().nextDouble(), SBConfig.COMMON.featherSnagChance.get());
            }
        }
        if (victim instanceof Turtle turtle && SBConfig.COMMON.scute.get()) {
            if (level.isClientSide) {
                entityParticleFX(level, turtle, velocity, arm, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.GREEN_CONCRETE.defaultBlockState()), 2, 4);
            } else {
                int timer = manager.getValue(turtle, SageBrush.SCUTE_TIMER);
                if (timer == 0) {
                    turtle.spawnAtLocation(Items.SCUTE);
                    damageItem(stack, player);
                    manager.setValue(turtle, SageBrush.SCUTE_TIMER, SBConfig.COMMON.scuteTimer.get());
                }
            }
            return;
        }
        if (SBConfig.COMMON.torScute.get() && (ModList.get().isLoaded("sullysmod") && SBConstants.isTortoise(victim))) {
            if (level.isClientSide) {
                entityParticleFX(level, victim, velocity, arm, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.BROWN_CONCRETE.defaultBlockState()), 2, 4);
            } else {
                int timer = manager.getValue(victim, SageBrush.SCUTE_TIMER);
                if (timer == 0) {
                    victim.spawnAtLocation(SBConstants.tortoiseScute);
                    damageItem(stack, player);
                    manager.setValue(victim, SageBrush.SCUTE_TIMER, SBConfig.COMMON.torScuteTimer.get());
                }
            }
        }
    }

    private static void visualMoltFX(Level level, Entity victim, Vec3 velocity, HumanoidArm arm) {
        ParticleOptions particle = SBParticleTypes.FEATHER.get();
        if (victim instanceof Parrot) {
            particle = SBParticleTypes.PARROT_FEATHER.get();
        } else if (victim.getType().is(COSMETIC_BLACK_FEATHERS)) {
            particle = SBParticleTypes.BLACK_FEATHER.get();
        } else if (victim.getType().is(COSMETIC_HUMMINGBIRD_FEATHERS)) {
            particle = SBParticleTypes.HUMMINGBIRD_FEATHER.get();
        } else if (victim.getType().is(COSMETIC_ROADRUNNER_FEATHERS)) {
            particle = SBParticleTypes.ROADRUNNER_FEATHER.get();
        } else if (victim.getType().is(COSMETIC_EMU_FEATHERS)) {
            particle = SBParticleTypes.EMU_FEATHER.get();
        } else if (victim.getType().is(COSMETIC_SHOEBILL_FEATHERS)) {
            particle = SBParticleTypes.SHOEBILL_FEATHER.get();
        } else if (victim.getType().is(COSMETIC_SUNBIRD_FEATHERS)) {
            particle = SBConstants.sunbirdParticle;
        }
        entityParticleFX(level, victim, velocity, arm, particle, 1, 3);
    }

    private static void pluck(ItemStack stack, LivingEntity player, Entity pluckee, TrackedData<Integer> timerData, int reset) {
        int timer = manager.getValue(pluckee, timerData);
        if (timer == 0) {
            ItemLike item = Items.FEATHER;
            if (ModList.get().isLoaded("alexsmobs")) {
                if (SBConstants.isEmu(pluckee)) {
                    item = SBConstants.emuFeather;
                } else if (SBConstants.isRoadrunner(pluckee)) {
                    item = SBConstants.roadrunnerFeather;
                }
            }
            pluckee.spawnAtLocation(item);
            damageItem(stack, player);
            manager.setValue(pluckee, timerData, reset);
        }
    }

    private static void snagBrush(LivingEntity victim, LivingEntity perp, double snag, double snagConfig) {
        if (victim instanceof TamableAnimal tamable && tamable.isOwnedBy(perp)) return;
        if (SBConfig.COMMON.brushSnag.get() && snag < snagConfig) {
            victim.playSound(SoundEvents.PLAYER_HURT_SWEET_BERRY_BUSH, 0.3F, 1.8F);
            if (SBConfig.COMMON.brushSnagMockDamage.get()) {
                victim.hurt(victim.level().damageSources().generic(), 0.0F);
            }
            victim.setLastHurtByMob(perp);
        }
    }

    private static void entityParticleFX(Level level, Entity victim, Vec3 vec3, HumanoidArm arm, ParticleOptions particle, int minPar, int maxPar) {
        int i = arm == HumanoidArm.RIGHT ? 1 : -1;
        int j = level.getRandom().nextInt(minPar, maxPar);
        Vec3 pos = victim.getEyePosition();
        vec3 = vec3.scale(0.1);

        for (int k = 0; k < j; ++k) {
            level.addParticle(particle, pos.x, pos.y, pos.z,
                    vec3.z() * (double) i * 0.1 * level.getRandom().nextDouble(), 0.0,
                    -vec3.x() * (double) i * 0.1 * level.getRandom().nextDouble());
        }
    }


    public static void onBlockBrushTick(Level level, BlockHitResult hitResult, BlockState state, Vec3 velocity,
                                        HumanoidArm arm, BlockPos blockPos, Operation<Void> original, BrushItem instance,
                                        LivingEntity living, ItemStack stack) {
        if (letItShnope(level, state, blockPos)) {
            blockParticleFX(level, hitResult, velocity, arm, ParticleTypes.SNOWFLAKE, 10, 14);
            damageItem(stack, living);
            return;
        }
        if (state.is(Blocks.SPORE_BLOSSOM)) {
            blockParticleFX(level, hitResult, velocity, arm, SBParticleTypes.SPORE_BLOSSOM.get(), 2, 5);
            return;
        } else if (state.is(Blocks.END_ROD)) {
            blockParticleFX(level, hitResult, velocity, arm, ParticleTypes.END_ROD, 2, 5);
            return;
        } else if (state.is(Blocks.CHERRY_LEAVES)) {
            blockParticleFX(level, hitResult, velocity, arm, SBParticleTypes.CHERRY_BLOSSOM.get(), 2, 5);
            return;
        } else if (ModList.get().isLoaded("supplementaries") && SBConstants.isFeatherBlock(state)) {
            blockParticleFX(level, hitResult, velocity, arm, SBParticleTypes.FEATHER.get(), 2, 5);
            return;
        } else if (ModList.get().isLoaded("atmospheric") && SBConstants.isYellowBlossom(state)) {
            blockParticleFX(level, hitResult, velocity, arm, SBParticleTypes.YELLOW_BLOSSOM.get(), 2, 3);
            return;
        }
        if (SBConfig.COMMON.removable.get() && state.is(SBBlockTags.REMOVABLE)) {
            blockParticleFX(level, hitResult, velocity, arm, new BlockParticleOption(ParticleTypes.BLOCK, state), 18, 24);
            if (!level.isClientSide) {
                level.removeBlock(blockPos, false);
                damageItem(stack, living);
            }
            return;
        }
        original.call(instance, level, hitResult, state, velocity, arm);
    }

    private static boolean letItShnope(Level level, BlockState state, BlockPos blockPos) {
        if (state.is(Blocks.SNOW)) {
            if (!level.isClientSide) {
                int layers = state.getValue(LAYERS);
                if (layers > 1) {
                    level.setBlockAndUpdate(blockPos, state.setValue(LAYERS, layers - 1));
                } else {
                    level.removeBlock(blockPos, false);
                }
            }
            return true;
        }
        if (state.is(Blocks.SNOW_BLOCK)) {
            if (!level.isClientSide) {
                level.setBlockAndUpdate(blockPos, Blocks.SNOW.defaultBlockState().setValue(LAYERS, 7));
            }
            return true;
        }
        return false;
    }

    private static void blockParticleFX(Level level, BlockHitResult hitResult, Vec3 vec3, HumanoidArm arm, ParticleOptions particle, int minPar, int maxPar) {
        int i = arm == HumanoidArm.RIGHT ? 1 : -1;
        int j = level.getRandom().nextInt(minPar, maxPar);
        Direction direction = hitResult.getDirection();
        Vec3 pos = hitResult.getLocation();
        vec3 = vec3.scale(0.1);

        for (int k = 0; k < j; ++k) {
            level.addParticle(particle, pos.x - (double) (direction == Direction.WEST ? 1.0E-6F : 0.0F), pos.y,
                    pos.z - (double) (direction == Direction.NORTH ? 1.0E-6F : 0.0F),
                    vec3.z() * (double) i * 3.0 * level.getRandom().nextDouble(), 0.0,
                    -vec3.x() * (double) i * 3.0 * level.getRandom().nextDouble());
        }
    }

    private static void damageItem(ItemStack stack, LivingEntity entity) {
        stack.hurtAndBreak(1, entity, (e) -> {
            EquipmentSlot slot = stack.equals(entity.getItemBySlot(EquipmentSlot.OFFHAND)) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
            e.broadcastBreakEvent(slot);
        });
    }


    public static HitResult getBrushHitResult(Vec3 eye, Entity viewer, Predicate<Entity> predicate, Vec3 view, Level level) {
        Vec3 vec3 = eye.add(view);
        HitResult hitresult = level.clip(new ClipContext(eye, vec3, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, viewer));
        if (hitresult.getType() != HitResult.Type.MISS) {
            vec3 = hitresult.getLocation();
        }
        HitResult hitresult1 = getEntityHitResult(level, viewer, eye, vec3, viewer.getBoundingBox().expandTowards(view).inflate(1.0), predicate);
        if (hitresult1 != null) {
            hitresult = hitresult1;
        }
        return hitresult;
    }

    public static void gleam(Level level, BlockHitResult hitResult, BlockState state, int particleCount) {
        RandomSource random = level.getRandom();
        BlockPos blockpos = hitResult.getBlockPos();
        VoxelShape voxelShape = state.getCollisionShape(level, blockpos);
        AABB bounds = voxelShape.isEmpty() ? new AABB(0, 0, 0, 1, 1, 1) : voxelShape.bounds();
        bounds = bounds.move(blockpos.getX(), blockpos.getY(), blockpos.getZ()).inflate(0.2);
        double minDistance = 0.15;
        List<Vec3> spawnedParticles = new ArrayList<>();
        for (int i = 0; i < particleCount; i++) {
            Vec3 newParticlePos = null;
            int attempts = 10;
            do {
                double x = bounds.minX + random.nextDouble() * (bounds.maxX - bounds.minX);
                double y = bounds.minY + random.nextDouble() * (bounds.maxY - bounds.minY);
                double z = bounds.minZ + random.nextDouble() * (bounds.maxZ - bounds.minZ);
                Vec3 candidate = new Vec3(x, y, z);
                boolean isValid = spawnedParticles.stream().noneMatch(pos -> pos.distanceTo(candidate) < minDistance);
                if (isValid) {
                    newParticlePos = candidate;
                    break;
                }
            } while (--attempts > 0);
            if (newParticlePos != null) {
                spawnedParticles.add(newParticlePos);
                level.addParticle(SBParticleTypes.GLEAM.get(), newParticlePos.x, newParticlePos.y, newParticlePos.z, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}