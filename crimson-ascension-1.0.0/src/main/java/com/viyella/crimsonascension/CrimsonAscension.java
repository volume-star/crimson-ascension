package com.viyella.crimsonascension;

import com.mojang.logging.LogUtils;
import com.viyella.crimsonascension.command.CrimsonCommands;
import com.viyella.crimsonascension.event.BloodlettingEvents;
import com.viyella.crimsonascension.event.BloodMoonManager;
import com.viyella.crimsonascension.event.BloodBossManager;
import com.viyella.crimsonascension.event.PathPassiveEvents;
import com.viyella.crimsonascension.entity.BloodPuppetFactory;
import com.viyella.crimsonascension.entity.MonsterRealmService;
import com.viyella.crimsonascension.network.ModNetwork;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

import org.slf4j.Logger;

@Mod(CrimsonAscension.MOD_ID)
public final class CrimsonAscension {
    public static final String MOD_ID = "crimson_ascension";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CrimsonAscension(IEventBus modEventBus, ModContainer modContainer) {
        ModRegistries.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, CrimsonConfig.SPEC);
        modEventBus.addListener(ModNetwork::register);
        NeoForge.EVENT_BUS.addListener(CrimsonCommands::register);
        NeoForge.EVENT_BUS.addListener(BloodlettingEvents::onLivingDeath);
        NeoForge.EVENT_BUS.addListener(BloodMoonManager::onLevelTick);
        NeoForge.EVENT_BUS.addListener(BloodBossManager::onLevelTick);
        NeoForge.EVENT_BUS.addListener(BloodBossManager::onLivingDeath);
        NeoForge.EVENT_BUS.addListener(BloodPuppetFactory::onLevelTick);
        NeoForge.EVENT_BUS.addListener(MonsterRealmService::onEntityJoin);
        NeoForge.EVENT_BUS.addListener(PathPassiveEvents::onLivingDamage);
        NeoForge.EVENT_BUS.addListener(PathPassiveEvents::onPlayerTick);
    }
}
