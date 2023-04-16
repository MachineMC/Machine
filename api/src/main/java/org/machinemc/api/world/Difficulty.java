package org.machinemc.api.world;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

/**
 * Difficulty of the world.
 */
public enum Difficulty {

    PEACEFUL,
    EASY,
    NORMAL,
    HARD;

    public static final Difficulty DEFAULT_DIFFICULTY = EASY;

    /**
     * @return numeric id of the difficulty used by Minecraft protocol.
     */
    public @Range(from = 0, to = 3) int getId() {
        return ordinal();
    }

    /**
     * Returns difficulty from its numeric id.
     * @param id id of the difficulty
     * @return difficulty for given id
     */
    public static Difficulty fromID(final @Range(from = 0, to = 3) int id) {
        Preconditions.checkArgument(id < values().length, "Unsupported difficulty type");
        return values()[id];
    }

    /**
     * Returns difficulty of given name.
     * @param name name of the difficulty
     * @return difficulty with given name
     */
    public static @Nullable Difficulty getByName(final String name) {
        for (Difficulty value : values()) {
            if (value.name().equalsIgnoreCase(name)) return value;
        }
        return null;
    }

}
