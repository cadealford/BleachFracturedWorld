package io.github.cadealford.bleachfracturedworld.client.debug;

import io.github.cadealford.bleachfracturedworld.network.DebugActionResultPayload;
import java.util.Optional;

public final class ClientDebugState {
    private static DebugActionResultPayload latest;

    private ClientDebugState() {
    }

    public static synchronized void accept(DebugActionResultPayload result) {
        latest = result;
    }

    public static synchronized Optional<DebugActionResultPayload> latest() {
        return Optional.ofNullable(latest);
    }

    public static synchronized void clear() {
        latest = null;
    }
}
