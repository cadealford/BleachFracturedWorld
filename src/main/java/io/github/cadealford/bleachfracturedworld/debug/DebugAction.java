package io.github.cadealford.bleachfracturedworld.debug;

import java.util.Optional;

public enum DebugAction {
    CHOOSE_SHINIGAMI(0),
    CHOOSE_QUINCY(1),
    RESET_PROFILE(2),
    ENERGY_ZERO(3),
    ENERGY_HALF(4),
    ENERGY_FULL(5),
    REFRESH(6);

    private final int id;

    DebugAction(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    public static Optional<DebugAction> fromId(int id) {
        for (DebugAction action : values()) {
            if (action.id == id) {
                return Optional.of(action);
            }
        }
        return Optional.empty();
    }
}
