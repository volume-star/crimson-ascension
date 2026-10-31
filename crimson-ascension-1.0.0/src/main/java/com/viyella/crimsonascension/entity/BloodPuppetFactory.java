package com.viyella.crimsonascension.entity;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.viyella.crimsonascension.CrimsonConfig;
import com.viyella.crimsonascension.registry.ModItems;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public final class BloodPuppetFactory {
    private static final Map<UUID, ExpiringPuppet> EXPIRING = new ConcurrentHashMap<>();

    private BloodPuppetFactory() {
    }

    public static boolean spawn(ServerLevel level, Vec3 position) {
        Zombie puppet = EntityType.ZOMBIE.create(level);
        if (puppet == null) {
            return false;
        }
        puppet.moveTo(position.x, position.y, position.z, level.random.nextFloat() * 360.0F, 0.0F);
        puppet.setCustomName(Component.translatable("entity.crimson_ascension.blood_puppet"));
        puppet.setCustomNameVisible(true);
        puppet.setPersistenceRequired();
        puppet.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.BLOODLETTING_DAGGER.get()));
        puppet.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        if (puppet.getAttribute(Attributes.MAX_HEALTH) != null) {
            puppet.getAttribute(Attributes.MAX_HEALTH).setBaseValue(30.0);
        }
        if (puppet.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            puppet.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(5.0);
        }
        puppet.setHealth(30.0F);
        return level.addFreshEntity(puppet);
    }

    public static boolean spawnTemporary(ServerLevel level, Vec3 position, int lifetimeTicks) {
        Zombie puppet = EntityType.ZOMBIE.create(level);
        if (puppet == null) {
            return false;
        }
        puppet.moveTo(position.x, position.y, position.z, level.random.nextFloat() * 360.0F, 0.0F);
        puppet.setCustomName(Component.translatable("entity.crimson_ascension.blood_puppet"));
        puppet.setCustomNameVisible(true);
        puppet.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.BLOODLETTING_DAGGER.get()));
        puppet.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        if (puppet.getAttribute(Attributes.MAX_HEALTH) != null) {
            puppet.getAttribute(Attributes.MAX_HEALTH).setBaseValue(30.0);
        }
        if (puppet.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            puppet.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(5.0);
        }
        puppet.setHealth(30.0F);
        if (!level.addFreshEntity(puppet)) {
            return false;
        }
        EXPIRING.put(
                puppet.getUUID(),
                new ExpiringPuppet(level, puppet, level.getGameTime() + lifetimeTicks)
        );
        return true;
    }

    public static void onLevelTick(LevelTickEvent.Post event) {
        if (EXPIRING.isEmpty()) {
            return;
        }
        Iterator<ExpiringPuppet> iterator = EXPIRING.values().iterator();
        while (iterator.hasNext()) {
            ExpiringPuppet entry = iterator.next();
            if (!entry.puppet.isAlive()) {
                iterator.remove();
            } else if (entry.level == event.getLevel() && entry.level.getGameTime() >= entry.expiresAt) {
                entry.puppet.discard();
                iterator.remove();
            }
        }
    }

    public static Zombie spawnBoss(ServerLevel level, Vec3 position) {
        Zombie boss = EntityType.ZOMBIE.create(level);
        if (boss == null) {
            return null;
        }
        boss.moveTo(position.x, position.y, position.z, level.random.nextFloat() * 360.0F, 0.0F);
        boss.setCustomName(Component.translatable("entity.crimson_ascension.blood_fiend"));
        boss.setCustomNameVisible(true);
        boss.setGlowingTag(true);
        boss.setPersistenceRequired();
        boss.setCanBreakDoors(true);
        boss.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.BLOODLETTING_DAGGER.get()));
        boss.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        int bossHealth = CrimsonConfig.BLOOD_FIEND_HEALTH.get();
        if (boss.getAttribute(Attributes.MAX_HEALTH) != null) {
            boss.getAttribute(Attributes.MAX_HEALTH).setBaseValue(bossHealth);
        }
        if (boss.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            boss.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(8.0);
        }
        if (boss.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
            boss.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.26);
        }
        if (boss.getAttribute(Attributes.ARMOR) != null) {
            boss.getAttribute(Attributes.ARMOR).setBaseValue(6.0);
        }
        boss.setHealth(bossHealth);
        MonsterRealmService.assignBloodFiend(level, boss);
        return level.addFreshEntity(boss) ? boss : null;
    }

    private record ExpiringPuppet(ServerLevel level, Zombie puppet, long expiresAt) {
    }
}
