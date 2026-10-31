package com.viyella.crimsonascension;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class CrimsonConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue BLOOD_MOON_CHANCE_PERCENT = BUILDER
            .comment("Chance for a blood moon to start each night.")
            .defineInRange("bloodMoonChancePercent", 25, 0, 100);

    public static final ModConfigSpec.IntValue BLOOD_MOON_MAX_PUPPETS = BUILDER
            .comment("Maximum blood puppets spawned per blood moon.")
            .defineInRange("bloodMoonMaxPuppets", 3, 0, 32);

    public static final ModConfigSpec.IntValue PRIMARY_TECHNIQUE_COST = BUILDER
            .comment("Blood qi cost of a path's primary technique.")
            .defineInRange("primaryTechniqueCost", 30, 0, 10_000);

    public static final ModConfigSpec.IntValue SECONDARY_TECHNIQUE_COST = BUILDER
            .comment("Blood qi cost of a path's secondary technique.")
            .defineInRange("secondaryTechniqueCost", 60, 0, 10_000);

    public static final ModConfigSpec.IntValue PRIMARY_COOLDOWN_TICKS = BUILDER
            .comment("Primary technique cooldown in ticks.")
            .defineInRange("primaryCooldownTicks", 160, 0, 72_000);

    public static final ModConfigSpec.IntValue SECONDARY_COOLDOWN_TICKS = BUILDER
            .comment("Secondary technique cooldown in ticks.")
            .defineInRange("secondaryCooldownTicks", 360, 0, 72_000);

    public static final ModConfigSpec.IntValue BONE_RUNE_BLOOD_COST = BUILDER
            .comment("Blood qi consumed when activating a bone rune formation.")
            .defineInRange("boneRuneBloodCost", 50, 0, 10_000);

    public static final ModConfigSpec.IntValue BLOOD_FIEND_HEALTH = BUILDER
            .comment("Maximum health of the Blood Fiend boss.")
            .defineInRange("bloodFiendHealth", 120, 1, 10_000);

    public static final ModConfigSpec.IntValue REALM_HEALTH_BONUS = BUILDER
            .comment("Maximum health added per cultivation realm.")
            .defineInRange("realmHealthBonus", 4, 0, 100);

    public static final ModConfigSpec.IntValue SUMMONED_PUPPET_LIFETIME_TICKS = BUILDER
            .comment("Lifetime of a Blood Puppet summoned by a technique.")
            .defineInRange("summonedPuppetLifetimeTicks", 600, 20, 72_000);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private CrimsonConfig() {
    }
}
