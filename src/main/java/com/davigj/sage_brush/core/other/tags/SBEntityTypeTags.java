package com.davigj.sage_brush.core.other.tags;

import com.davigj.sage_brush.core.SageBrush;
import com.teamabnormals.blueprint.core.util.TagUtil;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class SBEntityTypeTags {
    public static final TagKey<EntityType<?>> SLIMY = entityTypeTag("slimy");

    private static TagKey<EntityType<?>> entityTypeTag(String name) {
        return TagUtil.entityTypeTag(SageBrush.MOD_ID, name);
    }
}
