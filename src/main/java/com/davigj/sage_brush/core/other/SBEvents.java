package com.davigj.sage_brush.core.other;

import com.davigj.sage_brush.core.SBConfig;
import com.davigj.sage_brush.core.SageBrush;
import com.davigj.sage_brush.core.registry.SBParticleTypes;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedDataManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import static com.davigj.sage_brush.core.other.SBDataMapUtil.BRUSH_RESOURCES;

@EventBusSubscriber(modid = SageBrush.MOD_ID)
public class SBEvents {
    @SubscribeEvent
    public static void brushPets(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity().getItemInHand(event.getHand()).is(Items.BRUSH) && event.getTarget() instanceof TamableAnimal tamable && tamable.isOwnedBy(event.getEntity())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.CONSUME);
            event.getEntity().startUsingItem(event.getHand());
        }
    }

    @SubscribeEvent
    public static void spawnNotAlong(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        Holder<EntityType<?>> holder = entity.getType().builtInRegistryHolder();
        SBDataMapUtil.BrushData data = holder.getData(BRUSH_RESOURCES);

        if (data != null && entity instanceof LivingEntity living && !data.item().equals("null") && data.seconds() > 0) {
            TrackedDataManager.INSTANCE.setValue(entity, SageBrush.RESOURCE_TIMER, living.getRandom().nextInt(data.seconds()));
        }
    }

    @SubscribeEvent
    public static void entityTick(EntityTickEvent.Post event) {
        TrackedDataManager manager = TrackedDataManager.INSTANCE;
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        int timer = manager.getValue(target, SageBrush.RESOURCE_TIMER);
        if (timer > 0) {
            manager.setValue(target, SageBrush.RESOURCE_TIMER, timer - 1);
        }
        Holder<EntityType<?>> holder = target.getType().builtInRegistryHolder();
        SBDataMapUtil.BrushData data = holder.getData(BRUSH_RESOURCES);
        if (data != null && !data.item().equals("null") && !BrushUtil.isBaby(target, data.babyHarvest())) {
            if (target.level().isClientSide && SBConfig.CLIENT.gleam.get()) {
                Minecraft minecraft = Minecraft.getInstance();
                Player player = minecraft.player;
                if (player != null && (player.getMainHandItem().is(Items.BRUSH) || player.getOffhandItem().is(Items.BRUSH))) {
                    RandomSource random = target.level().getRandom();
                    if (target.tickCount % 25 == 0 && player.level() instanceof ClientLevel && manager.getValue(target, SageBrush.RESOURCE_TIMER) == 0 &&
                            (!data.shearable() || target instanceof IShearable shearable &&
                                    (shearable.isShearable(player, player.getItemInHand(InteractionHand.MAIN_HAND), target.level(), target.blockPosition())
                                    || (shearable.isShearable(player, player.getItemInHand(InteractionHand.OFF_HAND), target.level(), target.blockPosition()))))) {
                        target.level().addParticle(SBParticleTypes.GLEAM.get(), target.getX() + random.nextDouble() - (target.getBbWidth() * 0.65),
                                target.getEyeY() + (random.nextDouble() * 0.3) - 0.35, target.getZ() + random.nextDouble() - (target.getBbWidth() * 0.65), 0, 0, 0);
                    }
                }
            }
        }
    }
}