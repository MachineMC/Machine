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
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 * Default block data implementation without any special
 * properties.
 */
// This class is used by the code generators, edit with caution.
non-sealed class BlockDataImpl extends BlockData {

    private static final Map<Integer, BlockDataImpl> TEMP_REGISTRY = new TreeMap<>();
    private static BlockDataImpl[] registryArray;

    @Getter
    private Material material;
    private final int id;

    /**
     * Returns new instance of block data from id (mapped by vanilla server reports).
     * @param id id of the block data
     * @return new instance of the block data with the given id
     */
    public static Optional<BlockData> getBlockData(final int id) {
        if (id < 0) return Optional.empty();
        if (registryArray.length <= id) return Optional.empty();
        final BlockDataImpl data = registryArray[id];
        if (data == null) return Optional.empty();
        if (data.id != -1) return Optional.ofNullable(data.clone());

        final BlockDataImpl clone = (BlockDataImpl) data.clone();
        final Object[][] available = clone.getAcceptedProperties();
        final int[] weights = new int[available.length];

        for (int i = 0; i < weights.length; i++) {
            int weight = 1;
            for (int j = i + 1; j < available.length; j++)
                weight *= available[j].length;
            weights[i] = weight;
        }

        final Object[] newData = new Object[available.length];
        for (int i = 0; i < newData.length; i++)
            newData[i] = available[i][0];

        int diff = id - clone.firstStateID();
        int i = 0;
        while (diff != 0) {
            final Object[] properties = available[i];
            for (final Object property : properties) {
                newData[i] = property;
                diff -= weights[i];
                if (diff < 0) {
                    diff += weights[i];
                    break;
                }
            }
            i++;
        }

        clone.loadProperties(newData);
        return Optional.of(clone);
    }

    /**
     * Parses the block data text from {@link BlockData#toString()}.
     * <p>
     * Returns null in case the provided text doesn't match any
     * block data loaded by the server.
     * @param text text to parse
     * @return parsed block data or null if the text is invalid
     */
    public static Optional<BlockData> parse(final String text) {
        final String lowercase = text.toLowerCase();

        final StringBuilder materialName = new StringBuilder();
        for (int i = 0; i < lowercase.length(); i++) {
            final char next = lowercase.charAt(i);
            if (next == '[') break;
            materialName.append(next);
        }

        final Material material;
        try {
            material = Material.valueOf(materialName.toString()
                    .replace("minecraft:", "")
                    .toUpperCase());
        } catch (Throwable throwable) {
            return Optional.empty();
        }
        final BlockDataImpl blockData = (BlockDataImpl) material.createBlockData();

        if (blockData == null) return Optional.empty();
        if (blockData.getData().length == 0) return Optional.of(blockData);
        if (text.length() == materialName.length()) return Optional.of(blockData);

        final StringBuilder propertiesText = new StringBuilder();
        for (int i = materialName.length() + 1; i < lowercase.length(); i++) {
            final char next = lowercase.charAt(i);
            if (next == ']') break;
            propertiesText.append(next);
        }

        final Map<String, String> properties = new LinkedHashMap<>(blockData.getDataMap());
        for (final String property : propertiesText.toString().replace(" ", "").split(",")) {
            if (property.length() == 0) continue;
            final String[] keyAndValue = property.split("=");
            if (!properties.containsKey(keyAndValue[0])) continue;
            properties.replace(keyAndValue[0], keyAndValue[1]);
        }

        final Object[] data = new Object[properties.size()];
        final Object[][] available = blockData.getAcceptedProperties();

        int i = 0;
        for (final String property : properties.values()) {
            Object matched = available[i][0];
            for (final Object availableProperty : available[i]) {
                if (!availableProperty.toString().toLowerCase().matches(property)) continue;
                matched = availableProperty;
                break;
            }
            data[i] = matched;
            i++;
        }

        blockData.loadProperties(data);
        return Optional.of(blockData);
    }

    /**
     * Finishes the registration of the block data to materials.
     */
    public static void finishRegistration() {
        canRegister();
        registryArray = new BlockDataImpl[Iterables.getLast(TEMP_REGISTRY.keySet()) + 1];
        for (final Integer stateID : TEMP_REGISTRY.keySet())
            registryArray[stateID] = TEMP_REGISTRY.get(stateID);
        TEMP_REGISTRY.clear();
    }

    protected BlockDataImpl(final int id) {
        this.id = id;
    }

    protected BlockDataImpl() {
        this(-1);
    }

    @Override
    protected void register() {
        canRegister();
        if (id != -1) {
            TEMP_REGISTRY.put(id, this);
            return;
        }

        int id = firstStateID();
        final Object[][] available = getAcceptedProperties();

        for (int i = 0; i < available.length; i++) {
            int weight = 1;
            for (int j = i + 1; j < available.length; j++)
                weight *= available[j].length;
            id += weight * (available[i].length - 1);
        }

        for (int i = firstStateID(); i < id; i++) TEMP_REGISTRY.put(i, this);
    }

    /**
     * Checks whether registrations of new block data are allowed.
     * @throws UnsupportedOperationException if registrations had already finished
     */
    private static void canRegister() {
        if (registryArray == null) return;
        throw new UnsupportedOperationException("Registration has been already finished");
    }

    @Override
    protected BlockDataImpl setMaterial(final Material material) {
        this.material = material;
        return this;
    }

    @Override
    public int getID() {
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

    /**
     * Loads properties to the block data, their order has to match
     * the order of their keys, {@link BlockDataImpl#getDataNames()}.
     * The array needs to provide all properties with correct types.
     * @param properties properties to load
     */
    protected void loadProperties(final Object[] properties) {

    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getMaterial().getName().toString());
        builder.append('[');
        final int size = getData().length;
        int i = 0;
        for (final Map.Entry<String, String> entry : getDataMap().entrySet()) {
            i++;
            builder.append(entry.getKey()).append("=").append(entry.getValue());
            if (i == size) break;
            builder.append(", ");
        }
        builder.append(']');
        return builder.toString();
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
