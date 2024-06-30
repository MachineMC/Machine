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
package org.machinemc.network.protocol.serializers;

import org.machinemc.barebones.profile.GameProfile;
import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.serialization.Serializer;
import org.machinemc.paklet.serialization.SerializerContext;
import org.machinemc.paklet.serialization.Supports;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Supports(GameProfile.class)
public class GameProfileSerializer implements Serializer<GameProfile> {

    @Override
    public void serialize(final SerializerContext context, final DataVisitor visitor, final GameProfile profile) {
        final Serializer<UUID> uuidSerializer = context.serializerProvider().getFor(UUID.class);
        final Serializer<String> stringSerializer = context.serializerProvider().getFor(String.class);
        final Serializer<Integer> intSerializer = context.serializerProvider().getFor(Integer.class);
        final Serializer<Boolean> booleanSerializer = context.serializerProvider().getFor(Boolean.class);
        final List<GameProfile.Property> properties = profile.properties();
        visitor.write(context, uuidSerializer, profile.uuid());
        visitor.write(context, stringSerializer, profile.name());
        visitor.write(context, intSerializer, properties.size());
        for (final GameProfile.Property property : properties) {
            visitor.write(context, stringSerializer, property.name());
            visitor.write(context, stringSerializer, property.value());
            visitor.write(context, booleanSerializer, property.signature() != null);
            if (property.signature() != null)
                visitor.write(context, stringSerializer, property.signature());
        }
    }

    @Override
    public GameProfile deserialize(final SerializerContext context, final DataVisitor visitor) {
        final Serializer<UUID> uuidSerializer = context.serializerProvider().getFor(UUID.class);
        final Serializer<String> stringSerializer = context.serializerProvider().getFor(String.class);
        final Serializer<Integer> intSerializer = context.serializerProvider().getFor(Integer.class);
        final Serializer<Boolean> booleanSerializer = context.serializerProvider().getFor(Boolean.class);
        final UUID uuid = visitor.read(context, uuidSerializer);
        final String name = visitor.read(context, stringSerializer);
        final int size = visitor.read(context, intSerializer);
        final List<GameProfile.Property> properties = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            final String propertyName = visitor.read(context, stringSerializer);
            final String value = visitor.read(context, stringSerializer);
            final String signature = visitor.read(context, booleanSerializer)
                    ? visitor.read(context, stringSerializer)
                    : null;
            properties.add(new GameProfile.Property(propertyName, value, signature));
        }
        return new GameProfile(uuid, name, properties);
    }

}
