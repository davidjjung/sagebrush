package com.davigj.sage_brush.core.registry;

import com.davigj.sage_brush.client.particle.*;
import com.davigj.sage_brush.core.SageBrush;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = SageBrush.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class SBParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, SageBrush.MOD_ID);

    public static final RegistryObject<SimpleParticleType> GLEAM = PARTICLE_TYPES.register("gleam", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> FEATHER = PARTICLE_TYPES.register("feather", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> PARROT_FEATHER = PARTICLE_TYPES.register("parrot_feather", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BLACK_FEATHER = PARTICLE_TYPES.register("black_feather", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> HUMMINGBIRD_FEATHER = PARTICLE_TYPES.register("hummingbird_feather", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> ROADRUNNER_FEATHER = PARTICLE_TYPES.register("roadrunner_feather", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> EMU_FEATHER = PARTICLE_TYPES.register("emu_feather", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> SHOEBILL_FEATHER = PARTICLE_TYPES.register("shoebill_feather", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> CHERRY_BLOSSOM = PARTICLE_TYPES.register("cherry_blossom", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> SPORE_BLOSSOM = PARTICLE_TYPES.register("spore_blossom", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> YELLOW_BLOSSOM = PARTICLE_TYPES.register("yellow_blossom", () -> new SimpleParticleType(true));

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(SBParticleTypes.GLEAM.get(), GleamParticle.Provider::new);
        event.registerSpriteSet(SBParticleTypes.FEATHER.get(), FeatherParticle.Provider::new);
        event.registerSpriteSet(SBParticleTypes.PARROT_FEATHER.get(), FeatherParticle.Provider::new);
        event.registerSpriteSet(SBParticleTypes.BLACK_FEATHER.get(), FeatherParticle.Provider::new);
        event.registerSpriteSet(SBParticleTypes.HUMMINGBIRD_FEATHER.get(), FeatherParticle.Provider::new);
        event.registerSpriteSet(SBParticleTypes.ROADRUNNER_FEATHER.get(), FeatherParticle.Provider::new);
        event.registerSpriteSet(SBParticleTypes.EMU_FEATHER.get(), FeatherParticle.Provider::new);
        event.registerSpriteSet(SBParticleTypes.SHOEBILL_FEATHER.get(), FeatherParticle.Provider::new);
        event.registerSpriteSet(SBParticleTypes.CHERRY_BLOSSOM.get(), ShrunkBlossomParticle.Provider::new);
        event.registerSpriteSet(SBParticleTypes.SPORE_BLOSSOM.get(), BlossomParticle.Provider::new);
        event.registerSpriteSet(SBParticleTypes.YELLOW_BLOSSOM.get(), EnlargedBlossomParticle.Provider::new);
    }
}
