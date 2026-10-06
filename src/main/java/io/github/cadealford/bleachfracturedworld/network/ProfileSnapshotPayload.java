package io.github.cadealford.bleachfracturedworld.network;

import io.github.cadealford.bleachfracturedworld.BleachFracturedWorld;
import io.github.cadealford.bleachfracturedworld.player.PlayerProfile;
import io.github.cadealford.bleachfracturedworld.player.SpiritualPath;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ProfileSnapshotPayload(
        SpiritualPath path,
        int currentEnergy,
        int maxEnergy,
        int mastery,
        long revision) implements CustomPacketPayload {
    public static final Type<ProfileSnapshotPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(BleachFracturedWorld.MODID, "profile_snapshot"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ProfileSnapshotPayload> STREAM_CODEC =
            StreamCodec.ofMember(ProfileSnapshotPayload::encode, ProfileSnapshotPayload::decode);

    public ProfileSnapshotPayload {
        new PlayerProfile(path, currentEnergy, maxEnergy, mastery, revision);
    }

    public static ProfileSnapshotPayload from(PlayerProfile profile) {
        return new ProfileSnapshotPayload(
                profile.path(),
                profile.currentEnergy(),
                profile.maxEnergy(),
                profile.mastery(),
                profile.revision());
    }

    private void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeEnum(path);
        buffer.writeVarInt(currentEnergy);
        buffer.writeVarInt(maxEnergy);
        buffer.writeVarInt(mastery);
        buffer.writeVarLong(revision);
    }

    private static ProfileSnapshotPayload decode(RegistryFriendlyByteBuf buffer) {
        return new ProfileSnapshotPayload(
                buffer.readEnum(SpiritualPath.class),
                buffer.readVarInt(),
                buffer.readVarInt(),
                buffer.readVarInt(),
                buffer.readVarLong());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
