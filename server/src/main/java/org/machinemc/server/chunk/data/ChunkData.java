package org.machinemc.server.chunk.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
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

    public ChunkData(ServerBuffer buf) {
        heightmaps = buf.readNBT();
        data = buf.readByteArray();
        buf.readVarInt();
    }

    /**
     * Writes the data to a given buffer.
     * @param buf buffer to write into
     */
    @Override
    public void write(ServerBuffer buf) {
        buf.writeNBT(this.heightmaps);
        buf.writeByteArray(data);
        buf.writeVarInt(0); // TODO Block entities
    }


}
