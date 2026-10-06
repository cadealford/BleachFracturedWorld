package io.github.cadealford.bleachfracturedworld.player;

import net.minecraft.server.level.ServerPlayer;

public final class ProfileService {
    private final ProfileSnapshotSender snapshotSender;

    public ProfileService(ProfileSnapshotSender snapshotSender) {
        this.snapshotSender = snapshotSender;
    }

    public ProfileMutationResult choosePath(ServerPlayer player, SpiritualPath requestedPath) {
        PlayerProfile current = player.getData(PlayerProfiles.PROFILE);

        if (requestedPath == null || !requestedPath.isChosen()) {
            return new ProfileMutationResult(ProfileMutationResult.Status.INVALID_PATH, current);
        }
        if (current.path().isChosen()) {
            return new ProfileMutationResult(ProfileMutationResult.Status.ALREADY_CHOSEN, current);
        }
        if (current.revision() == Long.MAX_VALUE) {
            return new ProfileMutationResult(ProfileMutationResult.Status.REVISION_EXHAUSTED, current);
        }

        PlayerProfile replacement = current.withPath(requestedPath, current.revision() + 1L);
        player.setData(PlayerProfiles.PROFILE, replacement);
        snapshotSender.send(player);
        return new ProfileMutationResult(ProfileMutationResult.Status.ACCEPTED, replacement);
    }
}
