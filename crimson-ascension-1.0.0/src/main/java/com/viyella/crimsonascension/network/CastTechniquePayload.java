package com.viyella.crimsonascension.network;

import com.viyella.crimsonascension.CrimsonAscension;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CastTechniquePayload(boolean secondary) implements CustomPacketPayload {
    public static final Type<CastTechniquePayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(CrimsonAscension.MOD_ID, "cast_technique")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, CastTechniquePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL,
                    CastTechniquePayload::secondary,
                    CastTechniquePayload::new
            );

    @Override
    public Type<CastTechniquePayload> type() {
        return TYPE;
    }
}
