package com.viyella.crimsonascension.event;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.viyella.crimsonascension.entity.BloodPuppetFactory;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public final class BloodBossManager {
    private static final Map<UUID, BossState> BOSSES = new ConcurrentHashMap<>();

    private BloodBossManager() {
    }

    public static void spawnForBloodMoon(ServerLevel level) {
        if (!BOSSES.isEmpty() || level.players().isEmpty()) {
            return;
        }
        ServerPlayer player = level.getRandomPlayer();
        if (player == null) {
            return;
        }
        double angle = level.random.nextDouble() * Math.PI * 2.0;
        Vec3 position = player.position().add(Math.cos(angle) * 14.0, 0.0, Math.sin(angle) * 14.0);
        Zombie boss = BloodPuppetFactory.spawnBoss(level, position);
        if (boss == null) {
            return;
        }

        ServerBossEvent bar = new ServerBossEvent(
                boss.getDisplayName(),
                BossEvent.BossBarColor.RED,
                BossEvent.BossBarOverlay.PROGRESS
        );
        bar.setDarkenScreen(false);
        bar.setPlayBossMusic(true);
        bar.setCreateWorldFog(true);
        for (ServerPlayer online : level.players()) {
            bar.addPlayer(online);
        }
        BOSSES.put(boss.getUUID(), new BossState(level, boss.getUUID(), bar));
        for (ServerPlayer online : level.players()) {
            online.displayClientMessage(Component.translatable(
                    "event.crimson_ascension.blood_moon.boss"
            ), false);
        }
    }

    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level) || BOSSES.isEmpty()) {
            return;
        }
        Iterator<BossState> iterator = BOSSES.values().iterator();
        while (iterator.hasNext()) {
            BossState state = iterator.next();
            if (state.level != level) {
                continue;
            }
            if (!(level.getEntity(state.bossId) instanceof Zombie boss) || !boss.isAlive()) {
                state.bar.removeAllPlayers();
                iterator.remove();
                continue;
            }
            state.bar.setProgress(Math.max(0.0F, boss.getHealth() / boss.getMaxHealth()));
            for (ServerPlayer player : level.players()) {
                if (!state.bar.getPlayers().contains(player)) {
                    state.bar.addPlayer(player);
                }
            }
        }
    }

    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        BossState state = BOSSES.remove(entity.getUUID());
        if (state != null) {
            state.bar.removeAllPlayers();
        }
    }

    private record BossState(ServerLevel level, UUID bossId, ServerBossEvent bar) {
    }
}
