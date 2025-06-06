package com.davigj.sage_brush.common.item;

import com.davigj.sage_brush.client.BrushDustParticleOptions;
import com.davigj.sage_brush.core.SBConfig;
import com.davigj.sage_brush.core.other.SBBrushUtil;
import com.davigj.sage_brush.core.registry.SBSounds;
import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class DyeBrushItem extends BrushItem {
    private final DyeColor color;

    public DyeBrushItem(Properties p_272907_, DyeColor color) {
        super(p_272907_);
        this.color = color;
    }

    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int duration) {
        if (living instanceof Player player) {
            HitResult result = this.calculateHitResult(player);
            if (result instanceof EntityHitResult ehr && result.getType() == HitResult.Type.ENTITY) {
                int $$9 = this.getUseDuration(stack) - duration + 1;
                boolean $$10 = $$9 % 10 == 5;
                if ($$10) {
                    level.playSound(player, player.blockPosition(), SBSounds.BRUSH_GENERIC.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                    Entity $$11 = ehr.getEntity();
                    HumanoidArm arm = player.getUsedItemHand() == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
                    Vec3 vec3 = living.getViewVector(0.0F).scale(MAX_BRUSH_DISTANCE);
//                    SBBrushUtil.onEntityUseTick(level, stack, $$11, living, vec3, arm);
                    this.entityDustParticleFX(level, $$11, vec3, arm, this.getColor());
//                    player.getUseItem().use(level, )
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
                        this.onBlockBrushTick(level, blockhitresult, blockstate, living.getViewVector(0.0F), humanoidarm, blockpos, living, stack);
                        this.blockDustParticleFX(level, blockhitresult, living.getViewVector(0.0F), humanoidarm, this.getColor());
                        SoundEvent soundevent = SBSounds.BRUSH_GENERIC.get();
                        level.playSound(player, blockpos, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }

                    return;
                }
            }

            living.releaseUsingItem();
        } else {
            living.releaseUsingItem();
        }
    }

    private void entityDustParticleFX(Level level, Entity victim, Vec3 vec3, HumanoidArm arm, DyeColor dye) {
        int i = arm == HumanoidArm.RIGHT ? 1 : -1;
        int j = level.getRandom().nextInt(1, 4);
        Vec3 pos = victim.getEyePosition();
        vec3 = vec3.normalize();

        for (int k = 0; k < j; ++k) {
            double dx = vec3.z() * i * 0.07 * level.getRandom().nextDouble();
            double dz = -vec3.x() * i * 0.07 * level.getRandom().nextDouble();

            level.addParticle(new BrushDustParticleOptions(new Vector3f(Vec3.fromRGB24(dye.getFireworkColor())), 1.0F),
                    pos.x, victim.yo + (victim.getBbHeight() / 2), pos.z,
                    dx, 0.0D, dz);
        }
    }

    private void blockDustParticleFX(Level level, BlockHitResult hitResult, Vec3 vec3, HumanoidArm arm, DyeColor color) {
        int i = arm == HumanoidArm.RIGHT ? 1 : -1;
        int j = level.getRandom().nextInt(1, 4);
        Direction direction = hitResult.getDirection();
        Vec3 pos = hitResult.getLocation();
        vec3 = vec3.scale(0.1);

        for (int k = 0; k < j; ++k) {
            level.addParticle(new BrushDustParticleOptions(new Vector3f(Vec3.fromRGB24(color.getFireworkColor())), 1.0F),
                    pos.x - (double) (direction == Direction.WEST ? 1.0E-6F : 0.0F), pos.y,
                    pos.z - (double) (direction == Direction.NORTH ? 1.0E-6F : 0.0F),
                    vec3.z() * (double) i * 3.0 * level.getRandom().nextDouble(), 0.0D, -vec3.x() * (double) i * 3.0 * level.getRandom().nextDouble());
        }
    }

    public void onBlockBrushTick(Level level, BlockHitResult hitResult, BlockState state, Vec3 velocity,
                                 HumanoidArm arm, BlockPos blockPos, LivingEntity living, ItemStack stack) {
        System.out.println(state.getBlockHolder().get().getName());
    }

    public DyeColor getColor() {
        return this.color;
    }

    @Override
    public int getBarColor(@NotNull ItemStack p_150901_) {
        return this.getColor().getFireworkColor();
    }

    private static Supplier<Block> getConversionBlock(String fullId) {
        String[] parts = fullId.split(":");
        if (parts.length != 2) {
            return () -> Blocks.CRYING_OBSIDIAN;
        }
        String modid = parts[0];
        String blockID = parts[1];
        if (!ModList.get().isLoaded(modid) && modid != null) {
            return () -> Blocks.CRYING_OBSIDIAN;
        }
        assert modid != null;
        ResourceLocation block = new ResourceLocation(modid, blockID);
        if (ForgeRegistries.BLOCKS.getValue(block) == Blocks.AIR) {
            return () -> Blocks.CRYING_OBSIDIAN;
        }
        return (ModList.get().isLoaded(modid) ? () -> ForgeRegistries.BLOCKS.getValue(block) : () -> null);
    }
}
