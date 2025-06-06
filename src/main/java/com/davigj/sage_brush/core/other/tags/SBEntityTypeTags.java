package com.davigj.sage_brush.core.other.tags;

import com.davigj.sage_brush.core.SageBrush;
import com.teamabnormals.blueprint.core.util.TagUtil;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class SBEntityTypeTags {
    public static final TagKey<EntityType<?>> FEATHERED = entityTypeTag("feathered");
    public static final TagKey<EntityType<?>> WORSE_FEATHERED = entityTypeTag("worse_feathered");
    public static final TagKey<EntityType<?>> DOCILE = entityTypeTag("docile");
    public static final TagKey<EntityType<?>> COSMETIC_BLACK_FEATHERS = entityTypeTag("cosmetic_black_feathers");
    public static final TagKey<EntityType<?>> COSMETIC_HUMMINGBIRD_FEATHERS = entityTypeTag("cosmetic_hummingbird_feathers");
    public static final TagKey<EntityType<?>> COSMETIC_ROADRUNNER_FEATHERS = entityTypeTag("cosmetic_roadrunner_feathers");
    public static final TagKey<EntityType<?>> COSMETIC_SUNBIRD_FEATHERS = entityTypeTag("cosmetic_sunbird_feathers");
    public static final TagKey<EntityType<?>> COSMETIC_SHOEBILL_FEATHERS = entityTypeTag("cosmetic_shoebill_feathers");
    public static final TagKey<EntityType<?>> COSMETIC_EMU_FEATHERS = entityTypeTag("cosmetic_emu_feathers");
    public static final TagKey<EntityType<?>> COSMETIC_FEATHERED = entityTypeTag("cosmetic_feathered");
    public static final TagKey<EntityType<?>> STRINGABLES = entityTypeTag("stringables");
    public static final TagKey<EntityType<?>> SLIMY = entityTypeTag("slimy");

    private static TagKey<EntityType<?>> entityTypeTag(String name) {
        return TagUtil.entityTypeTag(SageBrush.MOD_ID, name);
    }
}
