package org.machinemc.api.world.blocks;

/**
 * Represents a block type with custom internal nbt compound.
 */
public interface EntityBlockType extends BlockType {

    /**
     * Called when new block of this type is generated or placed,
     * initial NBT data of the block needs to be set here.
     * @param state newly created block
     */
    // TODO Event support
    void initialize(WorldBlock.State state);

}
