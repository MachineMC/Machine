package org.machinemc.api.world;

import com.google.common.base.Objects;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * Visual data of a block, to create a new instance
 * use {@link Material#createBlockData()}, each BlockData will
 * return unique id depending on its material and properties.
 */
// This class is used by the code generators, edit with caution.
public abstract class BlockData implements Cloneable {

    /**
     * Returns new instance of block data from id (mapped by vanilla server reports).
     * @param id id of the block data
     * @return new instance of the block data with the given id
     */
    public static BlockData getBlockData(final int id) {
        return BlockDataImpl.getBlockData(id);
    }

    /**
     * @return material of the block data
     */
    public abstract @Nullable Material getMaterial();

    /**
     * Changes base material for the block data and all its
     * variants.
     * <p>
     * Example: Changing material for oak log block data would
     * change the base material for all rotations of the oak log.
     * @param material new material
     * @return this
     */
    @Contract("_ -> this")
    protected abstract BlockData setMaterial(Material material);

    /**
     * Returns id of the block data.
     * @param blockData block data to get id from
     * @return id of the given block data
     */
    public static int getId(final BlockData blockData) {
        return BlockDataImpl.getId(blockData);
    }

    /**
     * @return id of the block data used by Minecraft protocol
     */
    public abstract int getId();

    /**
     * Returns all data used by the block data (block data properties)
     * in alphabetically order.
     * @return block data properties
     */
    protected abstract Object[] getData();

    /**
     * @return clone of this block data
     */
    public BlockData clone() {
        try {
            return (BlockData) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        if (getMaterial() != null)
            return getMaterial().getName().getKey() + Arrays.toString(getData());
        return "none" + Arrays.toString(getData());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockData blockData)) return false;
        if (getMaterial() != blockData.getMaterial()) return false;
        final Object[] original = getData();
        final Object[] compare = blockData.getData();
        if (original.length != compare.length) return false;
        for (int i = 0; i < original.length; i++) {
            if (original[i] != compare[i]) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getMaterial(), getData());
    }

}
