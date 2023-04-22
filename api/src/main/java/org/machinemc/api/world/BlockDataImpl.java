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
package org.machinemc.api.world;

import com.google.common.collect.Iterables;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 * Default block data implementation without any special
 * properties.
 */
// This class is used by the code generators, edit with caution.
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlockDataImpl extends BlockData {

    private static final Map<Integer, BlockDataImpl> REGISTRY = new TreeMap<>();
    private static BlockDataImpl[] registryArray = new BlockDataImpl[0];

    private Material material;
    private int id;

    /**
     * Finishes the registration of the block data to materials.
     */
    public static void finishRegistration() {
        registryArray = new BlockDataImpl[Iterables.getLast(REGISTRY.keySet()) + 1];
        for (final Integer stateId : REGISTRY.keySet())
            registryArray[stateId] = REGISTRY.get(stateId);
    }

    /**
     * Returns new instance of block data from id (mapped by vanilla server reports).
     * @param id id of the block data
     * @return new instance of the block data with the given id
     */
    public static @Nullable BlockData getBlockData(final int id) {
        if (id == -1) return null;
        if (registryArray.length <= id) return null;
        final BlockDataImpl data = registryArray[id];
        if (data == null) return null;
        return data.clone();
    }

    /**
     * Returns id of the block data.
     * @param blockData block data to get id from
     * @return id of the given block data
     */
    public static int getId(final BlockData blockData) {
        return blockData.getId();
    }

    protected BlockDataImpl(final int id) {
        this.id = id;
    }

    @Override
    protected BlockDataImpl setMaterial(final Material material) {
        this.material = material;
        final Map<Integer, BlockData> stateMap = getIdMap();
        for (final Integer stateId : stateMap.keySet()) {
            final BlockData data = stateMap.get(stateId).clone();
            if (!(data instanceof BlockDataImpl blockData))
                throw new IllegalStateException();
            blockData.material = material;
            REGISTRY.put(stateId, blockData);
        }
        return this;
    }

    @Override
    protected Object[] getData() {
        return new Object[0];
    }

    /**
     * @return all block data states for material of this block data mapped to ids
     */
    protected @Unmodifiable Map<Integer, BlockData> getIdMap() {
        return Map.of(id, this);
    }

}
