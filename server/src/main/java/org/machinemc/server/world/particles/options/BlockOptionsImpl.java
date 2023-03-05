package org.machinemc.server.world.particles.options;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.machinemc.api.world.Material;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.world.BlockData;
import org.machinemc.api.world.BlockDataImpl;
import org.machinemc.server.world.particles.ParticleFactory;
import org.jetbrains.annotations.NotNull;
import org.machinemc.api.world.particles.options.BlockOptions;

import java.util.Map;

/**
 * Default block options implementation.
 */
@Data
@AllArgsConstructor
public class BlockOptionsImpl implements BlockOptions {

    private static final Material DEFAULT_LOOK = Material.STONE;

    private @NotNull BlockData blockData;

    static {
        ParticleFactory.registerOption(BlockOptions.class, BlockOptionsImpl::new);
    }

    public BlockOptionsImpl() {
        this.blockData = Material.STONE.createBlockData();
    }

    public BlockOptionsImpl(@NotNull ServerBuffer buf) {
        final BlockData blockData = BlockDataImpl.getBlockData(buf.readVarInt());
        this.blockData = blockData != null ? blockData : DEFAULT_LOOK.createBlockData();
    }

    @Override
    public @NotNull NBTCompound toNBT() {
        if(blockData.getMaterial() == null)
            return new NBTCompound(Map.of("Name", DEFAULT_LOOK.getName().toString()));
        return new NBTCompound(Map.of(
                "Name", blockData.getMaterial().getName().toString()
                // TODO Full block data support (properties)
        ));
    }

    @Override
    public void write(@NotNull ServerBuffer buf) {
        buf.writeVarInt(blockData.getId());
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public BlockOptions clone() {
        final ServerBuffer buf = new FriendlyByteBuf().write(this);
        return new BlockOptionsImpl(buf);
    }
}
