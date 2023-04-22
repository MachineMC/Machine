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
 * If not, see <https://www.gnu.org/licenses/>.
 */
package org.machinemc.api.world.blocks;

import org.jetbrains.annotations.Nullable;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.nbt.NBTInt;
import org.machinemc.nbt.NBTString;

/**
 * Represents a block type with custom internal nbt compound.
 */
public interface BlockEntityType extends BlockType {

    /**
     * Called when new block of this type is generated or placed,
     * initial NBT data of the block needs to be set here.
     * @param state newly created block
     */
    // TODO Event support
    void initialize(WorldBlock.State state);

    /**
     * Whether the block entity nbt should also be send to the client.
     * <p>
     * If true, it's required to specify the base of the
     * block entity with {@link #getBlockEntityBase(WorldBlock.State)} and
     * NBT that will be sent to the client {@link #getClientVisibleNBT(WorldBlock.State)}.
     * @return if the block's nbt should be send
     */
    boolean sendsToClient();

    /**
     * Chooses the correct block entity base for the given block state.
     * <p>
     * Can be null if {@link #sendsToClient()} is false.
     * @param state state of the block
     * @return base of the block entity
     * @see BlockEntityType#sendsToClient()
     */
    @Nullable BlockEntityBase getBlockEntityBase(WorldBlock.State state);

    /**
     * Creates NBT that should be sent to the client from the block's state.
     * <p>
     * Can be null if {@link #sendsToClient()} is false.
     * @param state state of the block
     * @return nbt of given block that will be sent to client
     * @see BlockEntityType#sendsToClient()
     */
    @Nullable NBTCompound getClientVisibleNBT(WorldBlock.State state);

    /**
     * Creates base for the client visible nbt to render the block correctly, containing
     * id of the base and block position.
     * <p>
     * Can be used with {@link #getClientVisibleNBT(WorldBlock.State)} to get the
     * base information to the compound.
     * <p>
     * Can be null if {@link #sendsToClient()} is false.
     * @param state state of the block
     * @return base compound for the client
     */
    default @Nullable NBTCompound getBaseClientVisibleNBT(final WorldBlock.State state) {
        final BlockEntityBase base = getBlockEntityBase(state);
        if (base == null) return null;
        final NBTCompound compound = new NBTCompound();
        compound.set("id", new NBTString(base.getName().toString()));
        compound.set("x", new NBTInt(state.position().getX()));
        compound.set("y", new NBTInt(state.position().getY()));
        compound.set("z", new NBTInt(state.position().getZ()));
        return compound;
    }

}
