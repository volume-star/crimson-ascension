package com.viyella.crimsonascension.cultivation;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.viyella.crimsonascension.CrimsonConfig;
import com.viyella.crimsonascension.effect.BleedingEffect;
import com.viyella.crimsonascension.entity.BloodPuppetFactory;
import com.viyella.crimsonascension.registry.ModMobEffects;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class TechniqueService {
    private static final Map<UUID, int[]> COOLDOWNS = new ConcurrentHashMap<>();

    private TechniqueService() {
    }

    public static void cast(ServerPlayer player, boolean secondary) {
        CultivationState state = ModAttachments.get(player);
        CultivationPath path = state.getPath();
        if (path == CultivationPath.NONE) {
            player.displayClientMessage(Component.translatable(
                    "technique.crimson_ascension.no_path"
            ), true);
            return;
        }
        if (state.getRealm() < 1) {
            player.displayClientMessage(Component.translatable(
                    "technique.crimson_ascension.realm_too_low"
            ), true);
            return;
        }

        int cost = secondary
                ? CrimsonConfig.SECONDARY_TECHNIQUE_COST.get()
                : CrimsonConfig.PRIMARY_TECHNIQUE_COST.get();
        long gameTime = player.serverLevel().getGameTime();
        int[] cooldowns = COOLDOWNS.computeIfAbsent(player.getUUID(), ignored -> new int[2]);
        int cooldownIndex = secondary ? 1 : 0;
        if (cooldowns[cooldownIndex] > gameTime) {
            long remaining = cooldowns[cooldownIndex] - gameTime;
            player.displayClientMessage(Component.translatable(
                    "technique.crimson_ascension.cooldown",
                    (remaining + 19) / 20
            ), true);
            return;
        }
        if (state.getBloodQi() < cost) {
            player.displayClientMessage(Component.translatable(
                    "technique.crimson_ascension.not_enough_blood",
                    cost,
                    state.getBloodQi()
            ), true);
            return;
        }

        boolean cast = switch (path) {
            case BLOOD_RIVER -> castBloodRiver(player, secondary);
            case WITHERED_BONE -> castWitheredBone(player, secondary);
            case GHOST_WOMB -> castGhostWomb(player, secondary);
            case NONE -> false;
        };
        if (!cast) {
            return;
        }

        state.addBloodQi(-cost);
        state.addCorruption(secondary ? 8 : 3);
        ModAttachments.sync(player);
        int cooldown = secondary
                ? CrimsonConfig.SECONDARY_COOLDOWN_TICKS.get()
                : CrimsonConfig.PRIMARY_COOLDOWN_TICKS.get();
        cooldowns[cooldownIndex] = (int) (gameTime + cooldown);
        player.displayClientMessage(Component.translatable(
                secondary ? "technique.crimson_ascension.cast_secondary" : "technique.crimson_ascension.cast_primary",
                Component.translatable(
                        secondary
                                ? "technique.crimson_ascension." + path.id() + ".secondary"
                                : "technique.crimson_ascension." + path.id() + ".primary"
                ),
                cost
        ), true);
    }

    private static boolean castBloodRiver(ServerPlayer player, boolean secondary) {
        ServerLevel level = player.serverLevel();
        if (secondary) {
            List<LivingEntity> targets = level.getEntitiesOfClass(
                    LivingEntity.class,
                    player.getBoundingBox().inflate(6.0),
                    entity -> entity != player && entity.isAlive() && !(entity instanceof Player)
            );
            for (LivingEntity target : targets) {
                target.hurt(level.damageSources().playerAttack(player), 6.0F);
                target.addEffect(new MobEffectInstance(
                        ModMobEffects.BLEEDING,
                        BleedingEffect.TICK_INTERVAL * 2,
                        0
                ), player);
            }
            level.sendParticles(ParticleTypes.CRIMSON_SPORE, player.getX(), player.getY(0.7), player.getZ(), 48, 3.5, 1.0, 3.5, 0.04);
            level.playSound(null, player.blockPosition(), SoundEvents.WARDEN_ATTACK_IMPACT, SoundSource.PLAYERS, 0.7F, 1.45F);
            return true;
        }

        Vec3 start = player.getEyePosition();
        Vec3 direction = player.getLookAngle();
        Vec3 target = start.add(direction.scale(8.0));
        BlockHitResult hit = level.clip(new ClipContext(
                start,
                target,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
        ));
        Vec3 destination = hit.getType() == HitResult.Type.MISS
                ? target
                : hit.getLocation().subtract(direction.scale(1.2));
        Vec3 origin = player.position();
        player.teleportTo(level, destination.x, destination.y, destination.z, player.getYRot(), player.getXRot());
        level.sendParticles(ParticleTypes.CRIMSON_SPORE, origin.x, origin.y + 0.8, origin.z, 24, 0.4, 0.8, 0.4, 0.03);
        level.sendParticles(ParticleTypes.CRIMSON_SPORE, destination.x, destination.y + 0.8, destination.z, 24, 0.4, 0.8, 0.4, 0.03);
        level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.7F, 0.75F);
        return true;
    }

    private static boolean castWitheredBone(ServerPlayer player, boolean secondary) {
        ServerLevel level = player.serverLevel();
        if (secondary) {
            Vec3 spawn = player.position().add(player.getLookAngle().scale(3.0));
            if (!BloodPuppetFactory.spawnTemporary(
                    level,
                    spawn,
                    CrimsonConfig.SUMMONED_PUPPET_LIFETIME_TICKS.get()
            )) {
                return false;
            }
            level.sendParticles(ParticleTypes.SOUL, spawn.x, spawn.y + 0.8, spawn.z, 24, 0.4, 0.8, 0.4, 0.03);
            return true;
        }

        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 140, 1));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 140, 1));
        level.sendParticles(ParticleTypes.SOUL, player.getX(), player.getY(0.6), player.getZ(), 18, 0.5, 1.0, 0.5, 0.01);
        return true;
    }

    private static boolean castGhostWomb(ServerPlayer player, boolean secondary) {
        ServerLevel level = player.serverLevel();
        if (secondary) {
            List<LivingEntity> targets = level.getEntitiesOfClass(
                    LivingEntity.class,
                    player.getBoundingBox().inflate(8.0),
                    entity -> entity != player && entity.isAlive() && !(entity instanceof Player)
            );
            for (LivingEntity target : targets) {
                target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 120, 1));
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 1));
            }
            level.sendParticles(ParticleTypes.SCULK_SOUL, player.getX(), player.getY(0.8), player.getZ(), 32, 4.0, 1.5, 4.0, 0.02);
            level.playSound(null, player.blockPosition(), SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.PLAYERS, 0.55F, 0.8F);
            return true;
        }

        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 120, 0));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 120, 1));
        level.sendParticles(ParticleTypes.SOUL, player.getX(), player.getY(0.6), player.getZ(), 18, 0.4, 1.0, 0.4, 0.01);
        return true;
    }
}
