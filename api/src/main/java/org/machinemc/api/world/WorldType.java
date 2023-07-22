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

import java.util.Optional;
import java.util.Objects;

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
    public static Optional<WorldType> getByName(final String name) {
        Objects.requireNonNull(name, "Name of the world type can not be null");
        for (final WorldType value : values()) {
            if (value.name().equalsIgnoreCase(name))
                return Optional.of(value);
        }
        return Optional.empty();
    }

}
