package com.viyella.crimsonascension.network;

import com.viyella.crimsonascension.CrimsonAscension;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record OpenBloodVaultPayload() implements CustomPacketPayload {
    public static final OpenBloodVaultPayload INSTANCE = new OpenBloodVaultPayload();
    public static final Type<OpenBloodVaultPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(CrimsonAscension.MOD_ID, "open_blood_vault")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenBloodVaultPayload> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public Type<OpenBloodVaultPayload> type() {
        return TYPE;
    }
}
