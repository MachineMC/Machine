package org.machinemc.api.world;

import org.jetbrains.annotations.Nullable;

/**
 * World type of a server world. Changes some properties
 * on client's side such as void fog.
 */
public enum WorldType {

    NORMAL,
    FLAT;

    /**
     * Returns world type of given name.
     * @param name name of the world type
     * @return world type with given name
     */
    public static @Nullable WorldType getByName(final String name) {
        for (WorldType value : values()) {
            if (value.name().equalsIgnoreCase(name))
                return value;
        }
        return null;
    }

}
