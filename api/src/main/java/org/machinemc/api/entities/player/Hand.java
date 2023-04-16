package org.machinemc.api.entities.player;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

/**
 * Representing player's hands.
 */
public enum Hand {

    LEFT,
    RIGHT;

    /**
     * @return numeric id of the hand used by Minecraft protocol.
     */
    public @Range(from = 0, to = 1) int getId() {
        return ordinal();
    }

    /**
     * Returns hand from its numeric id.
     * @param id id of the hand
     * @return hand for given id
     */
    public static Hand fromID(final @Range(from = 0, to = 1) int id) {
        Preconditions.checkArgument(id < values().length, "Unsupported Hand type");
        return values()[id];
    }


    /**
     * Returns hand of given name.
     * @param name name of the hand
     * @return hand with given name
     */
    public static @Nullable Hand getByName(final String name) {
        for (Hand value : values()) {
            if (value.name().equalsIgnoreCase(name)) return value;
        }
        return null;
    }

}
