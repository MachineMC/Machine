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
package org.machinemc.api.chat;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Objects;

/**
 * Represents players chat mode option.
 */
public enum ChatMode {

    ENABLED,
    COMMANDS_ONLY,
    HIDDEN;

    /**
     * @return numeric id of the chat mode used by Minecraft protocol.
     */
    public @Range(from = 0, to = 2) int getID() {
        return ordinal();
    }

    /**
     * Returns chat mode from its numeric id.
     * @param id id of the chat mode
     * @return chat mode for given id
     */
    public static ChatMode fromID(final @Range(from = 0, to = 2) int id) {
        Preconditions.checkArgument(id < values().length, "Unsupported ChatMode type");
        return values()[id];
    }

    /**
     * Returns chat mode of given name.
     * @param name name of the chat mode
     * @return chat mode with given name
     */
    public static @Nullable ChatMode getByName(final String name) {
        Objects.requireNonNull(name, "Name of the chat mode can not be null");
        for (final ChatMode value : values()) {
            if (value.name().equalsIgnoreCase(name))
                return value;
        }
        return null;
    }

}
