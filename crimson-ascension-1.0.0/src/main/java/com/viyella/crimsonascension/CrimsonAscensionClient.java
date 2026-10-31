package com.viyella.crimsonascension;

import com.viyella.crimsonascension.client.CultivationClientCache;
import com.viyella.crimsonascension.client.BloodVaultClient;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = CrimsonAscension.MOD_ID, dist = Dist.CLIENT)
public final class CrimsonAscensionClient {
    public CrimsonAscensionClient(IEventBus modEventBus) {
        modEventBus.addListener(BloodVaultClient::registerKeyMappings);
        modEventBus.addListener(BloodVaultClient::registerMenuScreens);
        modEventBus.addListener(BloodVaultClient::registerHud);
        NeoForge.EVENT_BUS.addListener(CultivationClientCache::onClientTick);
        NeoForge.EVENT_BUS.addListener(BloodVaultClient::onClientTick);
    }
}
