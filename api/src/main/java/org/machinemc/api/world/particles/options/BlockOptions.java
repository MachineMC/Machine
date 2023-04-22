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
package org.machinemc.api.world.particles.options;

import org.machinemc.api.world.BlockData;
import org.machinemc.api.world.particles.ParticleOptions;

/**
 * Options used by {@link org.machinemc.api.world.particles.ParticleType#BLOCK}.
 */
public interface BlockOptions extends ParticleOptions {

    /**
     * @return block data used by the block particle
     */
    BlockData getBlockData();

    /**
     * Changes the block data used by the block particle.
     * @param blockData new block data
     */
    void setBlockData(BlockData blockData);

}
