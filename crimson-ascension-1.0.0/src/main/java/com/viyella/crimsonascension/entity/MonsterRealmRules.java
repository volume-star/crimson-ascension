package com.viyella.crimsonascension.entity;

import com.viyella.crimsonascension.cultivation.CultivationRules;

/**
 * Pure monster-realm scaling rules, kept independent from Minecraft entities
 * so the balance curve can be validated by the dependency-light checks.
 */
public final class MonsterRealmRules {
    public static final int MIN_BOSS_REALM = 3;
    private static final double[] HEALTH_MULTIPLIERS = {
            1.00,
            1.45,
            2.00,
            2.65,
            3.40,
            4.30,
            5.40
    };
    private static final double[] DAMAGE_MULTIPLIERS = {
            1.00,
            1.25,
            1.55,
            1.90,
            2.30,
            2.75,
            3.30
    };
    private static final double[] SPEED_MULTIPLIERS = {
            1.00,
            1.03,
            1.06,
            1.09,
            1.12,
            1.15,
            1.18
    };
    private static final double[] ARMOR_BONUSES = {
            0.0,
            2.0,
            4.0,
            6.0,
            9.0,
            12.0,
            16.0
    };

    private MonsterRealmRules() {
    }

    public static int clampRealm(int realm) {
        return Math.max(0, Math.min(CultivationRules.MAX_REALM, realm));
    }

    public static int randomRealm(int roll) {
        return Math.floorMod(roll, CultivationRules.MAX_REALM + 1);
    }

    public static int bossRealm(int roll) {
        int realmCount = CultivationRules.MAX_REALM - MIN_BOSS_REALM + 1;
        return MIN_BOSS_REALM + Math.floorMod(roll, realmCount);
    }

    public static double healthMultiplier(int realm) {
        return HEALTH_MULTIPLIERS[clampRealm(realm)];
    }

    public static double damageMultiplier(int realm) {
        return DAMAGE_MULTIPLIERS[clampRealm(realm)];
    }

    public static double speedMultiplier(int realm) {
        return SPEED_MULTIPLIERS[clampRealm(realm)];
    }

    public static double armorBonus(int realm) {
        return ARMOR_BONUSES[clampRealm(realm)];
    }

    public static double scaledHealth(double baseHealth, int realm) {
        return Math.max(1.0, baseHealth * healthMultiplier(realm));
    }

    public static double scaledDamage(double baseDamage, int realm) {
        return Math.max(0.0, baseDamage * damageMultiplier(realm));
    }
}
