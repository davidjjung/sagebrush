package com.davigj.sage_brush.core.registry;

import com.davigj.sage_brush.core.SageBrush;
import com.teamabnormals.blueprint.core.util.registry.SoundSubRegistryHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = SageBrush.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SBSounds {
    public static final SoundSubRegistryHelper HELPER = SageBrush.REGISTRY_HELPER.getSoundSubHelper();

    public static final RegistryObject<SoundEvent> BRUSH_GENERIC = HELPER.createSoundEvent("item.brush.brushing.generic");
}
