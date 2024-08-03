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

import lombok.RequiredArgsConstructor;
import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.serialization.SerializerContext;
import org.machinemc.paklet.serialization.aliases.SerializerAlias;
import org.machinemc.scriptive.components.ClientComponent;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.serialization.JSONPropertiesSerializer;
import org.machinemc.text.ComponentProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Serializer for Minecraft text components in JSON string format.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE_USE)
@SerializerAlias(JSONTextComponent.Serializer.class)
public @interface JSONTextComponent {

    /**
     * Implementation of the serializer.
     */
    @RequiredArgsConstructor
    class Serializer implements org.machinemc.paklet.serialization.Serializer<Component> {

        private final ComponentProcessor componentProcessor;
        private final JSONPropertiesSerializer propertiesSerializer = new JSONPropertiesSerializer();

        @Override
        public void serialize(final SerializerContext serializerContext, final DataVisitor dataVisitor, final Component component) {
            final ClientComponent transformed = componentProcessor.transform(component);
            final String json = componentProcessor.getSerializer().serialize(transformed, propertiesSerializer);
            serializerContext.serializerProvider().getFor(String.class).serialize(serializerContext, dataVisitor, json);
        }

        @Override
        public Component deserialize(final SerializerContext serializerContext, final DataVisitor dataVisitor) {
            final String json = serializerContext.serializerProvider().getFor(String.class).deserialize(serializerContext, dataVisitor);
            return componentProcessor.getSerializer().deserialize(json, propertiesSerializer);
        }

    }

}
