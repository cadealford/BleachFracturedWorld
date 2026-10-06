package io.github.cadealford.bleachfracturedworld.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Locale;

public enum SpiritualPath {
    UNCHOSEN,
    SHINIGAMI,
    QUINCY;

    public static final Codec<SpiritualPath> CODEC = Codec.STRING.comapFlatMap(
            SpiritualPath::decode,
            SpiritualPath::serializedName);

    public String serializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public boolean isChosen() {
        return this != UNCHOSEN;
    }

    private static DataResult<SpiritualPath> decode(String value) {
        for (SpiritualPath path : values()) {
            if (path.serializedName().equals(value)) {
                return DataResult.success(path);
            }
        }

        return DataResult.error(() -> "Unknown spiritual path: " + value);
    }
}
