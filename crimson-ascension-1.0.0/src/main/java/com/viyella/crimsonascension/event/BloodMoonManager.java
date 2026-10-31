package com.viyella.crimsonascension.event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.viyella.crimsonascension.CrimsonConfig;
import com.viyella.crimsonascension.entity.BloodPuppetFactory;
import com.viyella.crimsonascension.network.BloodMoonStatusPayload;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public final class BloodMoonManager {
    private static final Map<ResourceKey<Level>, MoonState> STATES = new ConcurrentHashMap<>();

    private BloodMoonManager() {
    }

    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level) || level.dimension() != Level.OVERWORLD) {
            return;
        }

        long dayTime = level.getDayTime() % 24000L;
        boolean night = dayTime >= 13000L && dayTime < 23000L;
        MoonState state = STATES.computeIfAbsent(level.dimension(), ignored -> new MoonState());
        long nightKey = level.getDayTime() / 24000L;

        if (!night) {
            if (state.active) {
                state.active = false;
                broadcast(level, "event.crimson_ascension.blood_moon.end");
                sendStatus(level, false);
            }
            return;
        }

        if (state.checkedNight != nightKey) {
            state.checkedNight = nightKey;
            state.active = level.random.nextInt(100) < CrimsonConfig.BLOOD_MOON_CHANCE_PERCENT.get();
            state.spawned = 0;
            if (state.active) {
                broadcast(level, "event.crimson_ascension.blood_moon.start");
                sendStatus(level, true);
                BloodBossManager.spawnForBloodMoon(level);
            }
        }
        if (state.active
                && state.spawned < CrimsonConfig.BLOOD_MOON_MAX_PUPPETS.get()
                && level.getGameTime() % 400L == 0L) {
            ServerPlayer player = level.getRandomPlayer();
            if (player != null) {
                double angle = level.random.nextDouble() * Math.PI * 2.0;
                Vec3 position = player.position().add(Math.cos(angle) * 10.0, 0.0, Math.sin(angle) * 10.0);
                if (BloodPuppetFactory.spawn(level, position)) {
                    state.spawned++;
                }
            }
        }
    }

    private static void broadcast(ServerLevel level, String messageKey) {
        Component message = Component.translatable(messageKey);
        for (ServerPlayer player : level.players()) {
            player.displayClientMessage(message, false);
        }
    }

    private static void sendStatus(ServerLevel level, boolean active) {
        BloodMoonStatusPayload payload = new BloodMoonStatusPayload(active);
        for (ServerPlayer player : level.players()) {
            PacketDistributor.sendToPlayer(player, payload);
        }
    }

    private static final class MoonState {
        private long checkedNight = -1L;
        private boolean active;
        private int spawned;
    }
}
