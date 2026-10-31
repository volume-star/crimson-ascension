package com.viyella.crimsonascension.menu;

import com.viyella.crimsonascension.block.BloodCondensationAltarBlock;
import com.viyella.crimsonascension.registry.ModBlocks;
import com.viyella.crimsonascension.registry.ModMenus;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public final class BloodCondensationAltarMenu extends AbstractContainerMenu {
    private static final Component TITLE = Component.translatable("screen.crimson_ascension.blood_altar");

    private final BlockPos altarPos;
    private final Player owner;

    public BloodCondensationAltarMenu(int containerId, Inventory inventory, BlockPos altarPos) {
        super(ModMenus.BLOOD_ALTAR.get(), containerId);
        this.altarPos = altarPos;
        this.owner = inventory.player;
    }

    public BloodCondensationAltarMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf data) {
        this(containerId, inventory, data.readBlockPos());
    }

    public static void openFor(ServerPlayer player, BlockPos altarPos) {
        player.openMenu(
                new SimpleMenuProvider(
                        (containerId, inventory, ignored) ->
                                new BloodCondensationAltarMenu(containerId, inventory, altarPos),
                        TITLE
                ),
                buffer -> buffer.writeBlockPos(altarPos)
        );
    }

    public void exchange(ServerPlayer player, int refinedAmount) {
        if (player.containerMenu == this && canUse(player)) {
            BloodCondensationAltarBlock.exchange(player, refinedAmount, altarPos);
        }
    }

    public BlockPos getAltarPos() {
        return altarPos;
    }

    public void attemptBreakthrough(ServerPlayer player) {
        if (player.containerMenu == this && canUse(player)) {
            BloodCondensationAltarBlock.tryBreakthrough(player, player.serverLevel(), altarPos);
        }
    }

    private boolean canUse(Player player) {
        return player == owner
                && player.isAlive()
                && player.level().getBlockState(altarPos).is(ModBlocks.BLOOD_CONDENSATION_ALTAR.get())
                && player.distanceToSqr(
                        altarPos.getX() + 0.5,
                        altarPos.getY() + 0.5,
                        altarPos.getZ() + 0.5
                ) <= 64.0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return canUse(player);
    }
}
