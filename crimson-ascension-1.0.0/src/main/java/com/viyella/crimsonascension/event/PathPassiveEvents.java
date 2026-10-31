package com.viyella.crimsonascension.event;

import com.viyella.crimsonascension.cultivation.CultivationPath;
import com.viyella.crimsonascension.cultivation.CultivationRules;
import com.viyella.crimsonascension.cultivation.CultivationState;
import com.viyella.crimsonascension.cultivation.ModAttachments;
import com.viyella.crimsonascension.CrimsonConfig;
import com.viyella.crimsonascension.CrimsonAscension;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class PathPassiveEvents {
    private static final ResourceLocation REALM_HEALTH_ID = ResourceLocation.fromNamespaceAndPath(
            CrimsonAscension.MOD_ID,
            "realm_max_health"
    );

    private PathPassiveEvents() {
    }

    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (event.getEntity() instanceof Player player
                && event.getSource().is(DamageTypeTags.IS_FALL)
                && ModAttachments.get(player).getPath() == CultivationPath.WITHERED_BONE) {
            event.setNewDamage(event.getNewDamage() * 0.5F);
        }
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (player.tickCount % 20 == 0) {
            updateRealmHealth(player);
        }

        CultivationState state = ModAttachments.get(player);
        boolean changed = false;
        if (player.tickCount % CultivationRules.BLOOD_RECOVERY_INTERVAL_TICKS == 0) {
            changed = applyRealmBloodRecovery(player, state);
        }
        if (player.tickCount % 200 == 0
                && state.getPath() == CultivationPath.GHOST_WOMB
                && state.getCorruption() > 0) {
            state.addCorruption(-1);
            changed = true;
        }
        if (changed) {
            ModAttachments.sync(player);
        }
    }

    private static boolean applyRealmBloodRecovery(ServerPlayer player, CultivationState state) {
        int realm = state.getRealm();
        boolean changed = false;

        int recovery = CultivationRules.passiveBloodRecoveryForRealm(realm);
        if (recovery > 0 && state.getBloodQi() < state.getBloodQiCapacity()) {
            state.addBloodQi(recovery);
            changed = true;
        }

        int healingCost = CultivationRules.passiveBloodHealingCostForRealm(realm);
        if (healingCost > 0
                && player.getHealth() < player.getMaxHealth()
                && state.getBloodQi() >= healingCost) {
            state.addBloodQi(-healingCost);
            player.heal(1.0F);
            changed = true;
        }
        return changed;
    }

    private static void updateRealmHealth(ServerPlayer player) {
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null) {
            return;
        }
        double bonus = ModAttachments.get(player).getRealm() * CrimsonConfig.REALM_HEALTH_BONUS.get();
        AttributeModifier current = maxHealth.getModifier(REALM_HEALTH_ID);
        if (current == null || current.amount() != bonus) {
            maxHealth.removeModifier(REALM_HEALTH_ID);
            if (bonus > 0.0) {
                maxHealth.addOrReplacePermanentModifier(new AttributeModifier(
                        REALM_HEALTH_ID,
                        bonus,
                        AttributeModifier.Operation.ADD_VALUE
                ));
            }
        }
    }
}
