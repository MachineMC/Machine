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
import org.machinemc.nbt.NBTCompound;
import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.serialization.Serializer;
import org.machinemc.paklet.serialization.SerializerContext;
import org.machinemc.paklet.serialization.Supports;
import org.machinemc.scriptive.components.ClientComponent;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.serialization.NBTPropertiesSerializer;
import org.machinemc.text.ComponentProcessor;

/**
 * Network serializer for {@link Component}.
 */
@Supports(Component.class)
@RequiredArgsConstructor
public class ComponentSerializer implements Serializer<Component> {

    private final ComponentProcessor componentProcessor;
    private final NBTPropertiesSerializer propertiesSerializer = NBTPropertiesSerializer.get();

    @Override
    public void serialize(final SerializerContext context, final DataVisitor visitor, final Component component) {
        final ClientComponent transformed = componentProcessor.transform(component);
        final NBTCompound compound = componentProcessor.getSerializer().serialize(transformed, propertiesSerializer);
        context.serializerProvider().getFor(NBTCompound.class).serialize(context, visitor, compound);
    }

    @Override
    public Component deserialize(final SerializerContext context, final DataVisitor visitor) {
        final NBTCompound compound = context.serializerProvider().getFor(NBTCompound.class).deserialize(context, visitor);
        return componentProcessor.getSerializer().deserialize(compound, propertiesSerializer);
    }

}
