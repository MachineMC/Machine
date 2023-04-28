/*
 * This file is part of Machine.
 *
 * Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Machine.
 * If not, see https://www.gnu.org/licenses/.
 */
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
        for (final Difficulty value : values()) {
            if (value.name().equalsIgnoreCase(name)) return value;
        }
        return null;
    }

}
