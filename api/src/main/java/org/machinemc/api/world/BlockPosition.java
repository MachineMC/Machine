package org.machinemc.api.world;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.utils.Writable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Represents position of a block in the world.
 */
@Data
@With
@AllArgsConstructor
public class BlockPosition implements Writable, Cloneable {

    public static final long PACKED_X_MASK = 0x3FFFFFF; // max x-coordinate value
    public static final long PACKED_Y_MASK = 0xFFF; // max y-coordinate value
    public static final long PACKED_Z_MASK = 0x3FFFFFF; // max z-coordinate value

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
     * Position of a block in the world.
     * @param location location of the block
     */
    public BlockPosition(@NotNull Location location) {
        this(location.getBlockX(), location.getBlockY(), location.getBlockZ());
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
