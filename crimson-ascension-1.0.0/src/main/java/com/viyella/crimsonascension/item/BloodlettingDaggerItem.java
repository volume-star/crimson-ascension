package com.viyella.crimsonascension.item;

import java.util.List;

import com.viyella.crimsonascension.effect.BleedingEffect;
import com.viyella.crimsonascension.registry.ModMobEffects;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;

public final class BloodlettingDaggerItem extends SwordItem {
    public static final int BLEEDING_DURATION_TICKS = 100;

    public BloodlettingDaggerItem(Properties properties) {
        super(Tiers.IRON, properties);
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!target.level().isClientSide()) {
            target.addEffect(new MobEffectInstance(
                    ModMobEffects.BLEEDING,
                    BLEEDING_DURATION_TICKS,
                    0,
                    false,
                    true,
                    true
            ), attacker);

            if (target.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        ParticleTypes.CRIMSON_SPORE,
                        target.getX(),
                        target.getY(0.65),
                        target.getZ(),
                        10,
                        0.3,
                        0.45,
                        0.3,
                        0.02
                );
            }
        }
        super.postHurtEnemy(stack, target, attacker);
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltipComponents,
            TooltipFlag tooltipFlag
    ) {
        tooltipComponents.add(Component.translatable(
                "item.crimson_ascension.bloodletting_dagger.tooltip",
                BLEEDING_DURATION_TICKS / 20,
                BleedingEffect.TICK_INTERVAL / 20
        ));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
