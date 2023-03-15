package org.machinemc.server.world.blocks;

import lombok.Synchronized;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.world.BlockPosition;
import org.machinemc.api.world.World;
import org.machinemc.api.world.blocks.BlockType;
import org.machinemc.api.world.blocks.BlockVisual;
import org.machinemc.api.world.blocks.WorldBlock;
import org.machinemc.nbt.NBTCompound;

import java.util.*;
import java.util.function.Supplier;

/**
 * Default world block implementation.
 */
public class WorldBlockImpl implements WorldBlock {

    private Supplier<BlockType> blockType;
    private final BlockPosition position;
    private final World world;
    private NBTCompound compound;

    protected WorldBlockImpl(Supplier<BlockType> blockType, BlockPosition position, World world, @Nullable NBTCompound compound) {
        this.blockType = blockType;
        this.position = position;
        this.world = world;
    }

    @Synchronized
    @Override
    public BlockType getBlockType() {
        return blockType.get();
    }

    @Synchronized
    @Override
    public void setBlockType(BlockType blockType) {
        getWorld().setBlock(blockType, position);
    }

    @Synchronized
    @Override
    public BlockPosition getPosition() {
        return position;
    }

    @Synchronized
    @Override
    public World getWorld() {
        return world;
    }

    @Synchronized
    @Override
    public NBTCompound getNBT() {
        if(compound == null)
            compound = new NBTCompound();
        return compound;
    }

    // TODO implement
    @Synchronized
    @Override
    public void setVisual(BlockVisual visual) {
        // getWorld().setChunk(of this block).setVisual(coordinates, visual)
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
