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

    public ChunkData(ServerBuffer buf) {
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
    public void write(ServerBuffer buf) {
        buf.writeNBT(this.heightmaps);
        buf.writeByteArray(data);
        buf.writeVarInt(blockEntities.length);
        for (Section.BlockEntity blockEntity : blockEntities)
            blockEntity.write(buf);
    }

}
