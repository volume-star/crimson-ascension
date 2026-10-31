package com.viyella.crimsonascension.menu;

import com.viyella.crimsonascension.cultivation.CultivationInventory;
import com.viyella.crimsonascension.cultivation.ModAttachments;
import com.viyella.crimsonascension.registry.ModMenus;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class BloodVaultMenu extends AbstractContainerMenu {
    public static final int VAULT_SLOTS = CultivationInventory.SIZE;
    public static final int PLAYER_INVENTORY_START = VAULT_SLOTS;
    public static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 36;

    private static final Component TITLE = Component.translatable("screen.crimson_ascension.blood_vault");

    private final Container container;
    private final Player owner;

    public BloodVaultMenu(int containerId, Inventory playerInventory, Container container) {
        super(ModMenus.BLOOD_VAULT.get(), containerId);
        this.container = container;
        this.owner = playerInventory.player;

        addSlot(new Slot(container, 0, 80, 26));
        addSlot(new Slot(container, 1, 44, 72));
        addSlot(new Slot(container, 2, 68, 72));
        addSlot(new Slot(container, 3, 92, 72));
        addSlot(new Slot(container, 4, 44, 116));
        addSlot(new Slot(container, 5, 68, 116));
        addSlot(new Slot(container, 6, 92, 116));

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 158 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInventory, column, 8 + column * 18, 220));
        }
    }

    public BloodVaultMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf data) {
        this(containerId, playerInventory, new SimpleContainer(VAULT_SLOTS));
    }

    public static void openFor(ServerPlayer player) {
        player.openMenu(new SimpleMenuProvider(
                (containerId, inventory, ignored) ->
                        new BloodVaultMenu(containerId, inventory, ModAttachments.getInventory(player)),
                TITLE
        ));
    }

    @Override
    public void slotsChanged(Container changedContainer) {
        super.slotsChanged(changedContainer);
        if (changedContainer == container && owner instanceof ServerPlayer serverPlayer) {
            ModAttachments.syncInventory(serverPlayer);
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (player instanceof ServerPlayer serverPlayer) {
            ModAttachments.syncInventory(serverPlayer);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stackInSlot = slot.getItem();
        ItemStack original = stackInSlot.copy();
        if (index < VAULT_SLOTS) {
            if (!moveItemStackTo(stackInSlot, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stackInSlot, 0, VAULT_SLOTS, false)) {
            return ItemStack.EMPTY;
        }

        if (stackInSlot.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (stackInSlot.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, stackInSlot);
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return player == owner && player.isAlive();
    }
}
