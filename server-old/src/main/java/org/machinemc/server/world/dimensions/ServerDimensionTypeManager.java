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
package org.machinemc.server.world.dimensions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.machinemc.api.Server;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.dimensions.DimensionType;
import org.machinemc.api.world.dimensions.DimensionTypeManager;
import org.machinemc.nbt.NBTCompound;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.machinemc.api.chunk.Chunk.CHUNK_SECTION_SIZE;

/**
 * Default implementation of the dimension manager.
 */
@RequiredArgsConstructor
public class ServerDimensionTypeManager implements DimensionTypeManager {

    protected final AtomicInteger idCounter = new AtomicInteger(0);
    private static final String CODEC_TYPE = "minecraft:dimension_type";

    private final Map<Integer, DimensionType> dimensionTypes = new ConcurrentHashMap<>();
    @Getter
    private final Server server;

    /**
     * Creates dimension manager with default values.
     * @param server server
     * @return new manager
     */
    public static DimensionTypeManager createDefault(final Server server) {
        final ServerDimensionTypeManager manager = new ServerDimensionTypeManager(server);
        manager.addDimension(ServerDimensionType.createDefault());
        return manager;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void addDimension(final DimensionType dimensionType) {
        Objects.requireNonNull(dimensionType, "Dimension type can not be null");
        if (isRegistered(dimensionType.getName()))
            throw new IllegalStateException("Dimension type '" + dimensionType.getName() + "' is already registered");
        if (dimensionType.getMinY() % CHUNK_SECTION_SIZE != 0
                || dimensionType.getHeight() % CHUNK_SECTION_SIZE != 0
                || dimensionType.getLogicalHeight() % CHUNK_SECTION_SIZE != 0)
            throw new IllegalStateException("Dimension type height levels has to be multiple of 16");
        if (dimensionType.getHeight() < 0 || dimensionType.getHeight() > 4064)
            throw new IllegalStateException("Dimension type height has to be between -2032 and 2016");
        if (dimensionType.getHeight() < dimensionType.getLogicalHeight())
            throw new IllegalStateException("Logical height of dimension type can't be higher than its height");
        if (dimensionType.getMinY() < -2032 || dimensionType.getMinY() > 2016)
            throw new IllegalStateException("Dimension type minimal Y level has to be between -2032 and 2016");
        dimensionTypes.put(idCounter.getAndIncrement(), dimensionType);
    }

    @Override
    public boolean removeDimension(final DimensionType dimensionType) {
        Objects.requireNonNull(dimensionType, "Dimension type can not be null");
        return dimensionTypes.remove(getDimensionID(dimensionType)) == null;
    }

    @Override
    public boolean isRegistered(final DimensionType dimensionType) {
        Objects.requireNonNull(dimensionType, "Dimension type can not be null");
        return dimensionTypes.containsValue(dimensionType);
    }

    @Override
    public Optional<DimensionType> getDimension(final NamespacedKey name) {
        for (final DimensionType dimensionType : getDimensions()) {
            if (!(dimensionType.getName().equals(name))) continue;
            return Optional.of(dimensionType);
        }
        return Optional.empty();
    }

    @Override
    public Optional<DimensionType> getByID(final int id) {
        return Optional.ofNullable(dimensionTypes.get(id));
    }

    @Override
    public int getDimensionID(final DimensionType dimensionType) {
        Objects.requireNonNull(dimensionType, "Dimension type can not be null");
        for (final Map.Entry<Integer, DimensionType> entry : dimensionTypes.entrySet()) {
            if (entry.getValue().equals(dimensionType))
                return entry.getKey();
        }
        return -1;
    }

    @Override
    public Set<DimensionType> getDimensions() {
        return dimensionTypes.values().stream().collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public NBTCompound getDimensionNBT(final DimensionType dimensionType) {
        Objects.requireNonNull(dimensionType, "Dimension type can not be null");
        if (!isRegistered(dimensionType))
            throw new IllegalStateException();
        final NBTCompound nbtCompound = dimensionType.toNBT();
        return new NBTCompound(Map.of(
                "name", dimensionType.getName().toString(),
                "id", getDimensionID(dimensionType),
                "element", nbtCompound
        ));
    }

    @Override
    public String getCodecType() {
        return CODEC_TYPE;
    }

    @Override
    public List<NBTCompound> getCodecElements() {
        return dimensionTypes.values().stream()
                .map(this::getDimensionNBT)
                .toList();
    }

    @Override
    public String toString() {
        return "ServerDimensionTypeManager("
                + "server=" + server
                + ')';
    }

}
