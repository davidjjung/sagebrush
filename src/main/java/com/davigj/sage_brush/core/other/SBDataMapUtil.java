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

    public record BlockBrushResultData(String particle, int minCount, int maxCount, double speed, String result) {
        public static final Codec<BlockBrushResultData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.optionalFieldOf("particle", "null").forGetter(BlockBrushResultData::particle),
                Codec.INT.optionalFieldOf("minCount", 2).forGetter(BlockBrushResultData::minCount),
                Codec.INT.optionalFieldOf("maxCount", 5).forGetter(BlockBrushResultData::maxCount),
                Codec.DOUBLE.optionalFieldOf("speed", 1.0D).forGetter(BlockBrushResultData::speed),
                Codec.STRING.optionalFieldOf("result", "null").forGetter(BlockBrushResultData::result)
        ).apply(instance, BlockBrushResultData::new));
    }

    public record MLVariantMapData(List<MLVariantData> mlVariants, String babyTexture, String babyTextureParticle) {
        public static final Codec<MLVariantMapData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.list(MLVariantData.CODEC).fieldOf("variants").forGetter(MLVariantMapData::mlVariants),
                Codec.STRING.optionalFieldOf("babyTexture", "null").forGetter(MLVariantMapData::babyTexture),
                Codec.STRING.optionalFieldOf("babyTextureParticle", "null").forGetter(MLVariantMapData::babyTextureParticle)
        ).apply(instance, MLVariantMapData::new));

        public record MLVariantData(String texture, String particle) {
            public static final Codec<MLVariantData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("texture").forGetter(MLVariantData::texture),
                    Codec.STRING.optionalFieldOf("particle", "null").forGetter(MLVariantData::particle)
            ).apply(instance, MLVariantData::new));
        }
    }

    public record NaturalistVariantMapData(List<NaturalistVariantData> variants) {
        public static final Codec<NaturalistVariantMapData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.list(NaturalistVariantData.CODEC).fieldOf("variants").forGetter(NaturalistVariantMapData::variants)
        ).apply(instance, NaturalistVariantMapData::new));

        public record NaturalistVariantData(String texture, String particle) {
            public static final Codec<NaturalistVariantData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("texture").forGetter(NaturalistVariantData::texture),
                    Codec.STRING.optionalFieldOf("particle", "null").forGetter(NaturalistVariantData::particle)
            ).apply(instance, NaturalistVariantData::new));
        }
    }

    public record VariantHolderMapData(List<VariantData> variants) {
        public static final Codec<VariantHolderMapData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.list(VariantData.CODEC).fieldOf("variants").forGetter(VariantHolderMapData::variants)
        ).apply(instance, VariantHolderMapData::new));

        public record VariantData(String variant, String particle) {
            public static final Codec<VariantData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("variant").forGetter(VariantData::variant),
                    Codec.STRING.optionalFieldOf("particle", "null").forGetter(VariantData::particle)
            ).apply(instance, VariantData::new));
        }
    }

    public record VariantResourceData(List<VariantItemData> variants) {
        public static final Codec<VariantResourceData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.list(VariantItemData.CODEC).fieldOf("variants").forGetter(VariantResourceData::variants)
        ).apply(instance, VariantResourceData::new));

        public record VariantItemData(String variant, String item) {
            public static final Codec<VariantItemData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("variant").forGetter(VariantItemData::variant),
                    Codec.STRING.optionalFieldOf("item", "null").forGetter(VariantItemData::item)
            ).apply(instance, VariantItemData::new));
        }
    }


    public static final DataMapType<EntityType<?>, BrushData> BRUSH_RESOURCES = DataMapType.builder(
            ResourceLocation.fromNamespaceAndPath("sage_brush", "brush_resources"), Registries.ENTITY_TYPE, BrushData.CODEC
    ).build();

    public static final DataMapType<EntityType<?>, MLVariantMapData> ML_VARIANTS = DataMapType.builder(
            ResourceLocation.fromNamespaceAndPath("sage_brush", "ml_particle_variants"), Registries.ENTITY_TYPE, MLVariantMapData.CODEC
    ).build();

    public static final DataMapType<EntityType<?>, NaturalistVariantMapData> NATURALIST_VARIANTS = DataMapType.builder(
            ResourceLocation.fromNamespaceAndPath("sage_brush", "naturalist_particle_variants"), Registries.ENTITY_TYPE, NaturalistVariantMapData.CODEC
    ).build();

    public static final DataMapType<EntityType<?>, VariantHolderMapData> VANILLA_VARIANTS = DataMapType.builder(
            ResourceLocation.fromNamespaceAndPath("sage_brush", "vanilla_particle_variants"), Registries.ENTITY_TYPE, VariantHolderMapData.CODEC
    ).build();

    public static final DataMapType<EntityType<?>, VariantResourceData> VARIANT_BRUSH_RESOURCES = DataMapType.builder(
            ResourceLocation.fromNamespaceAndPath("sage_brush", "variant_brush_resources"), Registries.ENTITY_TYPE, VariantResourceData.CODEC
    ).build();

    public static final DataMapType<Block, BlockBrushResultData> BLOCK_BRUSH_RESULTS = DataMapType.builder(
            ResourceLocation.fromNamespaceAndPath("sage_brush", "block_brush_results"), Registries.BLOCK, BlockBrushResultData.CODEC
    ).build();
}
