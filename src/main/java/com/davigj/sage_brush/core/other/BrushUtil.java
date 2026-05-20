package com.davigj.sage_brush.core.other;

import com.davigj.sage_brush.client.BrushDustParticleOptions;
import com.davigj.sage_brush.client.TintedFeatherParticleOptions;
import com.davigj.sage_brush.core.SBConfig;
import com.davigj.sage_brush.core.SageBrush;
import com.davigj.sage_brush.core.mixin.BeeAccessor;
import com.davigj.sage_brush.core.mixin.IMixinLivingEntity;
import com.davigj.sage_brush.core.other.compat.EnvironmentalCompat;
import com.davigj.sage_brush.core.other.compat.MixedLitterCompat;
import com.davigj.sage_brush.core.other.compat.NaturalistCompat;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.enchantment.Enchantments;
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
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.davigj.sage_brush.core.SageBrush.LOGGER;
import static com.davigj.sage_brush.core.other.SBDataMapUtil.*;
import static com.davigj.sage_brush.core.other.compat.MixedLitterCompat.MIXED_LITTER;
import static com.davigj.sage_brush.core.other.compat.NaturalistCompat.NATURALIST;
import static com.davigj.sage_brush.core.other.tags.SBEntityTypeTags.SLIMY;
import static net.minecraft.world.entity.projectile.ProjectileUtil.getEntityHitResult;
import static net.minecraft.world.level.block.Block.dropResources;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.LAYERS;

public class BrushUtil {
    public static final TrackedDataManager manager = TrackedDataManager.INSTANCE;

    public static void onEntityUseTick(Level level, ItemStack stack, Entity victim, LivingEntity player, Vec3 velocity, HumanoidArm arm, HitResult result) {
        Holder<EntityType<?>> holder = victim.getType().builtInRegistryHolder();
        SBDataMapUtil.BrushData data = holder.getData(BRUSH_RESOURCES);
        boolean dusty = !victim.isInWaterOrBubble() && !victim.getType().is(SLIMY) && (level.isClientSide && SBConfig.CLIENT.dustyMobs.get());
        ParticleOptions particle = null;
        if (data != null) {
            if (!level.isClientSide()) {
                if (isBaby(victim, data.babyHarvest())) return;
                TrackedData<Integer> timer = SageBrush.RESOURCE_TIMER;
                int timerTicks = manager.getValue(victim, timer);
                boolean aggro = data.aggroChance() != 0.0 && victim.getRandom().nextDouble() < data.aggroChance();
                if (timerTicks == 0) {
                    manager.setValue(victim, timer, data.seconds() * 20);
                    boolean canShear = data.shearable() && player instanceof Player player1 && victim instanceof IShearable shearable
                            && shearable.isShearable(player1, stack, level, victim.blockPosition());
                    boolean passesShearFilter = !data.shearable() || canShear;

                    if (passesShearFilter && !data.item().equals("null")) {
                        ItemStack resource = new ItemStack(getCompatItem(data.item(), victim).get(), data.itemCount());
                        victim.spawnAtLocation(resource);
                        victim.playSound(SoundEvents.ITEM_PICKUP, 0.3F, (float) (0.5F + (victim.getRandom().nextGaussian() * 0.2F)));
                        damageItem(stack, player);
                        if (SBConfig.COMMON.shearables.get() && canShear && victim instanceof LivingEntity living && aggro) {
                            handleShearables(living, player, stack);
                        }
                    }
                } else {
                    if (victim instanceof LivingEntity living && aggro) {
                        if (victim instanceof Panda panda) {
                            handlePanda(panda, player, stack);
                        } else if (victim instanceof Bee bee && SBConfig.COMMON.pollenBrush.get()) {
                            handleBee(bee, player, stack);
                        } else {
                            if (!(victim instanceof IShearable)) {
                                snagBrush(living, player);
                            }
                        }
                    }
                }
            } else {
                if (!data.particle().equals("null")) {
                    particle = (ParticleOptions) getCompatParticle(data.particle()).get();
                }
            }
        }

        if (victim instanceof VariantHolder<?> variantHolder) {
            particle = getVariantParticle(holder.getData(VANILLA_VARIANTS), variantHolder, particle);
        }

        if (MIXED_LITTER) {
            particle = MixedLitterCompat.getParticle(holder.getData(ML_VARIANTS), victim, particle);
        }

        if (NATURALIST) {
            particle = NaturalistCompat.getParticle(holder.getData(NATURALIST_VARIANTS), victim, particle);
        }

        if (victim.isInWaterOrBubble()) {
            particle = ParticleTypes.BUBBLE_COLUMN_UP;
        }

        if (particle != null) {
            entityParticleFX(level, victim, velocity, arm, particle, 1, 3);
            dusty = false;
        }

        if (dusty) {
            entityDustParticleFX(level, victim, velocity, arm, 1, 4, result);
        }

        if (victim instanceof TamableAnimal tamable && tamable.isOwnedBy(player)) {
            if (level.isClientSide && SBConfig.CLIENT.petHearts.get()) {
                entityParticleFX(level, tamable, velocity, arm, ParticleTypes.HEART, 0, 2);
            } else if (tamable.getRandom().nextDouble() < 0.3 && SBConfig.COMMON.regen.get()) {
                tamable.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80));
                damageItem(stack, player);
            }
        }
    }

    public static boolean isBaby(Entity victim, boolean babyHarvest) {
        return (victim instanceof AgeableMob ageable && ageable.isBaby()) && !babyHarvest;
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

    private static void handleShearables(LivingEntity victim, LivingEntity perp, ItemStack stack) {
        victim.playSound(SoundEvents.BRUSH_SAND_COMPLETED);
        if (victim instanceof Sheep sheep) {
            handleSheep(sheep, perp);
        } else if (EnvironmentalCompat.isYak(victim)) {
            EnvironmentalCompat.handleYak(victim, perp);
        }
        damageItem(stack, perp);
    }

    private static void handleSheep(Sheep sheep, LivingEntity perp) {
        sheep.setSheared(true);
        snagBrush(sheep, perp);
    }

    private static void handleBee(Bee bee, LivingEntity perp, ItemStack stack) {
        if (bee.hasNectar()) {
            ((BeeAccessor) bee).callSetHasNectar(false);
            bee.playSound(SoundEvents.BRUSH_SAND_COMPLETED);
            snagBrush(bee, perp);
            damageItem(stack, perp);
            if (bee.level() instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.FALLING_NECTAR,
                        Mth.lerp(server.random.nextDouble(), bee.getX() - 0.3F, bee.getX() + 0.3F),
                        bee.getY(0.5),
                        Mth.lerp(server.random.nextDouble(), bee.getZ() - 0.3F, bee.getZ() + 0.3F),
                        bee.getRandom().nextInt(5) + 5,
                        0.15F, 0, 0.15F, 0.0F);
            }
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


    public static ParticleOptions getVariantParticle(SBDataMapUtil.VariantHolderMapData data, VariantHolder<?> victim, ParticleOptions particle) {
        String variantPath = victim.getVariant().toString();
        if (victim instanceof Wolf wolf) {
            variantPath = wolf.getVariant().getRegisteredName();
        }
        if (SBConfig.COMMON.variantPrint.get()) {
            LOGGER.debug("[This is a debug feature, enabled in the config.] Variant name: " + variantPath);
        }
        if (data != null) {
            for (SBDataMapUtil.VariantHolderMapData.VariantData variantData : data.variants()) {
                if (variantPath.equals(variantData.variant())) {
                    particle = (ParticleOptions) getCompatParticle(variantData.particle()).get();
                }
            }
        }
        return particle;
    }

    private static void entityParticleFX(Level level, Entity victim, Vec3 vec3, HumanoidArm arm, ParticleOptions particle, int minPar, int maxPar) {
        int i = arm == HumanoidArm.RIGHT ? 1 : -1;
        int j = level.getRandom().nextInt(minPar, maxPar);
        Vec3 pos = victim.getEyePosition();
        vec3 = vec3.scale(0.1);

        for (int k = 0; k < j; ++k) {
            level.addParticle(particle, pos.x, pos.y, pos.z,
                    vec3.z() * (double) i * 0.3 * level.getRandom().nextDouble(), 0.0,
                    -vec3.x() * (double) i * 0.3 * level.getRandom().nextDouble());
        }
    }

    private static void entityDustParticleFX(Level level, Entity victim, Vec3 vec3, HumanoidArm arm, int minPar, int maxPar, HitResult result) {
        int i = arm == HumanoidArm.RIGHT ? 1 : -1;
        int j = level.getRandom().nextInt(minPar, maxPar);
        Vec3 pos = victim.getEyePosition();

        int color = 0xFFFFFF;
        if (victim instanceof Mob mob && mob.getPickedResult(result) != null) {
            if (Objects.requireNonNull(mob.getPickedResult(result)).getItem() instanceof SpawnEggItem egg) {
                color = egg.getColor(0);
            }
        }
        if (victim instanceof Sheep sheep) {
            color = sheep.getColor().getFireworkColor();
        }
        if (NATURALIST) {
            color = NaturalistCompat.getSnailColor(color, victim);
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
        if (handleSnows(level, state, blockPos)) {
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
        if (SBConfig.COMMON.mineable.get() && state.is(SBBlockTags.MINEABLE)) {
            blockParticleFX(level, hitResult, velocity, arm, new BlockParticleOption(ParticleTypes.BLOCK, state), 18, 24);
            if (!level.isClientSide) {
                dropResources(state, level, blockPos);
                level.removeBlock(blockPos, false);
                damageItem(stack, living);
            }
            return;
        }
        Holder<Block> holder = state.getBlock().builtInRegistryHolder();
        SBDataMapUtil.BlockBrushResultData data = holder.getData(BLOCK_BRUSH_RESULTS);
        if (data != null && !data.particle().equals("null")) {
            velocity = velocity.scale(data.speed());
            blockParticleFX(level, hitResult, velocity, arm, getCompatParticle(data.particle()).get(), data.minCount(), data.maxCount());
            return;
        }
        original.call(instance, level, hitResult, state, velocity, arm);
    }

    private static boolean handleSnows(Level level, BlockState state, BlockPos blockPos) {
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

    private static Supplier<Item> getCompatItem(String fullId, Entity victim) {
        if (fullId.equals("sage_brush:variant_brush_resources")) {
            Holder<EntityType<?>> holder = victim.getType().builtInRegistryHolder();
            SBDataMapUtil.VariantResourceData variantResourceData = holder.getData(VARIANT_BRUSH_RESOURCES);
            if (variantResourceData != null) {
                for (SBDataMapUtil.VariantResourceData.VariantItemData variantItemData : variantResourceData.variants()) {
                    String variantPath = "null";
                    if (NATURALIST) variantPath = NaturalistCompat.getVariantPath(victim);
                    if (variantPath.equals(variantItemData.variant()) && !variantItemData.item().equals("null")) {
                        fullId = variantItemData.item();
                    }
                }
            }
        }
        String[] parts = fullId.split(":");
        String modid = parts[0];
        String itemID = parts[1];
        ResourceLocation item = ResourceLocation.fromNamespaceAndPath(modid, itemID);
        return ModList.get().isLoaded(modid) ? () -> BuiltInRegistries.ITEM.get(item) : () -> null;
    }

    public static Supplier<ParticleOptions> getCompatParticle(String fullId) {
        String[] parts = fullId.split(":");
        String modid = parts[0];
        String particleID = parts[1];
        ResourceLocation particleLoc = ResourceLocation.fromNamespaceAndPath(modid, particleID);
//        ParticleOptions particle = ModList.get().isLoaded(modid) ? (ParticleOptions) BuiltInRegistries.PARTICLE_TYPE.get(particleLoc) : null;
        if (parts[0].equals("sage_brush")) {
            if (parts[1].equals("tinted_feather")) {
                int color = !parts[2].isEmpty() ? Integer.parseInt(parts[2]) : 0xFFFFFF;
                return () -> new TintedFeatherParticleOptions(Vec3.fromRGB24(color).toVector3f(), 1.0F);
            } else if (parts[1].equals("dust")) {
                int color = !parts[2].isEmpty() ? Integer.parseInt(parts[2]) : 0xFFFFFF;
                return () -> new BrushDustParticleOptions(Vec3.fromRGB24(color).toVector3f(), 1.0F);
            }
        }
        return ModList.get().isLoaded(modid) ? () -> (ParticleOptions) BuiltInRegistries.PARTICLE_TYPE.get(particleLoc) : () -> null;
    }
}