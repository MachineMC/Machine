package org.machinemc.server.world.dimensions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.server.Machine;
import org.machinemc.api.utils.NamespacedKey;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.world.dimensions.DimensionType;
import org.machinemc.api.world.dimensions.DimensionTypeManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.machinemc.api.chunk.Chunk.CHUNK_SECTION_SIZE;

/**
 * Default implementation of the dimension manager.
 */
@RequiredArgsConstructor
public class DimensionTypeManagerImpl implements DimensionTypeManager {

    protected final AtomicInteger ID_COUNTER = new AtomicInteger(0);
    private final static String CODEC_TYPE = "minecraft:dimension_type";

    private final Map<Integer, DimensionType> dimensionTypes = new ConcurrentHashMap<>();
    @Getter
    private final Machine server;

    /**
     * Creates dimension manager with default values.
     * @param server server
     * @return new manager
     */
    public static DimensionTypeManager createDefault(Machine server) {
        DimensionTypeManagerImpl manager = new DimensionTypeManagerImpl(server);
        manager.addDimension(DimensionTypeImpl.createDefault());
        return manager;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void addDimension(DimensionType dimensionType) {
        if(isRegistered(dimensionType))
            throw new IllegalStateException("Dimension type '" + dimensionType.getName() + "' is already registered");
        if(dimensionType.getMinY() % CHUNK_SECTION_SIZE != 0 || dimensionType.getHeight() % CHUNK_SECTION_SIZE != 0 || dimensionType.getLogicalHeight() % CHUNK_SECTION_SIZE != 0)
            throw new IllegalStateException("Dimension type height levels has to be multiple of 16");
        if(dimensionType.getHeight() < 0 || dimensionType.getHeight() > 4064)
            throw new IllegalStateException("Dimension type height has to be between -2032 and 2016");
        if(dimensionType.getHeight() < dimensionType.getLogicalHeight())
            throw new IllegalStateException("Logical height of dimension type can't be higher than its height");
        if(dimensionType.getMinY() < -2032 || dimensionType.getMinY() > 2016)
            throw new IllegalStateException("Dimension type minimal Y level has to be between -2032 and 2016");
        dimensionTypes.put(ID_COUNTER.getAndIncrement(), dimensionType);
    }

    @Override
    public boolean removeDimension(DimensionType dimensionType) {
        return dimensionTypes.remove(getDimensionId(dimensionType)) == null;
    }

    @Override
    public boolean isRegistered(DimensionType dimensionType) {
        return dimensionTypes.containsValue(dimensionType);
    }

    @Override
    public DimensionType getDimension(NamespacedKey name) {
        for(DimensionType dimensionType : getDimensions()) {
            if(!(dimensionType.getName().equals(name))) continue;
            return dimensionType;
        }
        return null;
    }

    @Override
    public @Nullable DimensionType getById(int id) {
        return dimensionTypes.get(id);
    }

    @Override
    public int getDimensionId(DimensionType dimensionType) {
        for (Map.Entry<Integer, DimensionType> entry : dimensionTypes.entrySet()) {
            if (entry.getValue().equals(dimensionType))
                return entry.getKey();
        }
        return -1;
    }

    @Override
    public @NotNull Set<DimensionType> getDimensions() {
        return dimensionTypes.values().stream().collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public NBTCompound getDimensionNBT(DimensionType dimensionType) {
        NBTCompound nbtCompound = dimensionType.toNBT();
        return new NBTCompound(Map.of(
                "name", dimensionType.getName().toString(),
                "id", getDimensionId(dimensionType),
                "element", nbtCompound
        ));
    }

    @Override
    public String getCodecType() {
        return CODEC_TYPE;
    }

    @Override
    public @NotNull List<NBTCompound> getCodecElements() {
        return new ArrayList<>(dimensionTypes.values().stream()
                .map(DimensionType::toNBT)
                .toList());
    }

}
