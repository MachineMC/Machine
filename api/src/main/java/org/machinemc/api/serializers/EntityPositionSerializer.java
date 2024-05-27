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
import org.machinemc.api.world.EntityPosition;
import org.machinemc.cogwheel.DataVisitor;
import org.machinemc.cogwheel.serialization.Serializer;
import org.machinemc.cogwheel.util.error.ErrorContainer;

public class EntityPositionSerializer implements Serializer<EntityPosition> {

    @Override
    public void serialize(EntityPosition entityPosition, DataVisitor visitor) {
        visitor.enterSection().visit("x").writeNumber(entityPosition.getX())
                .visit("y").writeNumber(entityPosition.getY())
                .visit("z").writeNumber(entityPosition.getZ())
                .visit("yaw").writeNumber(entityPosition.getYaw())
                .visit("pitch").writeNumber(entityPosition.getPitch());
    }

    @Override
    public @Nullable EntityPosition deserialize(DataVisitor visitor, ErrorContainer errorContainer) {
        double x = visitor.readNumber().orElse(0d).doubleValue();
        double y = visitor.readNumber().orElse(0d).doubleValue();
        double z = visitor.readNumber().orElse(0d).doubleValue();
        float yaw = visitor.readNumber().orElse(0d).floatValue();
        float pitch = visitor.readNumber().orElse(0d).floatValue();
        return EntityPosition.of(x, y, z, yaw, pitch);
    }

}
