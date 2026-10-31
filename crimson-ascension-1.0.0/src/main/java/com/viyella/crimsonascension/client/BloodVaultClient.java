package com.viyella.crimsonascension.client;

import java.util.Locale;

import com.mojang.blaze3d.platform.InputConstants;
import com.viyella.crimsonascension.CrimsonAscension;
import com.viyella.crimsonascension.cultivation.CultivationRules;
import com.viyella.crimsonascension.cultivation.CultivationState;
import com.viyella.crimsonascension.network.CastTechniquePayload;
import com.viyella.crimsonascension.network.OpenBloodVaultPayload;
import com.viyella.crimsonascension.registry.ModMenus;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

public final class BloodVaultClient {
    private static final int SLOT_HIGHLIGHT_COLOR = 0xFFFFFFFF;
    private static final int SLOT_SHADOW_COLOR = 0xFF555555;
    private static final int SLOT_DARK_COLOR = 0xFF373737;
    private static final int HEALTH_BAR_TRACK_COLOR = 0xFF4A090C;
    private static final int HEALTH_BAR_FILL_COLOR = 0xFFD32932;
    private static final int MANA_BAR_TRACK_COLOR = 0xFF0D2349;
    private static final int MANA_BAR_FILL_COLOR = 0xFF3D86EC;
    private static final int EXPERIENCE_BAR_TRACK_COLOR = 0xFF3E3810;
    private static final int EXPERIENCE_BAR_FILL_COLOR = 0xFFE4C42E;
    private static final ResourceLocation HUD_LAYER =
            ResourceLocation.fromNamespaceAndPath(CrimsonAscension.MOD_ID, "cultivation_hud");

    public static final KeyMapping OPEN_BLOOD_VAULT = new KeyMapping(
            "key.crimson_ascension.open_blood_vault",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.categories.crimson_ascension"
    );

    public static final KeyMapping OPEN_CULTIVATION_STATUS = new KeyMapping(
            "key.crimson_ascension.open_cultivation_status",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "key.categories.crimson_ascension"
    );

    public static final KeyMapping CAST_TECHNIQUE = new KeyMapping(
            "key.crimson_ascension.cast_technique",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.categories.crimson_ascension"
    );

    private BloodVaultClient() {
    }

    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_BLOOD_VAULT);
        event.register(OPEN_CULTIVATION_STATUS);
        event.register(CAST_TECHNIQUE);
    }

    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.BLOOD_VAULT.get(), BloodVaultScreen::new);
        event.register(ModMenus.BLOOD_ALTAR.get(), BloodCondensationAltarScreen::new);
    }

    public static void registerHud(RegisterGuiLayersEvent event) {
        event.registerAboveAll(HUD_LAYER, BloodVaultClient::renderHud);
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        updateAmbience(minecraft);
        while (CAST_TECHNIQUE.consumeClick()) {
            if (minecraft.player != null && minecraft.screen == null) {
                PacketDistributor.sendToServer(new CastTechniquePayload(minecraft.player.isShiftKeyDown()));
            }
        }
        while (OPEN_CULTIVATION_STATUS.consumeClick()) {
            if (minecraft.player != null && minecraft.screen == null) {
                minecraft.setScreen(new CultivationStatusScreen());
            }
        }
        while (OPEN_BLOOD_VAULT.consumeClick()) {
            if (minecraft.player != null && minecraft.screen == null) {
                PacketDistributor.sendToServer(OpenBloodVaultPayload.INSTANCE);
            }
        }
    }

    private static void renderHud(GuiGraphics graphics, net.minecraft.client.DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        CultivationState state = CultivationClientCache.get();
        if (state == null || minecraft.options.hideGui) {
            return;
        }

        int x = 8;
        int y = 8;
        int width = 96;
        int height = 44;

        // Compact vanilla-style frame. Only health and blood qi show values.
        graphics.fill(x, y, x + width, y + height, SLOT_DARK_COLOR);
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, SLOT_HIGHLIGHT_COLOR);
        graphics.fill(x + width - 2, y + 1, x + width - 1, y + height - 1, SLOT_SHADOW_COLOR);
        graphics.fill(x + 1, y + height - 2, x + width - 1, y + height - 1, SLOT_SHADOW_COLOR);

        String realmName = Component.translatable("crimson_ascension.realm." + state.getRealm()).getString();
        graphics.fill(x + 2, y + 2, x + 34, y + 17, SLOT_DARK_COLOR);
        graphics.drawCenteredString(
                minecraft.font,
                minecraft.font.plainSubstrByWidth(realmName, 30),
                x + 18,
                y + 5,
                0xFFFFFFFF
        );
        graphics.fill(x + 34, y + 3, x + 35, y + 16, SLOT_SHADOW_COLOR);

        float health = minecraft.player == null ? 0.0F : minecraft.player.getHealth();
        float maxHealth = minecraft.player == null ? 1.0F : minecraft.player.getMaxHealth();
        int healthBarX = x + 36;
        int healthBarWidth = width - 39;
        drawBar(
                graphics,
                healthBarX,
                y + 3,
                healthBarWidth,
                13,
                health / Math.max(1.0F, maxHealth),
                HEALTH_BAR_TRACK_COLOR,
                HEALTH_BAR_FILL_COLOR
        );
        graphics.drawCenteredString(
                minecraft.font,
                String.format(Locale.ROOT, "%.1f/%.1f", health, maxHealth),
                healthBarX + healthBarWidth / 2,
                y + 5,
                0xFFFFFFFF
        );
        drawBar(
                graphics,
                x + 3,
                y + 18,
                width - 6,
                13,
                state.getBloodQi() / (float) state.getBloodQiCapacity(),
                MANA_BAR_TRACK_COLOR,
                MANA_BAR_FILL_COLOR
        );
        graphics.drawCenteredString(
                minecraft.font,
                state.getBloodQi() + "/" + state.getBloodQiCapacity(),
                x + width / 2,
                y + 20,
                0xFFFFFFFF
        );

        int requiredRefinedBlood = CultivationRules.breakthroughRefinedBloodCost(state.getRealm());
        drawBar(
                graphics,
                x + 3,
                y + 33,
                width - 6,
                8,
                state.getRefinedBlood() / (float) Math.max(1, requiredRefinedBlood),
                EXPERIENCE_BAR_TRACK_COLOR,
                EXPERIENCE_BAR_FILL_COLOR
        );

        int corruption = state.getCorruption();
        if (BloodMoonClientState.isActive() || corruption >= 500) {
            int alpha = Math.min(90, 28 + corruption / 18);
            int edgeColor = (alpha << 24) | 0x5E0710;
            int screenWidth = minecraft.getWindow().getGuiScaledWidth();
            int screenHeight = minecraft.getWindow().getGuiScaledHeight();
            graphics.fill(0, 0, screenWidth, 10, edgeColor);
            graphics.fill(0, screenHeight - 10, screenWidth, screenHeight, edgeColor);
            graphics.fill(0, 0, 10, screenHeight, edgeColor);
            graphics.fill(screenWidth - 10, 0, screenWidth, screenHeight, edgeColor);
        }
    }

    private static int heartbeatTicks;

    private static void drawBar(
            GuiGraphics graphics,
            int x,
            int y,
            int width,
            int height,
            float fill,
            int trackColor,
            int fillColor
    ) {
        graphics.fill(x, y, x + width, y + height, SLOT_DARK_COLOR);
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, trackColor);
        int filled = Math.round((width - 2) * Math.max(0.0F, Math.min(1.0F, fill)));
        graphics.fill(x + 1, y + 1, x + 1 + filled, y + height - 1, fillColor);
        graphics.fill(x + 1, y + 1, x + width - 1, y + 2, 0x55FFFFFF);
    }

    private static void updateAmbience(Minecraft minecraft) {
        if (minecraft.player == null) {
            BloodMoonClientState.setActive(false);
            heartbeatTicks = 0;
            return;
        }
        CultivationState state = CultivationClientCache.get();
        boolean bloodMoon = BloodMoonClientState.isActive();
        boolean highCorruption = state != null && state.getCorruption() >= 300;
        if (!bloodMoon && !highCorruption) {
            heartbeatTicks = 0;
            return;
        }
        if (--heartbeatTicks > 0) {
            return;
        }

        int corruption = state == null ? 0 : state.getCorruption();
        heartbeatTicks = Math.max(24, 80 - corruption / 12);
        minecraft.player.playSound(
                SoundEvents.WARDEN_HEARTBEAT,
                bloodMoon ? 0.45F : 0.3F,
                bloodMoon ? 0.72F : 0.9F
        );
        if (minecraft.player.tickCount % 20 == 0) {
            minecraft.player.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.CRIMSON_SPORE,
                    minecraft.player.getX() + (minecraft.player.getRandom().nextDouble() - 0.5) * 4.0,
                    minecraft.player.getY() + minecraft.player.getRandom().nextDouble() * 2.0,
                    minecraft.player.getZ() + (minecraft.player.getRandom().nextDouble() - 0.5) * 4.0,
                    0.0,
                    0.0,
                    0.0
            );
        }
    }
}
