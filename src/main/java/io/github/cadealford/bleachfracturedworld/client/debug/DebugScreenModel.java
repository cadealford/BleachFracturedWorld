package io.github.cadealford.bleachfracturedworld.client.debug;

import io.github.cadealford.bleachfracturedworld.network.DebugActionResultPayload;
import io.github.cadealford.bleachfracturedworld.network.ProfileSnapshotPayload;
import java.util.Locale;
import java.util.Optional;

public record DebugScreenModel(
        boolean connected,
        boolean hasSnapshot,
        boolean mutationsEnabled,
        String path,
        String energy,
        String mastery,
        String revision,
        String lastResult) {
    public static DebugScreenModel from(
            boolean connected,
            Optional<ProfileSnapshotPayload> snapshot,
            Optional<DebugActionResultPayload> latestResult) {
        if (snapshot.isEmpty()) {
            return new DebugScreenModel(
                    connected,
                    false,
                    false,
                    "Unavailable",
                    "Unavailable",
                    "Unavailable",
                    "Unavailable",
                    latestResult.map(result -> display(result.code().name())).orElse("None"));
        }

        ProfileSnapshotPayload value = snapshot.orElseThrow();
        return new DebugScreenModel(
                connected,
                true,
                connected,
                display(value.path().name()),
                value.currentEnergy() + " / " + value.maxEnergy(),
                Integer.toString(value.mastery()),
                Long.toString(value.revision()),
                latestResult.map(result -> display(result.code().name())).orElse("None"));
    }

    private static String display(String enumName) {
        String words = enumName.toLowerCase(Locale.ROOT).replace('_', ' ');
        return Character.toUpperCase(words.charAt(0)) + words.substring(1);
    }
}
