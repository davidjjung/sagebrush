package com.davigj.sage_brush.core.other.tags;

import com.davigj.sage_brush.core.SageBrush;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class SBBlockTags {
    public static final TagKey<Block> GLEAMING = blockTag("gleaming");
    public static final TagKey<Block> REDUCED_DUST = blockTag("reduced_dust");
    public static final TagKey<Block> REMOVABLE = blockTag("removable");

    private static TagKey<Block> blockTag(String name) {
        return TagKey.create(Registries.BLOCK, new ResourceLocation(SageBrush.MOD_ID, name));
    }
}
