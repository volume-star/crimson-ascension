package com.viyella.crimsonascension.client;

import com.viyella.crimsonascension.menu.BloodVaultMenu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public final class BloodVaultScreen extends AbstractContainerScreen<BloodVaultMenu> {
    private static final int PANEL_COLOR = 0xF20E090B;
    private static final int INNER_COLOR = 0xFF171013;
    private static final int BORDER_COLOR = 0xFF651C25;
    private static final int BRIGHT_BORDER_COLOR = 0xFFB42032;
    private static final int SLOT_COLOR = 0xFF211519;
    private static final int TEXT_COLOR = 0xFFD8C9CA;
    private static final int BLOOD_TEXT_COLOR = 0xFFCA4455;

    public BloodVaultScreen(BloodVaultMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 242;
        inventoryLabelY = 146;
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

        graphics.fill(x + 87, y + 44, x + 89, y + 62, BORDER_COLOR);
        graphics.fill(x + 56, y + 62, x + 89, y + 64, BORDER_COLOR);
        graphics.fill(x + 56, y + 64, x + 58, y + 70, BORDER_COLOR);
        graphics.fill(x + 87, y + 64, x + 89, y + 70, BORDER_COLOR);

        for (Slot slot : menu.slots) {
            int slotX = leftPos + slot.x - 1;
            int slotY = topPos + slot.y - 1;
            int border = slot.index == 0 ? BRIGHT_BORDER_COLOR : BORDER_COLOR;
            graphics.fill(slotX, slotY, slotX + 20, slotY + 20, border);
            graphics.fill(slotX + 1, slotY + 1, slotX + 19, slotY + 19, SLOT_COLOR);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawCenteredString(font, title, imageWidth / 2, 8, BLOOD_TEXT_COLOR);
        graphics.drawCenteredString(
                font,
                Component.translatable("screen.crimson_ascension.blood_seed"),
                imageWidth / 2,
                46,
                TEXT_COLOR
        );
        graphics.drawCenteredString(
                font,
                Component.translatable("screen.crimson_ascension.techniques"),
                imageWidth / 2,
                89,
                TEXT_COLOR
        );
        graphics.drawCenteredString(
                font,
                Component.translatable("screen.crimson_ascension.offerings"),
                imageWidth / 2,
                133,
                TEXT_COLOR
        );
        graphics.drawString(
                font,
                Component.translatable("container.inventory"),
                8,
                inventoryLabelY,
                TEXT_COLOR,
                false
        );

    }
}
