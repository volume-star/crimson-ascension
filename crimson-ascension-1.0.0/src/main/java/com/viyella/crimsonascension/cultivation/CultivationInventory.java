package com.viyella.crimsonascension.cultivation;

import java.util.List;

import com.mojang.serialization.Codec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public final class CultivationInventory extends SimpleContainer {
    public static final int SIZE = 7;

    public static final Codec<CultivationInventory> CODEC = ItemStack.OPTIONAL_CODEC.listOf()
            .xmap(CultivationInventory::new, inventory -> List.copyOf(inventory.getItems()));

    public static final StreamCodec<RegistryFriendlyByteBuf, CultivationInventory> STREAM_CODEC =
            ItemStack.OPTIONAL_LIST_STREAM_CODEC.map(
                    CultivationInventory::new,
                    inventory -> List.copyOf(inventory.getItems())
            );

    public CultivationInventory() {
        super(SIZE);
    }

    private CultivationInventory(List<ItemStack> items) {
        super(SIZE);
        for (int slot = 0; slot < SIZE; slot++) {
            setItem(slot, slot < items.size() ? items.get(slot).copy() : ItemStack.EMPTY);
        }
    }
}
