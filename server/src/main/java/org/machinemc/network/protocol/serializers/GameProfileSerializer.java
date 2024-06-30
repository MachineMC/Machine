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
    public void serialize(SerializerContext context, DataVisitor visitor, GameProfile profile) {
        Serializer<UUID> uuidSerializer = context.serializerProvider().getFor(UUID.class);
        Serializer<String> stringSerializer = context.serializerProvider().getFor(String.class);
        Serializer<Integer> intSerializer = context.serializerProvider().getFor(Integer.class);
        Serializer<Boolean> booleanSerializer = context.serializerProvider().getFor(Boolean.class);
        List<GameProfile.Property> properties = profile.properties();
        visitor.write(context, uuidSerializer, profile.uuid());
        visitor.write(context, stringSerializer, profile.name());
        visitor.write(context, intSerializer, properties.size());
        for (GameProfile.Property property : properties) {
            visitor.write(context, stringSerializer, property.name());
            visitor.write(context, stringSerializer, property.value());
            visitor.write(context, booleanSerializer, property.signature() != null);
            if (property.signature() != null)
                visitor.write(context, stringSerializer, property.signature());
        }
    }

    @Override
    public GameProfile deserialize(SerializerContext context, DataVisitor visitor) {
        Serializer<UUID> uuidSerializer = context.serializerProvider().getFor(UUID.class);
        Serializer<String> stringSerializer = context.serializerProvider().getFor(String.class);
        Serializer<Integer> intSerializer = context.serializerProvider().getFor(Integer.class);
        Serializer<Boolean> booleanSerializer = context.serializerProvider().getFor(Boolean.class);
        UUID uuid = visitor.read(context, uuidSerializer);
        String name = visitor.read(context, stringSerializer);
        int size = visitor.read(context, intSerializer);
        List<GameProfile.Property> properties = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            String propertyName = visitor.read(context, stringSerializer);
            String value = visitor.read(context, stringSerializer);
            String signature = null;
            if (visitor.read(context, booleanSerializer))
                signature = visitor.read(context, stringSerializer);
            properties.add(new GameProfile.Property(propertyName, value, signature));
        }
        return new GameProfile(uuid, name, properties);
    }

}
