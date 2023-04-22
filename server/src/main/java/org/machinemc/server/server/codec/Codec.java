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
package org.machinemc.server.server.codec;

import lombok.NoArgsConstructor;
import org.machinemc.api.server.NBTSerializable;
import org.machinemc.api.server.codec.CodecPart;
import org.machinemc.nbt.NBTCompound;

import java.util.ArrayList;
import java.util.List;

/**
 * Codec containing NBTCompound representing certain registries
 * that are sent from the server and are applied on the client.
 */
@NoArgsConstructor
public class Codec implements NBTSerializable {

    private final List<CodecPart> codecParts = new ArrayList<>();

    public Codec(final CodecPart... codecParts) {
        this.codecParts.addAll(List.of(codecParts));
    }

    @Override
    public NBTCompound toNBT() {
        final NBTCompound compound = new NBTCompound();
        for (final CodecPart part : codecParts)
            compound.set(part.getCodecType(), part.getCodecNBT());
        return compound;
    }

}
