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
package org.machinemc.api.world;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 * Default block data implementation without any special
 * properties.
 */
// This class is used by the code generators, edit with caution.
class BlockDataImpl extends BlockData {

    private static final Map<Integer, BlockDataImpl> REGISTRY = new TreeMap<>();
    private static BlockDataImpl[] registryArray = new BlockDataImpl[0];

    @Getter
    private Material material;
    private final int id;

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
        if (id < 0) return null;
        if (registryArray.length <= id) return null;
        final BlockDataImpl data = registryArray[id];
        if (data == null) return null;
        return data.clone();
    }

    protected BlockDataImpl(final int id) {
        this.id = id;
    }

    protected BlockDataImpl() {
        this(-1);
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

    /**
     * Returns id of the block data.
     * @param blockData block data to get id from
     * @return id of the given block data
     */
    public static int getId(final BlockData blockData) {
        return blockData.getId();
    }

    @Override
    public int getId() {
        if (id != -1) return id;

        final Object[][] available = getAcceptedProperties();
        final int[] weights = new int[available.length];

        for (int i = 0; i < weights.length; i++) {
            int weight = 1;
            for (int j = i + 1; j < available.length; j++)
                weight *= available[j].length;
            weights[i] = weight;
        }

        final Object[] data = getData();
        int id = firstStateID();

        for (int i = 0; i < data.length; i++) {
            int index = -1;
            for (int j = 0; j < available[i].length; j++) {
                if (available[i][j] != data[i]) continue;
                index = j;
                break;
            }
            assert index != -1;
            id += weights[i] * index;
        }

        return id;
    }

    @Override
    public @Unmodifiable Map<String, String> getDataMap() {
        final Map<String, String> map = new LinkedHashMap<>();
        final String[] names = getDataNames();
        final Object[] data = getData();
        assert names.length == data.length;
        for (int i = 0; i < names.length; i++)
            map.put(names[i], data[i].toString().toLowerCase());
        return Collections.unmodifiableMap(map);
    }

    /**
     * @return all block data states for material of this block data mapped to ids
     */
    protected @Unmodifiable Map<Integer, BlockData> getIdMap() {
        return Map.of(id, this);
    }

    /**
     * Returns all data used by the block data (block data properties)
     * in order of their names. {@link BlockDataImpl#getDataNames()}
     * @return block data properties
     */
    protected Object[] getData() {
        return new Object[0];
    }

    /**
     * Returns all data keys used by the block data (block data properties)
     * in order of Minecraft Protocol.
     * @return keys of this block data
     */
    protected String[] getDataNames() {
        return new String[0];
    }

    /**
     * Returns all accepted properties values of this block data.
     * <p>
     * They are also mentioned in the BlockData class itself and
     * marked with {@link PropertyRange}.
     * <p>
     * Their order needs to match the order in Minecraft Protocol;
     * meaning groups match the order of their keys, {@link BlockDataImpl#getDataNames()}.
     * <p>
     * Elements of each group are sorted by Minecraft Protocol.
     * @return accepted properties of this block data
     */
    protected Object[][] getAcceptedProperties() {
        return new Object[0][];
    }

    /**
     * Returns the id of the first state of this block data.
     * <p>
     * This is the lowest value of this block data.
     * @return id of first state
     */
    protected int firstStateID() {
        return id;
    }

    @Override
    public String toString() {
        if (getMaterial() != null)
            return getMaterial().getName().getKey() + Arrays.toString(getData());
        return "none" + Arrays.toString(getData());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockData blockData)) return false;
        if (getMaterial() != blockData.getMaterial()) return false;
        final Map<String, String> original = getDataMap();
        final Map<String, String> compare = getDataMap();
        if (original.size() != compare.size()) return false;
        return original.entrySet().stream()
                .allMatch(e -> e.getValue().equals(compare.get(e.getKey())));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getMaterial(), getData());
    }

}
