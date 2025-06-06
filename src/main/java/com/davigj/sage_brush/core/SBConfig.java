package com.davigj.sage_brush.core;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class SBConfig {
    public static class Common {
        public final ForgeConfigSpec.ConfigValue<Boolean> regen;
        public final ForgeConfigSpec.ConfigValue<Integer> stringTimer;
        public final ForgeConfigSpec.ConfigValue<Boolean> brushSnag;
        public final ForgeConfigSpec.ConfigValue<Boolean> brushSnagMockDamage;
        public final ForgeConfigSpec.ConfigValue<Boolean> pandaSneeze;
        public final ForgeConfigSpec.ConfigValue<Boolean> weakAndSick;
        public final ForgeConfigSpec.ConfigValue<Boolean> lazyAndPlayful;
        public final ForgeConfigSpec.ConfigValue<Double> pandaSnagChance;
        public final ForgeConfigSpec.ConfigValue<Double> featherSnagChance;
        public final ForgeConfigSpec.ConfigValue<Boolean> featheredMolt;
        public final ForgeConfigSpec.ConfigValue<Integer> moltTimer;
        public final ForgeConfigSpec.ConfigValue<Integer> worseMoltTimer;
        public final ForgeConfigSpec.ConfigValue<Boolean> removable;
        public final ForgeConfigSpec.ConfigValue<Boolean> scutesSpawnSet;
        public final ForgeConfigSpec.ConfigValue<Boolean> scute;
        public final ForgeConfigSpec.ConfigValue<Integer> scuteTimer;
        public final ForgeConfigSpec.ConfigValue<Integer> scuteBabyDrops;
        public final ForgeConfigSpec.ConfigValue<Boolean> torScute;
        public final ForgeConfigSpec.ConfigValue<Integer> torScuteTimer;
        public final ForgeConfigSpec.ConfigValue<Integer> torScuteBabyDrops;
        public final ForgeConfigSpec.ConfigValue<Boolean> yakHair;
        public final ForgeConfigSpec.ConfigValue<Double> yakShearChance;
        public final ForgeConfigSpec.ConfigValue<Boolean> yakBrushGentle;
        public final ForgeConfigSpec.ConfigValue<Integer> yakBrushHairCount;
        public final ForgeConfigSpec.ConfigValue<Integer> yakShearDropsBase;
        public final ForgeConfigSpec.ConfigValue<Integer> yakShearDropsExtra;

        Common (ForgeConfigSpec.Builder builder) {
            builder.push("common");
            builder.push("entity_interactions");
            regen = builder.comment("Pets periodically get Regeneration when brushed").define("Pet regen", false);
            stringTimer = builder.comment("String brush cooldown, in ticks. For mobs like sheep and goats").define("Stringable timer", 6000);
            builder.push("pandas");
            pandaSneeze = builder.comment("Pandas periodically sneeze when brushed").define("Pandas sneeze", true);
            weakAndSick = builder.comment("Only weak or baby pandas sneeze when brushed").define("Weak or babies sneeze", true);
            lazyAndPlayful = builder.comment("Lazy and playful pandas are not provoked by being forced to sneeze").define("Lazy and playful pandas stay docile", true);
            builder.pop();
            builder.push("feather_drops");
            featheredMolt = builder.comment("Mobs in the feathered tag periodically drop feather items when brushed").define("Feathered molt", true);
            moltTimer = builder.comment("Feather molt cooldown, in ticks. Defaults to ~4 feathers a day").define("Molt timer", 6000);
            worseMoltTimer = builder.comment("Feather molt cooldown for worse_feathered mobs, in ticks. Defaults to ~2 feathers a day").define("Molt timer", 12000);
            builder.pop();
            builder.push("scute_drops");
            scutesSpawnSet = builder.comment("Scute shedders spawn with scute timers that are still ticking down").define("Scute timer spawn set", true);
            scute = builder.comment("Adult turtles periodically drop scutes when brushed").define("Turtles shed scutes", true);
            scuteTimer = builder.comment("Scute shed cooldown, in ticks. Defaults to ~1 scute every two days.").define("Scute timer", 48000);
            scuteBabyDrops = builder.comment("Number of scutes dropped by baby turtles when grown, to compensate for brushing adults").define("Baby turtle scute drops", 3);
            torScute = builder.comment("Adult tortoises from Sully's Mod periodically drop tortoise scutes when brushed").define("Tortoises shed scutes", true);
            torScuteTimer = builder.comment("Tortoise scute shed cooldown, in ticks. Defaults to ~1 scute every two days.").define("Tortoise scute timer", 48000);
            torScuteBabyDrops = builder.comment("Number of scutes dropped by baby tortoises when grown, to compensate for brushing adults").define("Baby tortoise scute drops", 3);
            builder.pop();
            builder.push("yak_hair_drops");
            yakHair = builder.comment("Can yaks be brushed for hair instead").define("Yaks are brushed for hair", true);
            yakShearChance = builder.comment("Chance of yak becoming sheared when successfully brushed").define("Yak loses fur coat from brushing chance", 0.2);
            yakBrushGentle = builder.comment("Whether yaks remain unprovoked by brushing").define("Brushing yaks always gentle", false);
            yakBrushHairCount = builder.comment("How many hairs drop each time when brushed").define("Yak brush hair count", 1);
            yakShearDropsBase = builder.comment("Minimum dropped hair when yaks are sheared, not brushed").define("Yak shear drops base", 4);
            yakShearDropsExtra = builder.comment("Extra hair that drops by random chance, 0 to this number").define("Yak shear drops additional", 12);
            builder.pop();
            builder.push("brush_snags");
            brushSnag = builder.comment("Brushes occasionally provoke mobs that have no resources to shed, causing them to attack or panic").define("Brushes snag", false);
            brushSnagMockDamage = builder.comment("Brushes deal fake (zero) damage upon snagging").define("Brush snag deals mock damage", false);
            pandaSnagChance = builder.comment("Panda snag chance").define("Panda snag chance", 0.05);
            featherSnagChance = builder.comment("Feathered mobs snag chance").define("Feathered snag chance", 0.15);
            builder.pop();
            builder.pop();
            builder.push("block_interactions");
            removable = builder.comment("Brushes destroy blocks in the removable tag").define("Removable", true);
            builder.pop();
            builder.pop();
        }
    }

    public static class Client {
        public final ForgeConfigSpec.ConfigValue<Boolean> reducedParticles;
        public final ForgeConfigSpec.ConfigValue<Boolean> gleamingParticles;
        public final ForgeConfigSpec.ConfigValue<Boolean> purePolish;
        public final ForgeConfigSpec.ConfigValue<Boolean> petHearts;
        public final ForgeConfigSpec.ConfigValue<Boolean> molt;
        public final ForgeConfigSpec.ConfigValue<Boolean> allFeathersNoDust;
        public final ForgeConfigSpec.ConfigValue<Boolean> gleam;
        public final ForgeConfigSpec.ConfigValue<Boolean> specializedParticles;

        public Client(ForgeConfigSpec.Builder builder) {
            builder.push("client");
            builder.push("entity_interactions");
            petHearts = builder.comment("Pets periodically emit hearts when brushed").define("Pet hearts", true);
            molt = builder.comment("Feathered mobs periodically molt feathers when brushed").define("Molting", true);
            allFeathersNoDust = builder.comment("Feathered mobs don't emit poof particles when brushed").define("Just feathers", true);
            gleam = builder.comment("Overbrushed animals gleam. A more domestic alternative to brush snags").define("Gleaming mobs", true);
            builder.pop();
            builder.push("block_interactions");
            specializedParticles = builder.comment("Dust particle behaviors for brushes are altered at all").define("Specialized particles", true);
            reducedParticles = builder.comment("Certain blocks emit fewer particles when brushed").define("Reduced dust", true);
            gleamingParticles = builder.comment("Certain blocks emit gleam particles when brushed").define("Gleaming blocks", true);
            purePolish = builder.comment("Gleaming blocks do not emit dust particles when brushed").define("Pure polish", false);
            builder.pop();
            builder.pop();
        }
    }

    static final ForgeConfigSpec COMMON_SPEC;
    public static final SBConfig.Common COMMON;

    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final Client CLIENT;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(SBConfig.Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();

        Pair<Client, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(Client::new);
        CLIENT_SPEC = clientSpecPair.getRight();
        CLIENT = clientSpecPair.getLeft();
    }
}
