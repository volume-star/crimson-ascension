package com.viyella.crimsonascension.entity;

import com.viyella.crimsonascension.CrimsonAscension;
import com.viyella.crimsonascension.cultivation.CultivationRules;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

public final class MonsterRealmService {
    private static final String KEY_PREFIX = CrimsonAscension.MOD_ID + ".monster_realm.";
    private static final String REALM_KEY = KEY_PREFIX + "realm";
    private static final String BASE_NAME_KEY = KEY_PREFIX + "base_name";
    private static final String BASE_HEALTH_KEY = KEY_PREFIX + "base_health";
    private static final String BASE_DAMAGE_KEY = KEY_PREFIX + "base_damage";
    private static final String BASE_SPEED_KEY = KEY_PREFIX + "base_speed";
    private static final String BASE_ARMOR_KEY = KEY_PREFIX + "base_armor";
    private static final String BOSS_KEY = KEY_PREFIX + "blood_fiend";
    private MonsterRealmService() {
    }

    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)
                || !(event.getEntity() instanceof Mob mob)
                || !(mob instanceof Enemy)) {
            return;
        }

        CompoundTag data = mob.getPersistentData();
        boolean bloodFiend = isBloodFiend(data);
        if (data.contains(REALM_KEY)) {
            if (bloodFiend && getRealm(mob) < MonsterRealmRules.MIN_BOSS_REALM) {
                setRealm(mob, randomBloodFiendRealm(level));
            }
            return;
        }

        int realm = bloodFiend
                ? randomBloodFiendRealm(level)
                : MonsterRealmRules.randomRealm(level.random.nextInt(CultivationRules.MAX_REALM + 1));
        assign(level, mob, realm, !event.loadedFromDisk());
    }

    public static void assignBloodFiend(ServerLevel level, Mob mob) {
        mob.getPersistentData().putBoolean(BOSS_KEY, true);
        assign(level, mob, randomBloodFiendRealm(level), true);
    }

    private static int assign(ServerLevel level, Mob mob, int realm, boolean fullHealth) {
        CompoundTag data = mob.getPersistentData();
        captureBaseStats(mob, data, level);
        int clampedRealm = MonsterRealmRules.clampRealm(realm);
        data.putInt(REALM_KEY, clampedRealm);
        applyStats(mob, data, clampedRealm, fullHealth);
        applyRealmName(mob, data, clampedRealm);
        return clampedRealm;
    }

    public static int setRealm(Mob mob, int realm) {
        if (!(mob.level() instanceof ServerLevel level)) {
            return getRealm(mob);
        }
        CompoundTag data = mob.getPersistentData();
        if (!data.contains(REALM_KEY)) {
            captureBaseStats(mob, data, level);
        }
        int clampedRealm = MonsterRealmRules.clampRealm(realm);
        if (isBloodFiend(data)) {
            clampedRealm = Math.max(MonsterRealmRules.MIN_BOSS_REALM, clampedRealm);
        }
        data.putInt(REALM_KEY, clampedRealm);
        applyStats(mob, data, clampedRealm, false);
        applyRealmName(mob, data, clampedRealm);
        return clampedRealm;
    }

    public static int getRealm(Mob mob) {
        CompoundTag data = mob.getPersistentData();
        return data.contains(REALM_KEY) ? MonsterRealmRules.clampRealm(data.getInt(REALM_KEY)) : 0;
    }

    private static void captureBaseStats(Mob mob, CompoundTag data, ServerLevel level) {
        if (!data.contains(BASE_HEALTH_KEY)) {
            data.putDouble(BASE_HEALTH_KEY, attributeBase(mob, Attributes.MAX_HEALTH, 20.0));
            data.putDouble(BASE_DAMAGE_KEY, attributeBase(mob, Attributes.ATTACK_DAMAGE, 2.0));
            data.putDouble(BASE_SPEED_KEY, attributeBase(mob, Attributes.MOVEMENT_SPEED, 0.23));
            data.putDouble(BASE_ARMOR_KEY, attributeBase(mob, Attributes.ARMOR, 0.0));
        }
        if (!data.contains(BASE_NAME_KEY)) {
            MutableComponent baseName = mob.getCustomName() == null
                    ? mob.getType().getDescription().copy()
                    : mob.getCustomName().copy();
            data.putString(BASE_NAME_KEY, Component.Serializer.toJson(baseName, level.registryAccess()));
        }
    }

    private static void applyStats(Mob mob, CompoundTag data, int realm, boolean fullHealth) {
        float previousMaxHealth = Math.max(1.0F, mob.getMaxHealth());
        float healthRatio = Mth.clamp(mob.getHealth() / previousMaxHealth, 0.0F, 1.0F);
        double baseHealth = Math.max(1.0, data.getDouble(BASE_HEALTH_KEY));
        double baseDamage = Math.max(0.0, data.getDouble(BASE_DAMAGE_KEY));
        double baseSpeed = Math.max(0.0, data.getDouble(BASE_SPEED_KEY));
        double baseArmor = Math.max(0.0, data.getDouble(BASE_ARMOR_KEY));

        setAttribute(mob, Attributes.MAX_HEALTH, MonsterRealmRules.scaledHealth(baseHealth, realm));
        setAttribute(mob, Attributes.ATTACK_DAMAGE, MonsterRealmRules.scaledDamage(baseDamage, realm));
        setAttribute(
                mob,
                Attributes.MOVEMENT_SPEED,
                baseSpeed * MonsterRealmRules.speedMultiplier(realm)
        );
        setAttribute(mob, Attributes.ARMOR, baseArmor + MonsterRealmRules.armorBonus(realm));

        float maxHealth = mob.getMaxHealth();
        mob.setHealth(fullHealth ? maxHealth : Math.max(1.0F, maxHealth * healthRatio));
    }

    private static void applyRealmName(Mob mob, CompoundTag data, int realm) {
        MutableComponent baseName = Component.Serializer.fromJson(
                data.getString(BASE_NAME_KEY),
                mob.level().registryAccess()
        );
        if (baseName == null) {
            baseName = mob.getType().getDescription().copy();
        }
        mob.setCustomName(Component.translatable(
                "entity.crimson_ascension.realm_named_mob",
                baseName,
                Component.translatable("crimson_ascension.realm." + realm)
        ));
        mob.setCustomNameVisible(true);
    }

    private static boolean isBloodFiend(CompoundTag data) {
        return data.getBoolean(BOSS_KEY)
                || data.getString(BASE_NAME_KEY).contains("entity.crimson_ascension.blood_fiend");
    }

    private static int randomBloodFiendRealm(ServerLevel level) {
        int realmCount = CultivationRules.MAX_REALM - MonsterRealmRules.MIN_BOSS_REALM + 1;
        return MonsterRealmRules.bossRealm(level.random.nextInt(realmCount));
    }

    private static double attributeBase(
            Mob mob,
            net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute,
            double fallback
    ) {
        AttributeInstance instance = mob.getAttribute(attribute);
        return instance == null ? fallback : instance.getBaseValue();
    }

    private static void setAttribute(
            Mob mob,
            net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute,
            double value
    ) {
        AttributeInstance instance = mob.getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(value);
        }
    }
}
