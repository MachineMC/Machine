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

import org.machinemc.barebones.key.NamespacedKey;
import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.serialization.Serializer;
import org.machinemc.paklet.serialization.SerializerContext;
import org.machinemc.paklet.serialization.Supports;

/**
 * Network serializer for {@link NamespacedKey}.
 */
@Supports(NamespacedKey.class)
public class NamespacedKeySerializer implements Serializer<NamespacedKey> {

    @Override
    public void serialize(final SerializerContext context, final DataVisitor visitor, final NamespacedKey namespacedKey) {
        final Serializer<String> serializer = context.serializerProvider().getFor(String.class);
        visitor.write(context, serializer, namespacedKey.toString());
    }

    @Override
    public NamespacedKey deserialize(final SerializerContext context, final DataVisitor visitor) {
        final Serializer<String> serializer = context.serializerProvider().getFor(String.class);
        return NamespacedKey.parse(visitor.read(context, serializer));
    }

}
