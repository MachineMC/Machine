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

public enum FilterType {

    PASS_THROUGH,
    FULLY_FILTERED,
    PARTIALLY_FILTERED;

    /**
     * @return numeric id of the filter type used by Minecraft protocol.
     */
    public @Range(from = 0, to = 2) int getID() {
        return ordinal();
    }

    /**
     * Returns filter type from its numeric id.
     * @param id id of the filter type
     * @return filter type for given id
     */
    public static FilterType fromID(final @Range(from = 0, to = 2) int id) {
        Preconditions.checkArgument(id < values().length, "Unsupported filter type");
        return values()[id];
    }


    /**
     * Returns filter type of given name.
     * @param name name of the filter type
     * @return filter type with given name
     */
    public static @Nullable FilterType getByName(final String name) {
        Objects.requireNonNull(name, "Name of the filter type can not be null");
        for (final FilterType value : values()) {
            if (value.name().equalsIgnoreCase(name))
                return value;
        }
        return null;
    }

}
