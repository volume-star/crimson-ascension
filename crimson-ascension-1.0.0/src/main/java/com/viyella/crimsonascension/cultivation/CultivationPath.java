package com.viyella.crimsonascension.cultivation;

import java.util.Arrays;
import java.util.Optional;

public enum CultivationPath {
    NONE("none", "crimson_ascension.path.none"),
    BLOOD_RIVER("blood_river", "crimson_ascension.path.blood_river"),
    WITHERED_BONE("withered_bone", "crimson_ascension.path.withered_bone"),
    GHOST_WOMB("ghost_womb", "crimson_ascension.path.ghost_womb");

    private final String id;
    private final String translationKey;

    CultivationPath(String id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    public String id() {
        return id;
    }

    public String translationKey() {
        return translationKey;
    }

    public static CultivationPath fromId(String id) {
        return Arrays.stream(values())
                .filter(path -> path.id.equals(id))
                .findFirst()
                .orElse(NONE);
    }

    public static Optional<CultivationPath> selectableById(String id) {
        return Arrays.stream(values())
                .filter(path -> path != NONE && path.id.equals(id))
                .findFirst();
    }
}
