package com.viyella.crimsonascension.network;

import com.viyella.crimsonascension.menu.BloodVaultMenu;
import com.viyella.crimsonascension.menu.BloodCondensationAltarMenu;
import com.viyella.crimsonascension.client.BloodMoonClientState;
import com.viyella.crimsonascension.cultivation.CultivationPath;
import com.viyella.crimsonascension.cultivation.CultivationState;
import com.viyella.crimsonascension.cultivation.ModAttachments;
import com.viyella.crimsonascension.cultivation.TechniqueService;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModNetwork {
    private static final String PROTOCOL_VERSION = "2";

    private ModNetwork() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToServer(
                OpenBloodVaultPayload.TYPE,
                OpenBloodVaultPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (context.player() instanceof ServerPlayer player
                            && player.isAlive()
                            && !player.isSpectator()
                            && !player.hasContainerOpen()) {
                        BloodVaultMenu.openFor(player);
                    }
                })
        );
        registrar.playToClient(
                BloodMoonStatusPayload.TYPE,
                BloodMoonStatusPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(
                        () -> BloodMoonClientState.setActive(payload.active())
                )
        );
        registrar.playToServer(
                AltarActionPayload.TYPE,
                AltarActionPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (context.player() instanceof ServerPlayer player
                            && player.containerMenu instanceof BloodCondensationAltarMenu menu) {
                        if (payload.breakthrough()) {
                            menu.attemptBreakthrough(player);
                        } else {
                            menu.exchange(player, payload.refinedAmount());
                        }
                    }
                })
        );
        registrar.playToServer(
                SelectPathPayload.TYPE,
                SelectPathPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (context.player() instanceof ServerPlayer player) {
                        selectPath(player, payload.path());
                    }
                })
        );
        registrar.playToServer(
                CastTechniquePayload.TYPE,
                CastTechniquePayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (context.player() instanceof ServerPlayer player) {
                        TechniqueService.cast(player, payload.secondary());
                    }
                })
        );
    }

    private static void selectPath(ServerPlayer player, String pathId) {
        CultivationPath requested = CultivationPath.selectableById(pathId).orElse(null);
        if (requested == null) {
            return;
        }
        CultivationState state = ModAttachments.get(player);
        if (state.getRealm() < 1) {
            player.displayClientMessage(Component.translatable(
                    "technique.crimson_ascension.realm_too_low"
            ), true);
            return;
        }
        if (state.getPath() != CultivationPath.NONE && state.getPath() != requested) {
            player.displayClientMessage(Component.translatable(
                    "technique.crimson_ascension.path_locked"
            ), true);
            return;
        }
        state.setPath(requested);
        ModAttachments.sync(player);
        player.displayClientMessage(Component.translatable(
                "technique.crimson_ascension.path_selected",
                Component.translatable(requested.translationKey())
        ), false);
    }
}
