package org.machinemc.api.chunk;

import org.machinemc.api.chunk.palette.Palette;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.utils.Writable;
import org.machinemc.api.world.blocks.BlockEntityBase;
import org.machinemc.nbt.NBTCompound;

import java.util.Map;

/**
 * Represents a 16 blocks tall section of a chunk.
 */
public interface Section extends Writable, Cloneable {

    /**
     * @return source of this section
     */
    Chunk getSource();

    /**
     * @return index of the section in the source chunk
     */
    int getIndex();

    /**
     * @return block palette used by this section
     */
    Palette getBlockPalette();

    /**
     * @return biome palette used by this section
     */
    Palette getBiomePalette();

    /**
     * Map of client block entities within this section, for
     * encoding map keys {@link Section#index(int, int, int)} is used,
     * for decoding use {@link Section#decode(int)}.
     * @return all block entities within this section
     */
    Map<Integer, BlockEntity> getClientBlockEntities();

    /**
     * Returns clone of nbt data of this section.
     * <p>
     * Information about entities, generation etc. can be
     * stored there.
     * @return data of this section
     */
    NBTCompound getData();

    /**
     * Merges provided compound with the compound of the section.
     * @param compound compound to merge
     */
    void mergeData(NBTCompound compound);

    /**
     * Sets the data to this section.
     * @param compound new data
     */
    void setData(NBTCompound compound);

    /**
     * @return sky light data of this section
     */
    byte[] getSkyLight();

    /**
     * @return block light data of this section
     */
    byte[] getBlockLight();

    /**
     * Clears the section.
     */
    void clear();

    /**
     * Creates a unique index for a coordinates within the section.
     * @param x x
     * @param y y
     * @param z z
     * @return unique index of the coordinates
     */
    static int index(final int x, final int y, final int z) {
        return (x & 0xF) | ((y << 4) & 0xF0) | (z << 8);
    }

    /**
     * Decodes the index encoded using {@link #index(int, int, int)}.
     * @param index index
     * @return array of x, y, and z respectively
     */
    static int[] decode(final int index) {
        final int[] values = new int[3];
        values[0] = index & 0xF;
        values[1] = index & 0xF0;
        values[2] = index & 0xF00;
        return values;
    }

    /**
     * Represents a block entity that's sent to the client.
     * @param x chunk relative x coordinate of the entity
     * @param y y coordinate of the entity (in the world - accepts negative values)
     * @param z chunk relative z coordinate of the entity
     */
    record BlockEntity(byte x,
                       short y,
                       byte z,
                       BlockEntityBase base,
                       NBTCompound data) implements Writable {

        public BlockEntity(byte packedXZ, short y, BlockEntityBase base, NBTCompound data) {
            this((byte) (packedXZ & 0xF0), y, (byte) (packedXZ & 0xF), base, data);
        }

        public BlockEntity(ServerBuffer buf) {
            this(buf.readByte(), buf.readShort(), BlockEntityBase.fromID(buf.readVarInt()), buf.readNBT());
        }

        /**
         * Returns x and z coordinates of the block entity as single byte, used for
         * chunk encoding.
         * @return packed x and z coordinates
         */
        public byte packedXZ() {
            return (byte) ((x << 4) | z);
        }

        @Override
        public void write(ServerBuffer buf) {
            buf.writeByte(packedXZ());
            buf.writeShort(y);
            buf.writeVarInt(base.getId());
            buf.writeNBT(data);
        }

    }

}
