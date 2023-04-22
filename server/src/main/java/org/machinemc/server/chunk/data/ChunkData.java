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
 * If not, see <https://www.gnu.org/licenses/>.
 */
package org.machinemc.server.chunk.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.machinemc.api.chunk.Section;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.utils.Writable;
import org.machinemc.nbt.NBTCompound;

/**
 * Data about chunk's blocks and biomes.
 */
@AllArgsConstructor
@Getter
public class ChunkData implements Writable {

    private final NBTCompound heightmaps;
    private final byte[] data;
    private final Section.BlockEntity[] blockEntities;

    public ChunkData(final ServerBuffer buf) {
        heightmaps = buf.readNBT();
        data = buf.readByteArray();
        blockEntities = new Section.BlockEntity[buf.readVarInt()];
        for (int i = 0; i < blockEntities.length; i++)
            blockEntities[i] = new Section.BlockEntity(buf);
    }

    /**
     * Writes the data to a given buffer.
     * @param buf buffer to write into
     */
    @Override
    public void write(final ServerBuffer buf) {
        buf.writeNBT(this.heightmaps);
        buf.writeByteArray(data);
        buf.writeVarInt(blockEntities.length);
        for (final Section.BlockEntity blockEntity : blockEntities)
            blockEntity.write(buf);
    }

}
