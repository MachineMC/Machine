package me.pesekjak.machine.chunk.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.ServerBuffer;
import me.pesekjak.machine.utils.Writable;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

/**
 * Data about chunk's blocks and biomes.
 */
@AllArgsConstructor
@Getter
public class ChunkData implements Writable {

    private final NBTCompound heightmaps;
    private final byte[] data;

    public ChunkData(FriendlyByteBuf buf) {
        this((NBTCompound) buf.readNBT(), buf.readByteArray());
        buf.readVarInt();
    }

    /**
     * Writes the data to a given buffer.
     * @param buf buffer to write into
     */
    @Override
    public void write(@NotNull ServerBuffer buf) {
        buf.writeNBT("", this.heightmaps);
        buf.writeByteArray(data);
        buf.writeVarInt(0); // TODO Block entities
    }


}
