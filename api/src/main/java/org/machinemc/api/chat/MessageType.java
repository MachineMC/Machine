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

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public enum MessageType {

    CHAT,
    SYSTEM;

    /**
     * Returns message type of given name.
     * @param name name of the message type
     * @return message type with given name
     */
    public static @Nullable MessageType getByName(final String name) {
        Objects.requireNonNull(name, "Name of the message type can not be null");
        for (final MessageType value : values()) {
            if (value.name().equalsIgnoreCase(name))
                return value;
        }
        return null;
    }

}
