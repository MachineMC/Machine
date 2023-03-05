package org.machinemc.server.chunk.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.utils.Writable;
import org.jetbrains.annotations.NotNull;
import org.machinemc.nbt.NBTCompound;

/**
 * Data about chunk's blocks and biomes.
 */
@AllArgsConstructor
@Getter
public class ChunkData implements Writable {

    private final @NotNull NBTCompound heightmaps;
    private final byte @NotNull [] data;

    public ChunkData(@NotNull ServerBuffer buf) {
        heightmaps = buf.readNBT();
        data = buf.readByteArray();
        buf.readVarInt();
    }

    /**
     * Writes the data to a given buffer.
     * @param buf buffer to write into
     */
    @Override
    public void write(@NotNull ServerBuffer buf) {
        buf.writeNBT(this.heightmaps);
        buf.writeByteArray(data);
        buf.writeVarInt(0); // TODO Block entities
    }


}
