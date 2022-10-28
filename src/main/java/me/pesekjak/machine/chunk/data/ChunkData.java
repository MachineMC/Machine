package me.pesekjak.machine.chunk.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

@AllArgsConstructor
@Getter
public class ChunkData {

    private final NBTCompound heightmaps;
    private final byte[] data;

    public ChunkData(FriendlyByteBuf buf) {
        this((NBTCompound) buf.readNBT(), buf.readByteArray());
        buf.readVarInt();
    }

    public void write(FriendlyByteBuf buf) {
        // Heightmaps
        buf.writeNBT("", this.heightmaps);
        // Data
        buf.writeByteArray(data);
        // Block entities
        buf.writeVarInt(0);
    }


}
