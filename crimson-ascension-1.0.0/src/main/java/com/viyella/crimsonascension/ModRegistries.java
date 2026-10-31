package com.viyella.crimsonascension;

import com.viyella.crimsonascension.cultivation.ModAttachments;
import com.viyella.crimsonascension.registry.ModBlocks;
import com.viyella.crimsonascension.registry.ModCreativeTabs;
import com.viyella.crimsonascension.registry.ModDataComponents;
import com.viyella.crimsonascension.registry.ModItems;
import com.viyella.crimsonascension.registry.ModMenus;
import com.viyella.crimsonascension.registry.ModMobEffects;

import net.neoforged.bus.api.IEventBus;

public final class ModRegistries {
    private ModRegistries() {
    }

    public static void register(IEventBus modEventBus) {
        ModAttachments.REGISTER.register(modEventBus);
        ModDataComponents.REGISTER.register(modEventBus);
        ModBlocks.REGISTER.register(modEventBus);
        ModItems.REGISTER.register(modEventBus);
        ModMobEffects.REGISTER.register(modEventBus);
        ModMenus.REGISTER.register(modEventBus);
        ModCreativeTabs.REGISTER.register(modEventBus);
    }
}
