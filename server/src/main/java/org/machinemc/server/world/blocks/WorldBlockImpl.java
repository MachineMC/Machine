package org.machinemc.server.world.blocks;

import lombok.Synchronized;
import org.machinemc.api.world.BlockData;
import org.machinemc.api.world.BlockPosition;
import org.machinemc.api.world.World;
import org.machinemc.api.world.blocks.BlockHandler;
import org.machinemc.api.world.blocks.BlockType;
import org.machinemc.api.world.blocks.WorldBlock;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.server.chunk.ChunkUtils;

import java.util.*;
import java.util.function.Supplier;

/**
 * Default world block implementation.
 * @see WorldBlockManager
 */
@SuppressWarnings("ClassCanBeRecord")
public class WorldBlockImpl implements WorldBlock {

    private final World world;
    private final BlockPosition position;
    private final Supplier<BlockType> blockTypeSupplier;
    private final Supplier<NBTCompound> nbtSupplier;

    protected WorldBlockImpl(World world, BlockPosition position,
                             Supplier<BlockType> blockTypeSupplier,
                             Supplier<NBTCompound> nbtSupplier) {
        this.world = world;
        this.position = position;
        this.blockTypeSupplier = blockTypeSupplier;
        this.nbtSupplier = nbtSupplier;
    }

    @Synchronized
    @Override
    public World getWorld() {
        return world;
    }

    @Synchronized
    @Override
    public BlockPosition getPosition() {
        return position;
    }

    @Synchronized
    @Override
    public BlockType getBlockType() {
        return blockTypeSupplier.get();
    }

    @Synchronized
    @Override
    public void setBlockType(BlockType blockType) {
        world.setBlock(blockType, position);
    }

    @Synchronized
    @Override
    public NBTCompound getNBT() {
        return nbtSupplier.get().clone();
    }

    @Synchronized
    @Override
    public void setNBT(NBTCompound compound) {
        world.getChunk(position).setBlockNBT(ChunkUtils.getSectionRelativeCoordinate(position.getX()), position.getY(), ChunkUtils.getSectionRelativeCoordinate(position.getZ()), compound);
    }

    @Synchronized
    @Override
    public BlockData getBlockData() {
        final State state = asState();
        BlockData visual = getBlockType().getBlockData(state);
        for(final BlockHandler blockHandler : state.blockType().getHandlers())
            visual = blockHandler.onVisualRequest(state, visual);
        return visual;
    }

    @Override
    public String toString() {
        return "WorldBlock[" +
                world +
                ", " + position +
                ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorldBlockImpl that)) return false;
        return position.equals(that.position) && world.equals(that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, world);
    }

}
