package com.davigj.sage_brush.core.other;

import com.davigj.sage_brush.client.BrushDustParticleOptions;
import com.davigj.sage_brush.core.SBConfig;
import com.davigj.sage_brush.core.SageBrush;
import com.davigj.sage_brush.core.mixin.BeeAccessor;
import com.davigj.sage_brush.core.mixin.IMixinLivingEntity;
import com.davigj.sage_brush.core.other.tags.SBBlockTags;
import com.davigj.sage_brush.core.registry.SBParticleTypes;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedData;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedDataManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.IShearable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.davigj.sage_brush.core.other.SBDataMapUtil.BLOCK_BRUSH_RESULTS;
import static com.davigj.sage_brush.core.other.SBDataMapUtil.BRUSH_RESOURCES;
import static com.davigj.sage_brush.core.other.tags.SBEntityTypeTags.SLIMY;
import static net.minecraft.world.entity.projectile.ProjectileUtil.getEntityHitResult;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.LAYERS;

public class SBBrushUtil {
    public static final TrackedDataManager manager = TrackedDataManager.INSTANCE;

    public static void onEntityUseTick(Level level, ItemStack stack, Entity victim, LivingEntity player, Vec3 velocity, HumanoidArm arm, HitResult result) {
        Holder<EntityType<?>> holder = victim.getType().builtInRegistryHolder();
        SBDataMapUtil.BrushData data = holder.getData(BRUSH_RESOURCES);
        boolean dusty = !victim.isInWaterRainOrBubble() && !victim.getType().is(SLIMY) && (level.isClientSide && SBConfig.CLIENT.dustyMobs.get());
        if (data != null) {
            if ((victim instanceof AgeableMob ageable && ageable.isBaby()) && !data.babyHarvest()) return;
            TrackedData<Integer> timer = SageBrush.RESOURCE_TIMER;
            int timerTicks = manager.getValue(victim, timer);
            if (timerTicks == 0) {
                manager.setValue(victim, timer, data.seconds() * 20);
                boolean passesShearFilter = !data.shearable() || (player instanceof Player player1 && victim instanceof IShearable shearable
                        && shearable.isShearable(player1, stack, level, victim.blockPosition()));
                if (passesShearFilter && !data.item().equals("null")) {
                    ItemStack resource = new ItemStack(getCompatItem(data.item()).get(), data.itemCount());
                    victim.spawnAtLocation(resource);
                    victim.playSound(SoundEvents.ITEM_PICKUP, 0.3F, (float) (1.8F + (victim.getRandom().nextGaussian() * 0.2F)));
                    damageItem(stack, player);
                }
            } else {
                if (victim instanceof LivingEntity living && data.aggroChance() != 0.0 && victim.getRandom().nextDouble() < data.aggroChance()) {
                    if (victim instanceof Panda panda) {
                        handlePanda(panda, player, stack);
                    } else if (victim instanceof Sheep sheep && data.shearable()) {
                        handleSheep(sheep, player, stack);
                    } else if (SBConstants.isYak(victim) && data.shearable()) {
                        SBConstants.handleYak(living, player);
                    } else if (victim instanceof Bee bee) {
                        handleBee(bee, player, stack);
                    } else {
                        snagBrush(living, player);
                    }
                }
            }
            if (!data.particle().equals("null")) {
                if (level.isClientSide) {
                    entityParticleFX(level, victim, velocity, arm, (ParticleOptions) getCompatParticle(data.particle()).get(), 1, 3);
                    dusty = false;
                }
            }
        }

        if (victim instanceof TamableAnimal tamable && tamable.isOwnedBy(player)) {
            if (level.isClientSide && SBConfig.CLIENT.petHearts.get()) {
                entityParticleFX(level, tamable, velocity, arm, ParticleTypes.HEART, 0, 2);
            } else if (tamable.getRandom().nextDouble() < 0.3 && SBConfig.COMMON.regen.get()) {
                tamable.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80));
                damageItem(stack, player);
            }
        }

        if (dusty) {
            entityDustParticleFX(level, victim, velocity, arm, 1, 4, result);
        }
    }

    private static void handlePanda(Panda panda, LivingEntity perp, ItemStack stack) {
        boolean canSneeze = ((panda.isBaby() || panda.isWeak()) || !SBConfig.COMMON.weakAndSick.get());
        if (canSneeze && panda.canPerformAction() && !panda.isSneezing() && SBConfig.COMMON.pandaSneeze.get()) {
            panda.sneeze(true);
            damageItem(stack, perp);
            boolean canAggro = !(panda.isPlayful() || panda.isLazy());
            if (canAggro || !SBConfig.COMMON.lazyAndPlayful.get()) {
                snagBrush(panda, perp);
            }
        }
    }

    private static void handleSheep(Sheep sheep, LivingEntity perp, ItemStack stack) {
        sheep.setSheared(true);
        sheep.playSound(SoundEvents.SHEEP_SHEAR);
        snagBrush(sheep, perp);
        damageItem(stack, perp);
    }

    private static void handleBee(Bee bee, LivingEntity perp, ItemStack stack) {
        if (bee.hasNectar() && SBConfig.COMMON.pollenBrush.get()) {
            ((BeeAccessor) bee).callSetHasNectar(false);
            bee.playSound(SoundEvents.BRUSH_SAND_COMPLETED);
            snagBrush(bee, perp);
            damageItem(stack, perp);
        }
    }

    private static void snagBrush(LivingEntity victim, LivingEntity perp) {
        if (victim instanceof TamableAnimal tamable && tamable.isOwnedBy(perp)) return;
        victim.playSound(SoundEvents.PLAYER_HURT_SWEET_BERRY_BUSH, 0.3F, 1.8F);
        if (SBConfig.COMMON.brushSnagMockDamage.get()) {
            victim.hurt(victim.level().damageSources().generic(), 0.0F);
        }
        if (SBConfig.COMMON.hurtSound.get()) {
            victim.playSound(((IMixinLivingEntity) victim).callGetHurtSound(victim.damageSources().generic()));
        }
        if (SBConfig.COMMON.aggroReal.get()) {
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
                    vec3.z() * (double) i * 0.2 * level.getRandom().nextDouble(), 0.0,
                    -vec3.x() * (double) i * 0.2 * level.getRandom().nextDouble());
        }
    }

    private static void entityDustParticleFX(Level level, Entity victim, Vec3 vec3, HumanoidArm arm, int minPar, int maxPar, HitResult result) {
        int i = arm == HumanoidArm.RIGHT ? 1 : -1;
        int j = level.getRandom().nextInt(minPar, maxPar);
        Vec3 pos = victim.getEyePosition();

        int color = 0xFFFFFF;
        if (victim instanceof Mob mob && mob.getPickedResult(result) != null) {
            if (mob.getPickedResult(result).getItem() instanceof SpawnEggItem egg) {
                color = egg.getColor(0);
            }
        }

        vec3 = vec3.normalize();
        for (int k = 0; k < j; ++k) {
            double dx = vec3.z() * i * 0.07 * level.getRandom().nextDouble() * (victim.getBbWidth() * 0.8);
            double dz = -vec3.x() * i * 0.07 * level.getRandom().nextDouble() * (victim.getBbWidth() * 0.8);
            level.addParticle(new BrushDustParticleOptions(Vec3.fromRGB24(color).toVector3f(), 1.0F),
                    pos.x, victim.yo + (victim.getBbHeight() / 2), pos.z,
                    dx, 0.0D, dz);
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
        if (SBConfig.COMMON.removable.get() && state.is(SBBlockTags.REMOVABLE)) {
            blockParticleFX(level, hitResult, velocity, arm, new BlockParticleOption(ParticleTypes.BLOCK, state), 18, 24);
            if (!level.isClientSide) {
                level.removeBlock(blockPos, false);
                damageItem(stack, living);
            }
            return;
        }
        Holder<Block> holder = state.getBlock().builtInRegistryHolder();
        SBDataMapUtil.BlockBrushResultData data = holder.getData(BLOCK_BRUSH_RESULTS);
        if (data != null && !data.particle().equals("null")) {
            blockParticleFX(level, hitResult, velocity, arm, getCompatParticle(data.particle()).get(), data.minCount(), data.maxCount());
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

    public static void blockParticleFX(Level level, BlockHitResult hitResult, Vec3 vec3, HumanoidArm arm, ParticleOptions particle, int minPar, int maxPar) {
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
        stack.hurtAndBreak(1, entity, stack.equals(entity.getItemBySlot(EquipmentSlot.OFFHAND)) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND);
    }


    public static HitResult getBrushHitResult(Vec3 pos, Player player, Predicate<Entity> filter, Level level) {
        Vec3 blockVec = player.getViewVector(0.0F).scale(player.blockInteractionRange());
        Vec3 vec3 = pos.add(blockVec);
        HitResult hitresult = level.clip(new ClipContext(pos, vec3, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        if (hitresult.getType() != HitResult.Type.MISS) {
            vec3 = hitresult.getLocation();
        }
        HitResult hitresult1 = getEntityHitResult(level, player, pos, vec3,
                player.getBoundingBox().expandTowards(player.getViewVector(0.0F).scale(player.entityInteractionRange()))
                        .inflate(1.0), filter);
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

    private static Supplier<Item> getCompatItem(String fullId) {
        String[] parts = fullId.split(":");
        String modid = parts[0];
        String itemID = parts[1];
        ResourceLocation item = ResourceLocation.fromNamespaceAndPath(modid, itemID);
        return ModList.get().isLoaded(modid) ? () -> BuiltInRegistries.ITEM.get(item) : () -> null;
    }

    private static Supplier<ParticleOptions> getCompatParticle(String fullId) {
        String[] parts = fullId.split(":");
        String modid = parts[0];
        String particleID = parts[1];
        ResourceLocation particle = ResourceLocation.fromNamespaceAndPath(modid, particleID);
        return ModList.get().isLoaded(modid) ? () -> (ParticleOptions) BuiltInRegistries.PARTICLE_TYPE.get(particle) : () -> null;
    }
}