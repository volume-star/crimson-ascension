package com.viyella.crimsonascension.network;

import com.viyella.crimsonascension.CrimsonAscension;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SelectPathPayload(String path) implements CustomPacketPayload {
    public static final Type<SelectPathPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(CrimsonAscension.MOD_ID, "select_path")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, SelectPathPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    SelectPathPayload::path,
                    SelectPathPayload::new
            );

    @Override
    public Type<SelectPathPayload> type() {
        return TYPE;
    }
}
