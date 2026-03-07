package com.davigj.sage_brush.core;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class SBConfig {
    public static class Common {
        public final ModConfigSpec.ConfigValue<Boolean> regen;
        public final ModConfigSpec.ConfigValue<Boolean> pollenBrush;
        public final ModConfigSpec.ConfigValue<Boolean> shearables;
        public final ModConfigSpec.ConfigValue<Boolean> hurtSound;
        public final ModConfigSpec.ConfigValue<Boolean> aggroReal;
        public final ModConfigSpec.ConfigValue<Double> aggroChance;
        public final ModConfigSpec.ConfigValue<Boolean> brushSnagMockDamage;
        public final ModConfigSpec.ConfigValue<Boolean> pandaSneeze;
        public final ModConfigSpec.ConfigValue<Boolean> weakAndSick;
        public final ModConfigSpec.ConfigValue<Boolean> lazyAndPlayful;
        public final ModConfigSpec.ConfigValue<Boolean> removable;
        public final ModConfigSpec.ConfigValue<Boolean> mineable;
        public final ModConfigSpec.ConfigValue<Boolean> variantPrint;
        public final ModConfigSpec.ConfigValue<Boolean> mLVariantPrint;
        public final ModConfigSpec.ConfigValue<Integer> scuteBabyDrops;
        public final ModConfigSpec.ConfigValue<Integer> torScuteBabyDrops;

        Common (ModConfigSpec.Builder builder) {
            builder.push("common");
            builder.push("entity_interactions");
            regen = builder.comment("Pets periodically get Regeneration when brushed").define("Pet regen", false);
            pollenBrush = builder.comment("Nectar, technically pollen, can be brushed off of bees").define("Pollen brush", true);
            shearables = builder.comment("Shearable mobs, such as sheep and yaks, are accidentally shorn on aggro").define("Shearables shorn", true);
            builder.push("brush_snagging");
            hurtSound = builder.comment("Snagged brushes trigger hurt sound").define("Hurt sound", true);
            aggroReal = builder.comment("Aggroed mobs target the player").define("Aggro real", true);
            brushSnagMockDamage = builder.comment("Brushes deal fake (zero) damage upon aggro-ing").define("Aggro mock damage", false);
            aggroChance = builder.comment("Basic chance for mobs to aggress").define("Base aggro chance", 0.0);
            builder.pop();
            builder.push("pandas");
            pandaSneeze = builder.comment("Pandas sneeze when aggroed").define("Pandas sneeze", true);
            weakAndSick = builder.comment("Only weak or baby pandas sneeze when brushed").define("Weak or babies sneeze", true);
            lazyAndPlayful = builder.comment("Lazy and playful pandas are not provoked by being forced to sneeze").define("Lazy and playful pandas stay docile", true);
            builder.pop();
            builder.push("scute_drops");
            scuteBabyDrops = builder.comment("Number of scutes dropped by baby turtles when grown, to compensate for brushing adults").define("Baby turtle scute drops", 3);
            torScuteBabyDrops = builder.comment("Number of scutes dropped by baby tortoises when grown, to compensate for brushing adults").define("Baby tortoise scute drops", 3);
            builder.pop();
            builder.pop();
            builder.push("block_interactions");
            removable = builder.comment("Brushes destroy blocks in the removable tag").define("Removable", true);
            mineable = builder.comment("Brushes drop blocks in the mineable tag").define("Mineable", true);
            builder.pop();
            builder.push("debug");
            variantPrint = builder.comment("Logger prints the variant for a given entity of class VariantHolder when brushed").define("Print variants", false);
            mLVariantPrint = builder.comment("Logger prints the Mixed Litter variant for an entity with Mixed Litter variants when brushed").define("Print Mixed Litter variants", false);
            builder.pop();
            builder.pop();
        }
    }

    public static class Client {
        public final ModConfigSpec.ConfigValue<Boolean> reducedParticles;
        public final ModConfigSpec.ConfigValue<Boolean> gleamingBlocks;
        public final ModConfigSpec.ConfigValue<Boolean> purePolish;
        public final ModConfigSpec.ConfigValue<Boolean> petHearts;
        public final ModConfigSpec.ConfigValue<Boolean> gleam;
        public final ModConfigSpec.ConfigValue<Boolean> specializedParticles;
        public final ModConfigSpec.ConfigValue<Boolean> dustyMobs;

        public Client(ModConfigSpec.Builder builder) {
            builder.push("client");
            builder.push("entity_interactions");
            petHearts = builder.comment("Pets periodically emit hearts when brushed").define("Pet hearts", true);
            gleam = builder.comment("Mobs gleam when they can be brushed for resources").define("Gleaming mobs", true);
            dustyMobs = builder.comment("Mobs emit dust particles when brushed in the absence of a more specific particle").define("Dusty mobs", true);
            builder.pop();
            builder.push("block_interactions");
            specializedParticles = builder.comment("Dust particle behaviors for brushes are altered at all").define("Specialized particles", true);
            reducedParticles = builder.comment("Certain blocks emit fewer particles when brushed").define("Reduced dust", true);
            gleamingBlocks = builder.comment("Certain blocks emit gleam particles when brushed").define("Gleaming blocks", true);
            purePolish = builder.comment("Gleaming blocks do not emit dust particles when brushed").define("Pure polish", true);
            builder.pop();
            builder.pop();
        }
    }

    public static final ModConfigSpec COMMON_SPEC;
    public static final SBConfig.Common COMMON;

    public static final ModConfigSpec CLIENT_SPEC;
    public static final Client CLIENT;

    static {
        final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(SBConfig.Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();

        Pair<Client, ModConfigSpec> clientSpecPair = new ModConfigSpec.Builder().configure(Client::new);
        CLIENT_SPEC = clientSpecPair.getRight();
        CLIENT = clientSpecPair.getLeft();
    }
}
