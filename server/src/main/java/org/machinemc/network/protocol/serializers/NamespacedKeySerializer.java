package org.machinemc.network.protocol.serializers;

import org.machinemc.barebones.key.NamespacedKey;
import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.serialization.Serializer;
import org.machinemc.paklet.serialization.SerializerContext;
import org.machinemc.paklet.serialization.Supports;

@Supports(NamespacedKey.class)
public class NamespacedKeySerializer implements Serializer<NamespacedKey> {

    @Override
    public void serialize(SerializerContext context, DataVisitor visitor, NamespacedKey namespacedKey) {
        Serializer<String> serializer = context.serializerProvider().getFor(String.class);
        System.out.println(namespacedKey);
        visitor.write(context, serializer, namespacedKey.toString());
    }

    @Override
    public NamespacedKey deserialize(SerializerContext context, DataVisitor visitor) {
        Serializer<String> serializer = context.serializerProvider().getFor(String.class);
        return NamespacedKey.parse(visitor.read(context, serializer));
    }

}
