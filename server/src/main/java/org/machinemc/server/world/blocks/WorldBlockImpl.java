package org.machinemc.server.world.blocks;

import lombok.Synchronized;
import org.machinemc.api.world.BlockPosition;
import org.machinemc.api.world.World;
import org.machinemc.api.world.blocks.BlockType;
import org.machinemc.api.world.blocks.WorldBlock;
import org.machinemc.nbt.NBTCompound;

import java.util.*;
import java.util.function.Supplier;

/**
 * Default world block implementation.
 */
@SuppressWarnings("ClassCanBeRecord")
public class WorldBlockImpl implements WorldBlock {

    private final World world;
    private final BlockPosition position;
    private final Supplier<BlockType> blockType;
    private final Supplier<NBTCompound> compound;

    protected WorldBlockImpl(World world, BlockPosition position, Supplier<BlockType> blockType, Supplier<NBTCompound> compound) {
        this.world = world;
        this.position = position;
        this.blockType = blockType;
        this.compound = compound;
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
        return blockType.get();
    }

    @Override
    public void setBlockType(BlockType blockType) {
        world.setBlock(blockType, position);
    }

    @Synchronized
    @Override
    public NBTCompound getNBT() {
        return compound.get().clone();
    }

    @Override
    public void setNBT(NBTCompound compound) {
        NBTCompound source = this.compound.get();
        source.clear();
        source.putAll(compound);
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
