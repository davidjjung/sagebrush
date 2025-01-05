package com.davigj.sage_brush.core.other;

import com.davigj.sage_brush.core.SageBrush;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedData;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedDataManager;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.davigj.sage_brush.core.other.tags.SBEntityTypeTags.FEATHERED;
import static com.davigj.sage_brush.core.other.tags.SBEntityTypeTags.WORSE_FEATHERED;

@Mod.EventBusSubscriber(modid = SageBrush.MOD_ID)
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
    public static void entityTick(LivingEvent.LivingTickEvent event) {
        TrackedDataManager manager = TrackedDataManager.INSTANCE;
        LivingEntity target = event.getEntity();

        if (target.getType().is(FEATHERED)) {
            countDown(manager, target, SageBrush.FEATHER_TIMER);
        } else if (target.getType().is(WORSE_FEATHERED)) {
            countDown(manager, target, SageBrush.WORSE_FEATHER_TIMER);
        }
        if (target instanceof Turtle) {
            countDown(manager, target, SageBrush.SCUTE_TIMER);
        }
    }

    private static void countDown(TrackedDataManager manager, LivingEntity entity, TrackedData<Integer> timerData) {
        int timer = manager.getValue(entity, timerData);
        if (timer > 0) {
            manager.setValue(entity, timerData, timer - 1);
        }
    }


}