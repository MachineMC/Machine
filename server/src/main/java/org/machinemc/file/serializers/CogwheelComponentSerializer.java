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
package org.machinemc.file.serializers;

import org.jetbrains.annotations.Nullable;
import org.machinemc.cogwheel.DataVisitor;
import org.machinemc.cogwheel.serialization.Serializer;
import org.machinemc.cogwheel.util.error.ErrorContainer;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.serialization.ComponentSerializer;
import org.machinemc.scriptive.serialization.PropertiesSerializer;

/**
 * Cogwheel serializer fpr components.
 *
 * @param componentSerializer component serializer
 * @param propertiesSerializer component properties serializer
 */
public record CogwheelComponentSerializer(ComponentSerializer componentSerializer,
                                          PropertiesSerializer<String> propertiesSerializer) implements Serializer<Component> {

    @Override
    public void serialize(final Component properties, final DataVisitor visitor) {
        visitor.writeString(componentSerializer.serialize(properties, propertiesSerializer));
    }

    @Override
    public @Nullable Component deserialize(final DataVisitor visitor, final ErrorContainer errorContainer) {
        return visitor.readString().map(string -> componentSerializer.deserialize(string, propertiesSerializer)).orElse(null);
    }

}
