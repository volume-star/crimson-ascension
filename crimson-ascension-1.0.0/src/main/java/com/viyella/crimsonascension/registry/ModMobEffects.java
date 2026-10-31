package com.viyella.crimsonascension.registry;

import com.viyella.crimsonascension.CrimsonAscension;
import com.viyella.crimsonascension.effect.BleedingEffect;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMobEffects {
    public static final DeferredRegister<MobEffect> REGISTER =
            DeferredRegister.create(Registries.MOB_EFFECT, CrimsonAscension.MOD_ID);

    public static final DeferredHolder<MobEffect, BleedingEffect> BLEEDING =
            REGISTER.register("bleeding", BleedingEffect::new);

    private ModMobEffects() {
    }
}
