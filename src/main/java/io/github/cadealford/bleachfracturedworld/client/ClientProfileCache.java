package io.github.cadealford.bleachfracturedworld.client;

import io.github.cadealford.bleachfracturedworld.network.ProfileSnapshotPayload;
import java.util.Optional;

public final class ClientProfileCache {
    private static ProfileSnapshotPayload snapshot;

    private ClientProfileCache() {
    }

    public static synchronized boolean accept(ProfileSnapshotPayload incoming) {
        if (snapshot != null && incoming.revision() < snapshot.revision()) {
            return false;
        }

        snapshot = incoming;
        return true;
    }

    public static synchronized Optional<ProfileSnapshotPayload> snapshot() {
        return Optional.ofNullable(snapshot);
    }

    public static synchronized void clear() {
        snapshot = null;
    }
}
