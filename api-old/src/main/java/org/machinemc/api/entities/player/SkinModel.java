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

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents the model of player's skin.
 */
public enum SkinModel {
    CLASSIC,
    SLIM;

    /**
     * Returns skin model of given name.
     * @param name name of the skin model
     * @return skin model with given name
     */
    public static @Nullable SkinModel getByName(final String name) {
        Objects.requireNonNull(name, "Name of the skin model can not be null");
        for (final SkinModel value : values()) {
            if (value.name().equalsIgnoreCase(name)) return value;
        }
        return null;
    }

}
