package com.viyella.crimsonascension.event;

import com.viyella.crimsonascension.cultivation.CultivationState;
import com.viyella.crimsonascension.cultivation.CultivationPath;
import com.viyella.crimsonascension.cultivation.ModAttachments;
import com.viyella.crimsonascension.registry.ModItems;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public final class BloodlettingEvents {
    private static final int MAX_BLOOD_PER_KILL = 100;

    private BloodlettingEvents() {
    }

    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide()
                || event.getEntity() instanceof Player
                || !(event.getSource().getEntity() instanceof ServerPlayer player)
                || !player.getMainHandItem().is(ModItems.BLOODLETTING_DAGGER.get())) {
            return;
        }

        int gained = Mth.clamp(Mth.ceil(event.getEntity().getMaxHealth()), 1, MAX_BLOOD_PER_KILL);
        CultivationState state = ModAttachments.get(player);
        int before = state.getBloodQi();
        state.addBloodQi(gained);
        int actualGain = state.getBloodQi() - before;
        ModAttachments.sync(player);

        if (actualGain > 0) {
            if (state.getPath() == CultivationPath.BLOOD_RIVER) {
                player.heal(1.0F);
            }
            player.displayClientMessage(Component.translatable(
                    "item.crimson_ascension.bloodletting_dagger.harvested",
                    actualGain,
                    state.getBloodQi()
            ), true);
        }
    }
}
