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
package org.machinemc.api.entities;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.machinemc.api.utils.NamespacedKey;

import java.util.Optional;
import java.util.Objects;

/**
 * Represents entity type (model) of the entity.
 */
@Getter
public enum EntityType {

    PLAYER(116, 0.6, 1.8, "player");

    private final int id;
    private final double width;
    private final double height;
    private final NamespacedKey identifier;
    private final String typeName;

    EntityType(final int id, final double width, final double height, final String name) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.identifier = NamespacedKey.minecraft(name);
        this.typeName = name.replace('_', ' ');
    }

    /**
     * Returns entity type from its id.
     * @param id id of the entity type
     * @return entity type for given id
     */
    public static EntityType fromID(final int id) {
        Preconditions.checkArgument(id < values().length, "Unsupported Entity type");
        return values()[id];
    }

    /**
     * Returns entity type of given name.
     * @param name name of the entity type
     * @return entity type with given name
     */
    public static Optional<EntityType> getByName(final String name) {
        Objects.requireNonNull(name, "Name of the entity type can not be null");
        for (final EntityType value : values()) {
            if (value.name().equalsIgnoreCase(name)
                    || value.identifier.getKey().equalsIgnoreCase(name)
                    || value.typeName.equalsIgnoreCase(name))
                return Optional.of(value);
        }
        return Optional.empty();
    }

}
