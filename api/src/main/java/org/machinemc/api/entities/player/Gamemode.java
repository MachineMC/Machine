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
package org.machinemc.api.entities.player;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

/**
 * Representing player's gamemode.
 */
public enum Gamemode {

    SURVIVAL,
    CREATIVE,
    ADVENTURE,
    SPECTATOR;

    /**
     * @return numeric id of the gamemode used by Minecraft protocol.
     */
    public @Range(from = 0, to = 3) int getID() {
        return ordinal();
    }

    /**
     * Returns gamemode from its numeric id.
     * @param id id of the gamemode
     * @return gamemode for given id
     */
    public static Gamemode fromID(final @Range(from = 0, to = 3) int id) {
        Preconditions.checkArgument(id < values().length, "Unsupported Gamemode type");
        return values()[id];
    }

    /**
     * Returns gamemode from its numeric id, supporting -1
     * as null value.
     * @param id id of the gamemode
     * @return gamemode for given id
     */
    public static @Nullable Gamemode nullableFromID(final @Range(from = -1, to = 3) int id) {
        if (id == -1)
            return null;
        Preconditions.checkArgument(id < values().length, "Unsupported Gamemode type");
        return values()[id];
    }

    /**
     * Returns gamemode of given name.
     * @param name name of the gamemode
     * @return gamemode with given name
     */
    public static @Nullable Gamemode getByName(final String name) {
        for (final Gamemode value : values()) {
            if (value.name().equalsIgnoreCase(name)) return value;
        }
        return null;
    }

}
