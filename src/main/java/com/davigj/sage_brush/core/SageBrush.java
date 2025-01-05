package com.davigj.sage_brush.core;

import com.davigj.sage_brush.core.registry.SBParticleTypes;
import com.teamabnormals.blueprint.common.world.storage.tracking.DataProcessors;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedData;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedDataManager;
import com.teamabnormals.blueprint.core.util.registry.RegistryHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SageBrush.MOD_ID)
public class SageBrush {
    // TODO: docile tags, gleaming entities, IW sand, horsies and donkeys, offended animals
    // TODO later: arts and crafts paintbrushes, domestication innovation pets, example mixin
    // TODO huge stretch goal: block particle jsons, entity particle jsons, entity resource jsons
    // TODO uncertain: brushes getting "gummed up" by things like cobwebs?...
    public static final String MOD_ID = "sage_brush";
    public static final RegistryHelper REGISTRY_HELPER = new RegistryHelper(MOD_ID);

    public static final TrackedData<Integer> FEATHER_TIMER = TrackedData.Builder.create(DataProcessors.INT, () -> 0).enableSaving().enablePersistence().build();
    public static final TrackedData<Integer> WORSE_FEATHER_TIMER = TrackedData.Builder.create(DataProcessors.INT, () -> 0).enableSaving().enablePersistence().build();
    public static final TrackedData<Integer> SCUTE_TIMER = TrackedData.Builder.create(DataProcessors.INT, () -> 0).enableSaving().enablePersistence().build();

    public SageBrush() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext context = ModLoadingContext.get();
        MinecraftForge.EVENT_BUS.register(this);

		REGISTRY_HELPER.register(bus);
        SBParticleTypes.PARTICLE_TYPES.register(bus);

        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener(this::dataSetup);
        context.registerConfig(ModConfig.Type.COMMON, SBConfig.COMMON_SPEC);
        context.registerConfig(ModConfig.Type.CLIENT, SBConfig.CLIENT_SPEC);

        TrackedDataManager.INSTANCE.registerData(new ResourceLocation(MOD_ID, "feather_timer"), FEATHER_TIMER);
        TrackedDataManager.INSTANCE.registerData(new ResourceLocation(MOD_ID, "worse_feather_timer"), WORSE_FEATHER_TIMER);
        TrackedDataManager.INSTANCE.registerData(new ResourceLocation(MOD_ID, "scute_timer"), SCUTE_TIMER);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {

        });
    }

    private void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {

        });
    }

    private void dataSetup(GatherDataEvent event) {

    }
}