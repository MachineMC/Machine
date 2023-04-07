package org.machinemc.api.world.blocks;

import org.jetbrains.annotations.Nullable;
import org.machinemc.nbt.NBTCompound;

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
     * block entity with {@link BlockEntityType#getBlockEntityBase(WorldBlock.State)}.
     * <p>
     * NBT that's send to the client is specified using
     * {@link BlockEntityType#getClientVisibleNBT(WorldBlock.State)}.
     * @return if the block's nbt should be send
     */
    boolean sendsToClient();

    /**
     * Chooses the correct block entity base for the given block state
     * @param state state of the block
     * @return base of the block entity
     * @see BlockEntityType#sendsToClient()
     */
    @Nullable BlockEntityBase getBlockEntityBase(WorldBlock.State state);

    /**
     * Creates NBT that should be sent to the client from the block's state.
     * @param state state of the block
     * @return nbt of given block that will be sent to client
     * @see BlockEntityType#sendsToClient()
     */
    @Nullable NBTCompound getClientVisibleNBT(WorldBlock.State state);

}
