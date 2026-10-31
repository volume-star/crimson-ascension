package com.viyella.crimsonascension.registry;

import com.mojang.serialization.Codec;
import com.viyella.crimsonascension.CrimsonAscension;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> REGISTER =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, CrimsonAscension.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> STORED_BLOOD =
            REGISTER.register("stored_blood", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
                    .build());

    private ModDataComponents() {
    }
}
