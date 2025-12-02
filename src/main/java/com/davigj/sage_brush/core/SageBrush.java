package com.davigj.sage_brush.core;

import com.davigj.sage_brush.core.registry.SBParticleTypes;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedData;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedDataManager;
import com.teamabnormals.blueprint.core.util.registry.RegistryHelper;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

import static com.davigj.sage_brush.core.other.SBDataMapUtil.BLOCK_BRUSH_RESULTS;
import static com.davigj.sage_brush.core.other.SBDataMapUtil.BRUSH_RESOURCES;

@Mod(SageBrush.MOD_ID)
public class SageBrush {
    public static final String MOD_ID = "sage_brush";
    public static final RegistryHelper REGISTRY_HELPER = new RegistryHelper(MOD_ID);

    public static final TrackedData<Integer> RESOURCE_TIMER = TrackedData.Builder.create(ByteBufCodecs.INT, () -> 0).enablePersistence().build();

    public SageBrush(IEventBus bus, ModContainer container) {
        SBParticleTypes.PARTICLE_TYPES.register(bus);

        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener(this::dataSetup);
        bus.addListener(this::registerDataMapTypes);

        container.registerConfig(ModConfig.Type.COMMON, SBConfig.COMMON_SPEC);
        container.registerConfig(ModConfig.Type.CLIENT, SBConfig.CLIENT_SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        TrackedDataManager.INSTANCE.registerData(SageBrush.location("resource_timer"), RESOURCE_TIMER);
    }

    private void clientSetup(FMLClientSetupEvent event) {
    }

    private void dataSetup(GatherDataEvent event) {
    }

    private void registerDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(BRUSH_RESOURCES);
        event.register(BLOCK_BRUSH_RESULTS);
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}