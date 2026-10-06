package io.github.cadealford.bleachfracturedworld.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record PlayerProfile(
        SpiritualPath path,
        int currentEnergy,
        int maxEnergy,
        int mastery,
        long revision) {
    public static final int DEFAULT_MAX_ENERGY = 100;

    public static final Codec<PlayerProfile> CODEC = Serialized.CODEC.comapFlatMap(
            Serialized::toProfile,
            PlayerProfile::serialized);

    public PlayerProfile {
        if (path == null) {
            throw new IllegalArgumentException("Spiritual path cannot be null");
        }
        if (maxEnergy <= 0) {
            throw new IllegalArgumentException("Maximum energy must be positive");
        }
        if (currentEnergy < 0 || currentEnergy > maxEnergy) {
            throw new IllegalArgumentException("Current energy must be between zero and maximum energy");
        }
        if (mastery < 0) {
            throw new IllegalArgumentException("Mastery cannot be negative");
        }
        if (revision < 0) {
            throw new IllegalArgumentException("Revision cannot be negative");
        }
    }

    public static PlayerProfile unchosen() {
        return new PlayerProfile(SpiritualPath.UNCHOSEN, DEFAULT_MAX_ENERGY, DEFAULT_MAX_ENERGY, 0, 0L);
    }

    public PlayerProfile withPath(SpiritualPath newPath, long newRevision) {
        return new PlayerProfile(newPath, currentEnergy, maxEnergy, mastery, newRevision);
    }

    public PlayerProfile withCurrentEnergy(int newCurrentEnergy, long newRevision) {
        return new PlayerProfile(path, newCurrentEnergy, maxEnergy, mastery, newRevision);
    }

    private Serialized serialized() {
        return new Serialized(path, currentEnergy, maxEnergy, mastery, revision);
    }

    private record Serialized(
            SpiritualPath path,
            int currentEnergy,
            int maxEnergy,
            int mastery,
            long revision) {
        private static final Codec<Serialized> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                SpiritualPath.CODEC.fieldOf("path").forGetter(Serialized::path),
                Codec.INT.fieldOf("current_energy").forGetter(Serialized::currentEnergy),
                Codec.INT.fieldOf("max_energy").forGetter(Serialized::maxEnergy),
                Codec.INT.fieldOf("mastery").forGetter(Serialized::mastery),
                Codec.LONG.fieldOf("revision").forGetter(Serialized::revision))
                .apply(instance, Serialized::new));

        private DataResult<PlayerProfile> toProfile() {
            try {
                return DataResult.success(new PlayerProfile(path, currentEnergy, maxEnergy, mastery, revision));
            } catch (IllegalArgumentException exception) {
                return DataResult.error(exception::getMessage);
            }
        }
    }
}
