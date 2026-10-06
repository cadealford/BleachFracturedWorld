package io.github.cadealford.bleachfracturedworld.debug;

import io.github.cadealford.bleachfracturedworld.player.PlayerProfile;

public record DebugActionResult(Code code, PlayerProfile profile) {
    public enum Code {
        ACCEPTED,
        DEBUG_DISABLED,
        INVALID_ACTION,
        INVALID_PATH,
        ALREADY_CHOSEN,
        REVISION_EXHAUSTED
    }
}
