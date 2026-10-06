package io.github.cadealford.bleachfracturedworld.network;

import io.github.cadealford.bleachfracturedworld.BleachFracturedWorld;
import io.github.cadealford.bleachfracturedworld.debug.DebugActionResult;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record DebugActionResultPayload(
        DebugActionResult.Code code,
        ProfileSnapshotPayload snapshot) implements CustomPacketPayload {
    public static final Type<DebugActionResultPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(BleachFracturedWorld.MODID, "debug_action_result"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DebugActionResultPayload> STREAM_CODEC =
            StreamCodec.ofMember(DebugActionResultPayload::encode, DebugActionResultPayload::decode);

    public static DebugActionResultPayload from(DebugActionResult result) {
        return new DebugActionResultPayload(result.code(), ProfileSnapshotPayload.from(result.profile()));
    }

    private void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeEnum(code);
        ProfileSnapshotPayload.STREAM_CODEC.encode(buffer, snapshot);
    }

    private static DebugActionResultPayload decode(RegistryFriendlyByteBuf buffer) {
        return new DebugActionResultPayload(
                buffer.readEnum(DebugActionResult.Code.class),
                ProfileSnapshotPayload.STREAM_CODEC.decode(buffer));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
