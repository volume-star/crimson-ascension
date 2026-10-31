package com.viyella.crimsonascension.network;

import com.viyella.crimsonascension.CrimsonAscension;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record AltarActionPayload(BlockPos altarPos, int refinedAmount, boolean breakthrough)
        implements CustomPacketPayload {
    public static final Type<AltarActionPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(CrimsonAscension.MOD_ID, "altar_action")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, AltarActionPayload> STREAM_CODEC = StreamCodec.of(
            (buffer, payload) -> {
                buffer.writeBlockPos(payload.altarPos());
                buffer.writeVarInt(payload.refinedAmount());
                buffer.writeBoolean(payload.breakthrough());
            },
            buffer -> new AltarActionPayload(
                    buffer.readBlockPos(),
                    buffer.readVarInt(),
                    buffer.readBoolean()
            )
    );

    @Override
    public Type<AltarActionPayload> type() {
        return TYPE;
    }
}
