package io.github.cadealford.bleachfracturedworld.player;

public record ProfileMutationResult(Status status, PlayerProfile profile) {
    public enum Status {
        ACCEPTED,
        INVALID_PATH,
        ALREADY_CHOSEN,
        REVISION_EXHAUSTED
    }
}
