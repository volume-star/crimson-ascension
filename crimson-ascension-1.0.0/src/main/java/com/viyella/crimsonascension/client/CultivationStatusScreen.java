package com.viyella.crimsonascension.client;

import com.viyella.crimsonascension.cultivation.CultivationState;
import com.viyella.crimsonascension.cultivation.CultivationPath;
import com.viyella.crimsonascension.network.SelectPathPayload;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public final class CultivationStatusScreen extends Screen {
    private static final int PANEL_COLOR = 0xF20E090B;
    private static final int INNER_COLOR = 0xFF171013;
    private static final int BORDER_COLOR = 0xFF651C25;
    private static final int TEXT_COLOR = 0xFFD8C9CA;
    private static final int BLOOD_TEXT_COLOR = 0xFFCA4455;
    private static final int WIDTH = 280;
    private static final int HEIGHT = 180;

    public CultivationStatusScreen() {
        super(Component.translatable("screen.crimson_ascension.cultivation_status"));
    }

    @Override
    protected void init() {
        super.init();
        int x = (width - WIDTH) / 2;
        int y = (height - HEIGHT) / 2;
        CultivationState state = CultivationClientCache.get();
        boolean canSelect = state == null || state.getPath() == CultivationPath.NONE;
        CultivationPath[] paths = {
                CultivationPath.BLOOD_RIVER,
                CultivationPath.WITHERED_BONE,
                CultivationPath.GHOST_WOMB
        };
        for (int index = 0; index < paths.length; index++) {
            CultivationPath path = paths[index];
            Button button = Button.builder(
                    Component.translatable(path.translationKey()),
                    ignored -> PacketDistributor.sendToServer(new SelectPathPayload(path.id()))
            ).bounds(x + 12 + index * 88, y + 108, 80, 20).build();
            button.active = canSelect || (state != null && state.getPath() == path);
            addRenderableWidget(button);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        int x = (width - WIDTH) / 2;
        int y = (height - HEIGHT) / 2;

        graphics.fill(x, y, x + WIDTH, y + HEIGHT, PANEL_COLOR);
        graphics.fill(x + 3, y + 3, x + WIDTH - 3, y + HEIGHT - 3, INNER_COLOR);
        graphics.fill(x + 5, y + 5, x + WIDTH - 5, y + 6, BORDER_COLOR);
        graphics.fill(x + 5, y + HEIGHT - 6, x + WIDTH - 5, y + HEIGHT - 5, BORDER_COLOR);
        graphics.fill(x + 5, y + 5, x + 6, y + HEIGHT - 5, BORDER_COLOR);
        graphics.fill(x + WIDTH - 6, y + 5, x + WIDTH - 5, y + HEIGHT - 5, BORDER_COLOR);

        graphics.drawCenteredString(font, title, x + WIDTH / 2, y + 17, BLOOD_TEXT_COLOR);
        CultivationState state = CultivationClientCache.get();
        int realm = state == null ? 0 : state.getRealm();
        CultivationPath path = state == null ? CultivationPath.NONE : state.getPath();
        graphics.drawCenteredString(
                font,
                Component.translatable(
                        "screen.crimson_ascension.selected_path",
                        Component.translatable(path.translationKey())
                ),
                x + WIDTH / 2,
                y + 53,
                TEXT_COLOR
        );
        graphics.drawCenteredString(
                font,
                Component.translatable("crimson_ascension.realm." + realm),
                x + WIDTH / 2,
                y + 78,
                BLOOD_TEXT_COLOR
        );
        graphics.drawCenteredString(
                font,
                Component.translatable("screen.crimson_ascension.values_hidden"),
                x + WIDTH / 2,
                y + 94,
                TEXT_COLOR
        );
        graphics.drawCenteredString(
                font,
                Component.translatable("screen.crimson_ascension.technique_hint"),
                x + WIDTH / 2,
                y + 140,
                TEXT_COLOR
        );
        graphics.drawCenteredString(
                font,
                Component.translatable("screen.crimson_ascension.close_hint"),
                x + WIDTH / 2,
                y + 158,
                TEXT_COLOR
        );

        for (net.minecraft.client.gui.components.Renderable renderable : renderables) {
            renderable.render(graphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
