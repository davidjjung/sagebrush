package com.davigj.sage_brush.core.other;

import com.davigj.sage_brush.core.SBConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

import java.util.List;

public class SBDataMapUtil {

    public record BrushData(String item, int itemCount, int seconds, double aggroChance, String particle, boolean babyHarvest, boolean shearable) {
        public static final Codec<BrushData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.optionalFieldOf("item", "null").forGetter(BrushData::item),
                Codec.INT.optionalFieldOf("itemCount", 1).forGetter(BrushData::itemCount),
                Codec.INT.optionalFieldOf("seconds", 0).forGetter(BrushData::seconds),
                Codec.DOUBLE.optionalFieldOf("aggroChance", SBConfig.COMMON.aggroChance.get()).forGetter(BrushData::aggroChance),
                Codec.STRING.optionalFieldOf("particle", "null").forGetter(BrushData::particle),
                Codec.BOOL.optionalFieldOf("babyHarvest", false).forGetter(BrushData::babyHarvest),
                Codec.BOOL.optionalFieldOf("shearable", false).forGetter(BrushData::shearable)
        ).apply(instance, BrushData::new));
    }

    public record BlockBrushResultData(String particle, int minCount, int maxCount, String result) {
        public static final Codec<BlockBrushResultData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.optionalFieldOf("particle", "null").forGetter(BlockBrushResultData::particle),
                Codec.INT.optionalFieldOf("minCount", 2).forGetter(BlockBrushResultData::minCount),
                Codec.INT.optionalFieldOf("maxCount", 5).forGetter(BlockBrushResultData::maxCount),
                Codec.STRING.optionalFieldOf("result", "null").forGetter(BlockBrushResultData::result)
        ).apply(instance, BlockBrushResultData::new));
    }

    public record MLVariantMapData(List<MLVariantData> mlVariants) {
        public static final Codec<MLVariantMapData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.list(MLVariantData.CODEC).fieldOf("variants").forGetter(MLVariantMapData::mlVariants)
        ).apply(instance, MLVariantMapData::new));

        public record MLVariantData(String texture, String particle) {
            public static final Codec<MLVariantData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("texture").forGetter(MLVariantData::texture),
                    Codec.STRING.optionalFieldOf("particle", "null").forGetter(MLVariantData::particle)
            ).apply(instance, MLVariantData::new));
        }
    }

    public static final DataMapType<EntityType<?>, BrushData> BRUSH_RESOURCES = DataMapType.builder(
            ResourceLocation.fromNamespaceAndPath("sage_brush", "brush_resources"), Registries.ENTITY_TYPE, BrushData.CODEC
    ).build();

    public static final DataMapType<EntityType<?>, MLVariantMapData> ML_VARIANTS = DataMapType.builder(
            ResourceLocation.fromNamespaceAndPath("sage_brush", "ml_variants"), Registries.ENTITY_TYPE, MLVariantMapData.CODEC
    ).build();

    public static final DataMapType<Block, BlockBrushResultData> BLOCK_BRUSH_RESULTS = DataMapType.builder(
            ResourceLocation.fromNamespaceAndPath("sage_brush", "block_brush_results"), Registries.BLOCK, BlockBrushResultData.CODEC
    ).build();
}
