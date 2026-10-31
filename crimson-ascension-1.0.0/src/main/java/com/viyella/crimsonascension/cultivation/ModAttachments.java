package com.viyella.crimsonascension.cultivation;

import com.viyella.crimsonascension.CrimsonAscension;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> REGISTER =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CrimsonAscension.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<CultivationState>> CULTIVATION =
            REGISTER.register("cultivation", () -> AttachmentType.builder(CultivationState::new)
                    .serialize(CultivationState.CODEC)
                    .copyOnDeath()
                    .sync((holder, recipient) -> holder == recipient, CultivationState.STREAM_CODEC)
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<CultivationInventory>> CULTIVATION_INVENTORY =
            REGISTER.register("cultivation_inventory", () -> AttachmentType.builder(CultivationInventory::new)
                    .serialize(CultivationInventory.CODEC)
                    .copyOnDeath()
                    .sync((holder, recipient) -> holder == recipient, CultivationInventory.STREAM_CODEC)
                    .build());

    private ModAttachments() {
    }

    public static CultivationState get(Player player) {
        return player.getData(CULTIVATION);
    }

    /**
     * Marks an in-place mutation for synchronization. Call this only after
     * changing state on the logical server.
     */
    public static void sync(Player player) {
        player.syncData(CULTIVATION);
    }

    public static CultivationInventory getInventory(Player player) {
        return player.getData(CULTIVATION_INVENTORY);
    }

    public static void syncInventory(Player player) {
        player.syncData(CULTIVATION_INVENTORY);
    }
}
