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
package org.machinemc.server.chunk.data;

import lombok.AllArgsConstructor;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.utils.Writable;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Data about chunk's lighting.
 */
@AllArgsConstructor
public class LightData implements Writable {

    private final BitSet skyMask;
    private final BitSet blockMask;
    private final BitSet emptySkyMask;
    private final BitSet emptyBlockMask;
    private final List<byte[]> skyLight, blockLight;

    public LightData(final ServerBuffer buf) {
        skyMask = BitSet.valueOf(buf.readLongArray());
        blockMask = BitSet.valueOf(buf.readLongArray());
        emptySkyMask = BitSet.valueOf(buf.readLongArray());
        emptyBlockMask = BitSet.valueOf(buf.readLongArray());

        final int skyLights = buf.readVarInt();
        skyLight = new ArrayList<>(skyLights);
        for (int i = 0; i < skyLights; i++)
            skyLight.add(buf.readByteArray());

        final int blockLights = buf.readVarInt();
        blockLight = new ArrayList<>(blockLights);
        for (int i = 0; i < blockLights; i++)
            blockLight.add(buf.readByteArray());
    }

    /**
     * Writes the data to a given buffer.
     * @param buf buffer to write into
     */
    @Override
    public void write(final ServerBuffer buf) {
        buf.writeBitSet(skyMask);
        buf.writeBitSet(blockMask);

        buf.writeBitSet(emptySkyMask);
        buf.writeBitSet(emptyBlockMask);

        buf.writeVarInt(skyLight.size());
        for (final byte[] bytes : skyLight)
            buf.writeByteArray(bytes);

        buf.writeVarInt(blockLight.size());
        for (final byte[] bytes : blockLight)
            buf.writeByteArray(bytes);
    }

}
