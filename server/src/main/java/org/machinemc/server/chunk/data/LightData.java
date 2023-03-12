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

    private final boolean trustEdges;
    private final BitSet
            skyMask,
            blockMask,
            emptySkyMask,
            emptyBlockMask;
    private final List<byte[]> skyLight, blockLight;

    public LightData(ServerBuffer buf) {
        trustEdges = buf.readBoolean();
        skyMask = BitSet.valueOf(buf.readLongArray());
        blockMask = BitSet.valueOf(buf.readLongArray());
        emptySkyMask = BitSet.valueOf(buf.readLongArray());
        emptyBlockMask = BitSet.valueOf(buf.readLongArray());

        int skyLights = buf.readVarInt();
        skyLight = new ArrayList<>(skyLights);
        for(int i = 0; i < skyLights; i++)
            skyLight.add(buf.readByteArray());

        int blockLights = buf.readVarInt();
        blockLight = new ArrayList<>(blockLights);
        for(int i = 0; i < blockLights; i++)
            blockLight.add(buf.readByteArray());
    }

    /**
     * Writes the data to a given buffer.
     * @param buf buffer to write into
     */
    @Override
    public void write(ServerBuffer buf) {
        buf.writeBoolean(trustEdges);

        buf.writeLongArray(skyMask.toLongArray());
        buf.writeLongArray(blockMask.toLongArray());

        buf.writeLongArray(emptySkyMask.toLongArray());
        buf.writeLongArray(emptyBlockMask.toLongArray());

        buf.writeVarInt(skyLight.size());
        for(byte[] bytes : skyLight)
            buf.writeByteArray(bytes);

        buf.writeVarInt(blockLight.size());
        for(byte[] bytes : blockLight)
            buf.writeByteArray(bytes);
    }

}