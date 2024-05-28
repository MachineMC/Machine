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
import org.machinemc.cogwheel.DataVisitor;
import org.machinemc.cogwheel.serialization.Serializer;
import org.machinemc.cogwheel.util.error.ErrorContainer;
import org.machinemc.scriptive.components.Component;

public record ComponentSerializer(Server server) implements Serializer<Component> {

    @Override
    public void serialize(Component properties, DataVisitor visitor) {
        visitor.writeString(server.getComponentSerializer().serialize(properties));
    }

    @Override
    public @Nullable Component deserialize(DataVisitor visitor, ErrorContainer errorContainer) {
        return visitor.readString().map(server.getComponentSerializer()::deserialize).orElse(null);
    }

}
