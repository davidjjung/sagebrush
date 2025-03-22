package com.davigj.sage_brush.core.other;

import com.davigj.sage_brush.core.registry.SBParticleTypes;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.teamabnormals.atmospheric.core.registry.AtmosphericBlocks;
import net.mehvahdjukaar.supplementaries.reg.ModRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.fml.ModList;

import static com.teamabnormals.atmospheric.common.block.AloeVeraTallBlock.AGE;

public class SBConstants {
    public static final Item emuFeather;
    public static final Item roadrunnerFeather;
    public static final ParticleOptions sunbirdParticle;

    public static boolean isEmu(Entity entity) {
        return entity instanceof EntityEmu;
    }
    public static boolean isRoadrunner(Entity entity) {
        return entity instanceof EntityRoadrunner;
    }

    public static boolean isFeatherBlock(BlockState state) {
        return state.is(ModRegistry.FEATHER_BLOCK.get());
    }

    public static boolean isYellowBlossom(BlockState state) {
        return (state.is(AtmosphericBlocks.TALL_ALOE_VERA.get()) && state.getValue(AGE) > 5 && state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER)
                || state.is(AtmosphericBlocks.FLOWERING_MORADO_LEAVES.get());
    }

    static {
        emuFeather = ModList.get().isLoaded("alexsmobs") ? AMItemRegistry.EMU_FEATHER.get() : Items.FEATHER;
        roadrunnerFeather = ModList.get().isLoaded("alexsmobs") ? AMItemRegistry.ROADRUNNER_FEATHER.get() : Items.FEATHER;
        sunbirdParticle = ModList.get().isLoaded("alexsmobs") ? AMParticleRegistry.SUNBIRD_FEATHER.get() : SBParticleTypes.FEATHER.get();
    }
}
