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

import com.google.common.base.Preconditions;
import lombok.SneakyThrows;
import org.machinemc.nbt.NBT;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.serialization.Serializer;
import org.machinemc.paklet.serialization.SerializerContext;
import org.machinemc.paklet.serialization.Supports;

/**
 * Network serializer for {@link NBTCompound}.
 */
@Supports(NBTCompound.class)
public class NBTCompoundSerializer implements Serializer<NBTCompound> {

    private static final byte COMPOUND_ID = (byte) NBT.Tag.COMPOUND.getID();

    @Override
    @SneakyThrows
    public void serialize(final SerializerContext context, final DataVisitor visitor, final NBTCompound compound) {
        visitor.writeByte(COMPOUND_ID);
        compound.write(visitor.asOutputStream());
    }

    @Override
    @SneakyThrows
    public NBTCompound deserialize(final SerializerContext context, final DataVisitor visitor) {
        Preconditions.checkState(visitor.readByte() == COMPOUND_ID, "Malformed NBT format");
        return NBTCompound.readCompound(visitor.asInputStream());
    }

}
