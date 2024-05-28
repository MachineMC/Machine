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
package org.machinemc.api.serializers;

import org.jetbrains.annotations.Nullable;
import org.machinemc.api.Server;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.EntityPosition;
import org.machinemc.api.world.Location;
import org.machinemc.cogwheel.DataVisitor;
import org.machinemc.cogwheel.serialization.Serializer;
import org.machinemc.cogwheel.util.error.ErrorContainer;

public class LocationSerializer implements Serializer<Location> {

    private static final Serializer<EntityPosition> ENTITY_POSITION_SERIALIZER = new EntityPositionSerializer();
    private static final Serializer<NamespacedKey> NAMESPACED_KEY_SERIALIZER = new NamespacedKeySerializer();

    private final Server server;

    public LocationSerializer(final Server server) {
        this.server = server;
    }

    @Override
    public void serialize(final Location location, final DataVisitor visitor) {
        ENTITY_POSITION_SERIALIZER.serialize(location, visitor);
        NAMESPACED_KEY_SERIALIZER.serialize(location.getWorld().getName(), visitor);
    }

    @Override
    public @Nullable Location deserialize(final DataVisitor visitor, final ErrorContainer errorContainer) {
        EntityPosition entityPosition = ENTITY_POSITION_SERIALIZER.deserialize(visitor, errorContainer);
        if (entityPosition == null)
            entityPosition = EntityPosition.of(0, 0, 0);
        final NamespacedKey worldName = NAMESPACED_KEY_SERIALIZER.deserialize(visitor, errorContainer);
        return new Location(entityPosition, server.getWorld(worldName).orElse(null));
    }

}
