package com.viyella.crimsonascension.effect;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class BleedingEffect extends MobEffect {
    public static final int TICK_INTERVAL = 40;

    public BleedingEffect() {
        super(MobEffectCategory.HARMFUL, 0x8F0716, ParticleTypes.DAMAGE_INDICATOR);
        setBlendDuration(20);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.isAlive()) {
            DamageSource source = entity.getLastHurtByMob() instanceof Player player
                    ? entity.damageSources().playerAttack(player)
                    : entity.damageSources().magic();
            entity.hurt(source, 1.0F + amplifier);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % TICK_INTERVAL == 0;
    }

}
