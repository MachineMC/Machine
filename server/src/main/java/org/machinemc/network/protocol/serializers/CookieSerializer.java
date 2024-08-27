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
import org.machinemc.client.cookie.Cookie;
import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.serialization.*;

/**
 * Network serializer for {@link Cookie}.
 */
@Supports(Cookie.class)
public class CookieSerializer implements Serializer<Cookie> {

    @Override
    public void serialize(final SerializerContext context, final DataVisitor visitor, final Cookie cookie) {
        final Serializer<NamespacedKey> keySerializer = context.serializerProvider().getFor(NamespacedKey.class);
        final Serializer<byte[]> bytesSerializer = context.serializerProvider().getFor(byte[].class);
        visitor.write(context, keySerializer, cookie.key());
        visitor.write(context.withType(new Token<byte[]>() { }), bytesSerializer, cookie.payload());
    }

    @Override
    public Cookie deserialize(final SerializerContext context, final DataVisitor visitor) {
        final Serializer<NamespacedKey> keySerializer = context.serializerProvider().getFor(NamespacedKey.class);
        final Serializer<byte[]> bytesSerializer = context.serializerProvider().getFor(byte[].class);
        final NamespacedKey key = visitor.read(context, keySerializer);
        final byte[] payload = visitor.read(context.withType(new Token<byte[]>() { }), bytesSerializer);
        return new Cookie(key, payload);
    }

}
