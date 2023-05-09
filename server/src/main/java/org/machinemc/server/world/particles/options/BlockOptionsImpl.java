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
package org.machinemc.server.world.particles.options;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.machinemc.api.world.Material;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.world.BlockData;
import org.machinemc.server.world.particles.ParticleFactory;
import org.machinemc.api.world.particles.options.BlockOptions;

import java.util.Map;

/**
 * Default block options implementation.
 */
@Data
@AllArgsConstructor
public class BlockOptionsImpl implements BlockOptions {

    private static final Material DEFAULT_LOOK = Material.STONE;

    private BlockData blockData;

    static {
        ParticleFactory.registerOption(BlockOptions.class, BlockOptionsImpl::new);
    }

    public BlockOptionsImpl() {
        this.blockData = Material.STONE.createBlockData();
    }

    public BlockOptionsImpl(final ServerBuffer buf) {
        final BlockData blockData = BlockData.getBlockData(buf.readVarInt());
        this.blockData = blockData != null ? blockData : DEFAULT_LOOK.createBlockData();
    }

    @Override
    public NBTCompound toNBT() {
        if (blockData.getMaterial() == null)
            return new NBTCompound(Map.of("Name", DEFAULT_LOOK.getName().toString()));
        return new NBTCompound(Map.of(
                "Name", blockData.getMaterial().getName().toString()
                // TODO Full block data support (properties)
        ));
    }

    @Override
    public void write(final ServerBuffer buf) {
        buf.writeVarInt(blockData.getId());
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public BlockOptions clone() {
        final ServerBuffer buf = new FriendlyByteBuf().write(this);
        return new BlockOptionsImpl(buf);
    }
}
