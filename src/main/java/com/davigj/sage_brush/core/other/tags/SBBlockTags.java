package com.davigj.sage_brush.core.other.tags;

import com.davigj.sage_brush.core.SageBrush;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import static com.teamabnormals.blueprint.core.util.TagUtil.blockTag;

public class SBBlockTags {
    public static final TagKey<Block> GLEAMING = blockTag(SageBrush.MOD_ID, "gleaming");
    public static final TagKey<Block> REDUCED_DUST = blockTag(SageBrush.MOD_ID,"reduced_dust");
    public static final TagKey<Block> MINEABLE = blockTag(SageBrush.MOD_ID,"mineable");
    public static final TagKey<Block> REMOVABLE = blockTag(SageBrush.MOD_ID,"removable");
}
