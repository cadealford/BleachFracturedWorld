package io.github.cadealford.bleachfracturedworld.network;

import io.github.cadealford.bleachfracturedworld.BleachFracturedWorld;
import io.github.cadealford.bleachfracturedworld.debug.DebugAction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record DebugActionRequestPayload(int actionId) implements CustomPacketPayload {
    public static final Type<DebugActionRequestPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(BleachFracturedWorld.MODID, "debug_action_request"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DebugActionRequestPayload> STREAM_CODEC =
            StreamCodec.ofMember(DebugActionRequestPayload::encode, DebugActionRequestPayload::decode);

    public DebugActionRequestPayload(DebugAction action) {
        this(action.id());
    }

    private void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(actionId);
    }

    private static DebugActionRequestPayload decode(RegistryFriendlyByteBuf buffer) {
        return new DebugActionRequestPayload(buffer.readVarInt());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
