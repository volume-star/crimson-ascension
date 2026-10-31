package com.viyella.crimsonascension.client;

import com.viyella.crimsonascension.cultivation.CultivationRules;
import com.viyella.crimsonascension.cultivation.CultivationState;
import com.viyella.crimsonascension.menu.BloodCondensationAltarMenu;
import com.viyella.crimsonascension.network.AltarActionPayload;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public final class BloodCondensationAltarScreen extends AbstractContainerScreen<BloodCondensationAltarMenu> {
    private static final int PANEL_COLOR = 0xF20E090B;
    private static final int INNER_COLOR = 0xFF171013;
    private static final int BORDER_COLOR = 0xFF651C25;
    private static final int TEXT_COLOR = 0xFFD8C9CA;
    private static final int BLOOD_TEXT_COLOR = 0xFFCA4455;

    private EditBox amountField;
    private Button refineButton;

    public BloodCondensationAltarScreen(
            BloodCondensationAltarMenu menu,
            Inventory inventory,
            Component title
    ) {
        super(menu, inventory, title);
        imageWidth = 220;
        imageHeight = 176;
    }

    @Override
    protected void init() {
        super.init();
        amountField = new EditBox(font, leftPos + 102, topPos + 61, 44, 18, Component.translatable(
                "screen.crimson_ascension.altar.amount"
        ));
        amountField.setValue("1");
        amountField.setMaxLength(5);
        amountField.setFilter(value -> value.matches("\\d*"));
        addRenderableWidget(amountField);

        addRenderableWidget(Button.builder(
                Component.translatable("screen.crimson_ascension.altar.max"),
                ignored -> {
                    CultivationState state = CultivationClientCache.get();
                    int maximum = state == null ? 0 : state.getBloodQi() / CultivationRules.BLOOD_PER_REFINED;
                    amountField.setValue(Integer.toString(maximum));
                }
        ).bounds(leftPos + 151, topPos + 61, 42, 18).build());

        refineButton = addRenderableWidget(Button.builder(
                Component.translatable("screen.crimson_ascension.altar.refine"),
                ignored -> sendAction(false)
        ).bounds(leftPos + 24, topPos + 94, 82, 20).build());

        addRenderableWidget(Button.builder(
                Component.translatable("screen.crimson_ascension.altar.breakthrough"),
                ignored -> sendAction(true)
        ).bounds(leftPos + 114, topPos + 94, 82, 20).build());
    }

    private void sendAction(boolean breakthrough) {
        int amount = breakthrough ? 0 : parseAmount();
        PacketDistributor.sendToServer(new AltarActionPayload(
                menu.getAltarPos(),
                amount,
                breakthrough
        ));
    }

    private int parseAmount() {
        try {
            return amountField == null || amountField.getValue().isBlank()
                    ? 0
                    : Integer.parseInt(amountField.getValue());
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        graphics.fill(x, y, x + imageWidth, y + imageHeight, PANEL_COLOR);
        graphics.fill(x + 3, y + 3, x + imageWidth - 3, y + imageHeight - 3, INNER_COLOR);
        graphics.fill(x + 5, y + 5, x + imageWidth - 5, y + 6, BORDER_COLOR);
        graphics.fill(x + 5, y + imageHeight - 6, x + imageWidth - 5, y + imageHeight - 5, BORDER_COLOR);
        graphics.fill(x + 5, y + 5, x + 6, y + imageHeight - 5, BORDER_COLOR);
        graphics.fill(x + imageWidth - 6, y + 5, x + imageWidth - 5, y + imageHeight - 5, BORDER_COLOR);
        graphics.fill(x + 18, y + 55, x + 202, y + 56, BORDER_COLOR);
        graphics.fill(x + 18, y + 83, x + 202, y + 84, BORDER_COLOR);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawCenteredString(font, title, imageWidth / 2, 8, BLOOD_TEXT_COLOR);
        graphics.drawString(
                font,
                Component.translatable("screen.crimson_ascension.altar.rate"),
                18,
                31,
                TEXT_COLOR,
                false
        );

        CultivationState state = CultivationClientCache.get();
        int bloodQi = state == null ? 0 : state.getBloodQi();
        int refinedBlood = state == null ? 0 : state.getRefinedBlood();
        int amount = parseAmount();
        int cost = amount * CultivationRules.BLOOD_PER_REFINED;

        graphics.drawString(
                font,
                Component.translatable(
                        "screen.crimson_ascension.altar.current",
                        bloodQi,
                        refinedBlood
                ),
                18,
                45,
                TEXT_COLOR,
                false
        );
        graphics.drawString(
                font,
                Component.translatable("screen.crimson_ascension.altar.amount"),
                18,
                66,
                TEXT_COLOR,
                false
        );
        int realm = state == null ? 0 : state.getRealm();
        int requiredRefined = CultivationRules.breakthroughRefinedBloodCost(realm);
        graphics.drawCenteredString(
                font,
                Component.translatable(
                        "screen.crimson_ascension.altar.breakthrough_cost",
                        requiredRefined
                ),
                imageWidth / 2,
                126,
                TEXT_COLOR
        );
        graphics.drawCenteredString(
                font,
                Component.translatable("screen.crimson_ascension.close_hint"),
                imageWidth / 2,
                151,
                TEXT_COLOR
        );

        if (refineButton != null) {
            refineButton.active = amount > 0
                    && amount <= (state == null
                    ? 0
                    : state.getBloodQiCapacity() / CultivationRules.BLOOD_PER_REFINED)
                    && cost <= bloodQi;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
