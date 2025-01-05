package com.davigj.sage_brush.core.other;

import com.davigj.sage_brush.core.registry.SBParticleTypes;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.mehvahdjukaar.supplementaries.reg.ModRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;

public class SBConstants {
    public static final Item emuFeather;
    public static final Item roadrunnerFeather;
    public static final ParticleOptions sunbirdParticle;

    public static boolean isEmu(Entity entity) {
        return entity instanceof EntityEmu;
    }
    public static boolean isHummingbird(Entity entity) {
        return entity instanceof EntityHummingbird;
    }
    public static boolean isSunbird(Entity entity) {
        return entity instanceof EntitySunbird;
    }
    public static boolean isRoadrunner(Entity entity) {
        return entity instanceof EntityRoadrunner;
    }
    public static boolean isShoebill(Entity entity) {
        return entity instanceof EntityShoebill;
    }

    public static boolean isFeatherBlock(BlockState state) {
        return state.is(ModRegistry.FEATHER_BLOCK.get());
    }

    static {
        emuFeather = ModList.get().isLoaded("alexsmobs") ? AMItemRegistry.EMU_FEATHER.get() : Items.FEATHER;
        roadrunnerFeather = ModList.get().isLoaded("alexsmobs") ? AMItemRegistry.ROADRUNNER_FEATHER.get() : Items.FEATHER;
        sunbirdParticle = ModList.get().isLoaded("alexsmobs") ? AMParticleRegistry.SUNBIRD_FEATHER.get() : SBParticleTypes.FEATHER.get();
    }
}
