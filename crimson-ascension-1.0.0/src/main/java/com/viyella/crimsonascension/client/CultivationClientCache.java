package com.viyella.crimsonascension.client;

import com.viyella.crimsonascension.CrimsonAscension;
import com.viyella.crimsonascension.cultivation.CultivationState;
import com.viyella.crimsonascension.cultivation.ModAttachments;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public final class CultivationClientCache {
    private static CultivationState lastSyncedState;

    private CultivationClientCache() {
    }

    public static CultivationState get() {
        LocalPlayer player = Minecraft.getInstance().player;
        return player == null ? null : ModAttachments.get(player);
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            lastSyncedState = null;
            return;
        }

        CultivationState current = ModAttachments.get(player);
        if (!current.equals(lastSyncedState)) {
            lastSyncedState = current.copy();
            CrimsonAscension.LOGGER.debug("Client cultivation state synchronized: {}", current);
        }
    }
}
