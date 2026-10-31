package com.viyella.crimsonascension.cultivation;

import com.viyella.crimsonascension.entity.MonsterRealmRules;

import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;

public final class CultivationStateChecks {
    private CultivationStateChecks() {
    }

    public static void main(String[] args) {
        defaultsAreNeutral();
        valuesAreClamped();
        additionsClampWithoutOverflow();
        codecRoundTrips();
        streamCodecRoundTrips();
        pathSelectionIsValidated();
        refinementExchangeRatioIsStable();
        bloodCapacityScalesWithRealm();
        refinedBloodCostScalesWithRealm();
        breakthroughUsesOnlyRefinedBlood();
        failedBreakthroughConservesResources();
        bloodSpendingDoesNotChangeRefinedBloodCapacity();
        monsterRealmStatsScaleWithRealm();
        realmPassiveBloodValuesAreScaled();
        resetClearsEverything();
        System.out.println("CultivationState checks passed.");
    }

    private static void defaultsAreNeutral() {
        CultivationState state = new CultivationState();
        check(state.equals(new CultivationState(0, 0, 0, 0)), "Default state was not neutral.");
    }

    private static void valuesAreClamped() {
        CultivationState state = new CultivationState(
                Integer.MIN_VALUE,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE
        );
        check(state.getBloodQi() == 0, "Negative blood qi was not clamped.");
        check(state.getRefinedBlood() == CultivationRules.MAX_REFINED_BLOOD, "Refined blood maximum failed.");
        check(state.getRealm() == CultivationRules.MAX_REALM, "Realm maximum failed.");
        check(state.getCorruption() == CultivationRules.MAX_CORRUPTION, "Corruption maximum failed.");
    }

    private static void additionsClampWithoutOverflow() {
        CultivationState state = new CultivationState(999_999, 99_999, 5, 999_999);
        state.addBloodQi(Integer.MAX_VALUE);
        state.addRefinedBlood(Integer.MAX_VALUE);
        state.addRealm(Integer.MAX_VALUE);
        state.addCorruption(Integer.MAX_VALUE);
        state.addBloodQi(Integer.MIN_VALUE);
        state.addRefinedBlood(Integer.MIN_VALUE);
        state.addRealm(Integer.MIN_VALUE);
        state.addCorruption(Integer.MIN_VALUE);

        check(state.getBloodQi() == 0, "Blood qi arithmetic overflowed or failed to clamp.");
        check(state.getRefinedBlood() == 0, "Refined blood arithmetic overflowed or failed to clamp.");
        check(state.getRealm() == 0, "Realm arithmetic overflowed or failed to clamp.");
        check(state.getCorruption() == 0, "Corruption arithmetic overflowed or failed to clamp.");
    }

    private static void codecRoundTrips() {
        CultivationState original = new CultivationState(
                321,
                45,
                3,
                678,
                CultivationPath.BLOOD_RIVER.id()
        );
        Tag encoded = CultivationState.CODEC.encodeStart(NbtOps.INSTANCE, original)
                .getOrThrow(message -> new IllegalStateException(message));
        CultivationState decoded = CultivationState.CODEC.parse(NbtOps.INSTANCE, encoded)
                .getOrThrow(message -> new IllegalStateException(message));
        check(decoded.equals(original), "Cultivation state failed its NBT codec round trip.");
    }

    private static void streamCodecRoundTrips() {
        CultivationState original = new CultivationState(1_234, 56, 4, 432);
        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY);
        try {
            CultivationState.STREAM_CODEC.encode(buffer, original);
            CultivationState decoded = CultivationState.STREAM_CODEC.decode(buffer);
            check(decoded.equals(original), "Cultivation state failed its network codec round trip.");
        } finally {
            buffer.release();
        }
    }

    private static void refinedBloodCostScalesWithRealm() {
        for (int realm = 0; realm <= CultivationRules.MAX_REALM; realm++) {
            check(
                    CultivationRules.breakthroughRefinedBloodCost(realm)
                            == CultivationRules.REFINED_BLOOD_COST_PER_REALM * (realm + 1),
                    "Refined blood cost does not scale correctly at realm " + realm + "."
            );
        }
    }

    private static void pathSelectionIsValidated() {
        CultivationState state = new CultivationState(0, 0, 1, 0, "invalid_path");
        check(state.getPath() == CultivationPath.NONE, "Invalid stored path was not rejected.");
        state.setPath(CultivationPath.GHOST_WOMB);
        check(state.getPath() == CultivationPath.GHOST_WOMB, "Valid cultivation path was not stored.");
        state.reset();
        check(state.getPath() == CultivationPath.NONE, "Reset did not clear the cultivation path.");
    }

    private static void refinementExchangeRatioIsStable() {
        check(CultivationRules.BLOOD_PER_REFINED == 10, "Blood-to-refined ratio changed unexpectedly.");
    }

    private static void bloodCapacityScalesWithRealm() {
        CultivationState state = new CultivationState();
        for (int realm = 0; realm <= CultivationRules.MAX_REALM; realm++) {
            state.setRealm(realm);
            state.setBloodQi(Integer.MAX_VALUE);
            check(
                    state.getBloodQi() == 100 * (realm + 1),
                    "Blood qi capacity does not scale at realm " + realm + "."
            );
        }
        state.setBloodQi(700);
        state.setRealm(0);
        check(state.getBloodQi() == 100, "Lowering the realm did not clamp blood qi.");
    }

    private static void breakthroughUsesOnlyRefinedBlood() {
        CultivationState state = new CultivationState(0, 100, 0, 0);
        check(state.tryBreakthrough(), "Valid breakthrough was rejected.");
        check(state.getRealm() == 1, "Breakthrough did not advance the realm.");
        check(
                state.getRefinedBlood() == 95,
                "Breakthrough did not consume exactly five refined blood or discarded the remainder."
        );
        check(state.getCorruption() == 25, "Breakthrough did not add the expected corruption.");
    }

    private static void failedBreakthroughConservesResources() {
        CultivationState state = new CultivationState(700, 4, 0, 12);
        check(!state.tryBreakthrough(), "Breakthrough succeeded without enough refined blood.");
        check(state.equals(new CultivationState(700, 4, 0, 12)), "Failed breakthrough changed resources.");
    }

    private static void bloodSpendingDoesNotChangeRefinedBloodCapacity() {
        CultivationState state = new CultivationState(100, 10, 0, 0);
        state.addBloodQi(-100);
        state.addRefinedBlood(CultivationRules.MAX_REFINED_BLOOD);
        check(
                state.getRefinedBlood() == CultivationRules.MAX_REFINED_BLOOD,
                "Spending blood qi reduced the refined blood capacity."
        );
    }

    private static void monsterRealmStatsScaleWithRealm() {
        double previousHealth = 0.0;
        double previousDamage = 0.0;
        double previousSpeed = 0.0;
        double previousArmor = 0.0;
        for (int realm = 0; realm <= CultivationRules.MAX_REALM; realm++) {
            double health = MonsterRealmRules.healthMultiplier(realm);
            double damage = MonsterRealmRules.damageMultiplier(realm);
            double speed = MonsterRealmRules.speedMultiplier(realm);
            double armor = MonsterRealmRules.armorBonus(realm);
            check(health >= previousHealth, "Monster health multiplier regressed at realm " + realm + ".");
            check(damage >= previousDamage, "Monster damage multiplier regressed at realm " + realm + ".");
            check(speed >= previousSpeed, "Monster speed multiplier regressed at realm " + realm + ".");
            check(armor >= previousArmor, "Monster armor bonus regressed at realm " + realm + ".");
            previousHealth = health;
            previousDamage = damage;
            previousSpeed = speed;
            previousArmor = armor;
        }
        for (int roll = 0; roll <= CultivationRules.MAX_REALM; roll++) {
            check(MonsterRealmRules.randomRealm(roll) == roll,
                    "Random monster realm mapping failed for roll " + roll + ".");
        }
        check(MonsterRealmRules.randomRealm(CultivationRules.MAX_REALM + 1) == 0,
                "Random monster realm did not wrap to Mortal Body.");
        for (int roll = 0; roll < 10; roll++) {
            int bossRealm = MonsterRealmRules.bossRealm(roll);
            check(bossRealm >= MonsterRealmRules.MIN_BOSS_REALM
                            && bossRealm <= CultivationRules.MAX_REALM,
                    "Blood Fiend realm left the 3-6 range.");
        }
    }

    private static void realmPassiveBloodValuesAreScaled() {
        check(CultivationRules.passiveBloodRecoveryForRealm(0) == 0,
                "Mortal Body should not regenerate blood qi.");
        check(CultivationRules.passiveBloodRecoveryForRealm(1) == 0,
                "Blood Drawing should not regenerate blood qi.");
        for (int realm = 2; realm <= CultivationRules.MAX_REALM; realm++) {
            check(CultivationRules.passiveBloodRecoveryForRealm(realm) == realm - 1,
                    "Blood recovery does not scale at realm " + realm + ".");
        }
        check(CultivationRules.passiveBloodHealingCostForRealm(3) == 0,
                "Bone Refinement should not use passive blood healing.");
        check(CultivationRules.passiveBloodHealingCostForRealm(4) == 8,
                "Blood Core healing cost is incorrect.");
        check(CultivationRules.passiveBloodHealingCostForRealm(5) == 10,
                "Yin Spirit healing cost is incorrect.");
        check(CultivationRules.passiveBloodHealingCostForRealm(6) == 12,
                "Blood Immortal healing cost is incorrect.");
    }

    private static void resetClearsEverything() {
        CultivationState state = new CultivationState(321, 45, 3, 678);
        state.reset();
        check(state.equals(new CultivationState()), "Reset did not clear all cultivation data.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
