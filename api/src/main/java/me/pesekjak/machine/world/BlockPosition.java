package me.pesekjak.machine.world;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;
import me.pesekjak.machine.utils.ServerBuffer;
import me.pesekjak.machine.utils.Writable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Represents position of a block in the world.
 */
@Data
@With
@AllArgsConstructor
public class BlockPosition implements Writable, Cloneable {

    private static final long PACKED_X_MASK = 0x3FFFFFF; // max x-coordinate value
    private static final long PACKED_Y_MASK = 0xFFF; // max y-coordinate value
    private static final long PACKED_Z_MASK = 0x3FFFFFF; // max z-coordinate value

    private final int x;
    private final int y;
    private final int z;

    /**
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @return block position from given coordinates
     */
    @Contract("_, _, _ -> new")
    public static @NotNull BlockPosition of(int x, int y, int z) {
        return new BlockPosition(x, y, z);
    }

    /**
     * Decodes block position from long.
     * @param packedPos block position encoded as long
     * @return decoded block position
     */
    @Contract("_ -> new")
    public static @NotNull BlockPosition of(long packedPos) {
        return new BlockPosition(
                (int) (packedPos >> 38),
                (int) ((packedPos << 52) >> 52),
                (int) ((packedPos << 26) >> 38)
        );
    }

    /**
     * Encodes the block position as long.
     * @param x x coordinate of the BlockPosition
     * @param y y coordinate of the BlockPosition
     * @param z z coordinate of the BlockPosition
     * @return encoded BlockPosition
     */
    public static long asLong(int x, int y, int z) {
        return (((long) x & PACKED_X_MASK) << 38) |
                (((long) y & PACKED_Y_MASK)) |
                (((long) z & PACKED_Z_MASK) << 12);
    }

    /**
     * Position of a block in the world.
     * @param location location of the block
     */
    public BlockPosition(@NotNull Location location) {
        this(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * Encodes the block position as long.
     * @return encoded block position
     */
    public long asLong() {
        return asLong(this.getX(), this.getY(), this.getZ());
    }

    @Override
    public void write(@NotNull ServerBuffer buf) {
        buf.writeBlockPos(this);
    }

    @Override
    public BlockPosition clone() {
        try {
            return (BlockPosition) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
