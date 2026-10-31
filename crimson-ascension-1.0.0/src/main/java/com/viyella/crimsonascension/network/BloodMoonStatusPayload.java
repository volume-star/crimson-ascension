package com.viyella.crimsonascension.network;

import com.viyella.crimsonascension.CrimsonAscension;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record BloodMoonStatusPayload(boolean active) implements CustomPacketPayload {
    public static final Type<BloodMoonStatusPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(CrimsonAscension.MOD_ID, "blood_moon_status")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, BloodMoonStatusPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL,
                    BloodMoonStatusPayload::active,
                    BloodMoonStatusPayload::new
            );

    @Override
    public Type<BloodMoonStatusPayload> type() {
        return TYPE;
    }
}
