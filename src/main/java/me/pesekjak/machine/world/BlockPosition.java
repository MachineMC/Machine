package me.pesekjak.machine.world;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents BlockPosition in a world.
 */
@AllArgsConstructor
@Data
public class BlockPosition implements Cloneable {

    private static final long PACKED_X_MASK = 0x3FFFFFF;
    private static final long PACKED_Y_MASK = 0xFFF;
    private static final long PACKED_Z_MASK = 0x3FFFFFF;

    private int x;
    private int y;
    private int z;

    public BlockPosition(Location location) {
        this(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Override
    public BlockPosition clone() throws CloneNotSupportedException {
        return (BlockPosition) super.clone();
    }

    /**
     * Decodes BlockPosition from long, more information
     * about how Minecraft encodes BlockPositions here: https://wiki.vg/
     * @param packedPos BlockPosition encoded as long
     * @return Decoded BlockPosition
     */
    public static BlockPosition of(long packedPos) {
        return new BlockPosition(
                (int) (packedPos >> 38),
                (int) ((packedPos << 52) >> 52),
                (int) ((packedPos << 26) >> 38)
        );
    }

    /**
     * Encodes BlockPosition as long, more information
     * about how Minecraft encodes BlockPositions here: https://wiki.vg/
     * @param x x coordinate of the BlockPosition
     * @param y y coordinate of the BlockPosition
     * @param z z coordinate of the BlockPosition
     * @return Encoded BlockPosition
     */
    public static long asLong(int x, int y, int z) {
        return (((long) x & PACKED_X_MASK) << 38) |
                (((long) y & PACKED_Y_MASK)) |
                (((long) z & PACKED_Z_MASK) << 12);
    }

    /**
     * Encodes BlockPosition as long, more information
     * about how Minecraft encodes BlockPositions here: https://wiki.vg/
     * @return Encoded BlockPosition
     */
    public long asLong() {
        return asLong(this.getX(), this.getY(), this.getZ());
    }

}
