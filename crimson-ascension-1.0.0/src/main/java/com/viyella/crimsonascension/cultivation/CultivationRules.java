package com.viyella.crimsonascension.cultivation;

/**
 * Pure numeric rules for cultivation data. Keeping these limits independent of
 * Minecraft types makes them straightforward to validate without launching the game.
 */
public final class CultivationRules {
    public static final int MAX_REFINED_BLOOD = 100_000;
    public static final int MAX_REALM = 6;
    public static final int MAX_CORRUPTION = 1_000;
    public static final int BLOOD_VIAL_CAPACITY = 100;
    public static final int BLOOD_PER_REFINED = 10;
    public static final int CORRUPTION_PER_REFINED = 1;
    public static final int REFINED_BLOOD_COST_PER_REALM = 5;
    public static final int BLOOD_RECOVERY_START_REALM = 2;
    public static final int BLOOD_HEALING_START_REALM = 4;
    public static final int BLOOD_RECOVERY_INTERVAL_TICKS = 40;

    private CultivationRules() {
    }

    public static int clamp(int value, int maximum) {
        return Math.max(0, Math.min(maximum, value));
    }

    public static int addClamped(int current, int delta, int maximum) {
        long result = (long) current + delta;
        return (int) Math.max(0L, Math.min(maximum, result));
    }

    public static int breakthroughRefinedBloodCost(int realm) {
        return REFINED_BLOOD_COST_PER_REALM * (Math.max(0, realm) + 1);
    }

    public static int maxBloodQiForRealm(int realm) {
        return 100 * (Math.max(0, Math.min(MAX_REALM, realm)) + 1);
    }

    public static int passiveBloodRecoveryForRealm(int realm) {
        return realm < BLOOD_RECOVERY_START_REALM
                ? 0
                : Math.max(0, Math.min(MAX_REALM, realm)) - 1;
    }

    public static int passiveBloodHealingCostForRealm(int realm) {
        return realm < BLOOD_HEALING_START_REALM
                ? 0
                : 2 * Math.max(0, Math.min(MAX_REALM, realm));
    }
}
