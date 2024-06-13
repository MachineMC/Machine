/*
 * This file is part of Machine.
 *
 * Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Machine.
 * If not, see https://www.gnu.org/licenses/.
 */
package org.machinemc.server.world.blocks;

import lombok.Synchronized;
import org.jetbrains.annotations.Nullable;
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
public class WorldBlockImpl implements WorldBlock {

    private final World world;
    private final BlockPosition position;
    private final Supplier<BlockType> blockTypeSupplier;

    protected WorldBlockImpl(final World world,
                             final BlockPosition position,
                             final Supplier<BlockType> blockTypeSupplier) {
        this.world = Objects.requireNonNull(world, "World of world block can not be null");
        this.position = Objects.requireNonNull(position, "Position of world block can not be null");
        this.blockTypeSupplier = Objects.requireNonNull(blockTypeSupplier, "Block type supplier can not be null");
    }

    @Override
    @Synchronized
    public World getWorld() {
        return world;
    }

    @Override
    @Synchronized
    public BlockPosition getPosition() {
        return position;
    }

    @Override
    @Synchronized
    public BlockType getBlockType() {
        return blockTypeSupplier.get();
    }

    @Override
    @Synchronized
    public void setBlockType(final BlockType blockType) {
        world.setBlock(blockType, position);
    }

    @Override
    @Synchronized
    public NBTCompound getNBT() {
        return world.getChunk(position).getBlockNBT(
                ChunkUtils.getSectionRelativeCoordinate(position.getX()),
                position.getY(),
                ChunkUtils.getSectionRelativeCoordinate(position.getZ()));
    }

    @Override
    @Synchronized
    public void mergeNBT(final NBTCompound compound) {
        world.getChunk(position).mergeBlockNBT(
                ChunkUtils.getSectionRelativeCoordinate(position.getX()),
                position.getY(),
                ChunkUtils.getSectionRelativeCoordinate(position.getZ()),
                compound);
    }

    @Override
    @Synchronized
    public void setNBT(final @Nullable NBTCompound compound) {
        world.getChunk(position).setBlockNBT(
                ChunkUtils.getSectionRelativeCoordinate(position.getX()),
                position.getY(),
                ChunkUtils.getSectionRelativeCoordinate(position.getZ()),
                compound);
    }

    @Override
    @Synchronized
    public BlockData getBlockData() {
        final State state = asState();
        BlockData visual = getBlockType().getBlockData(state);
        for (final BlockHandler blockHandler : state.blockType().getHandlers())
            visual = blockHandler.onVisualRequest(state, visual);
        return visual;
    }

    @Override
    public String toString() {
        return "WorldBlock("
                + world
                + ", " + position
                + ')';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof WorldBlockImpl that)) return false;
        return position.equals(that.position) && world.equals(that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, world);
    }

}
