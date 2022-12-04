package me.pesekjak.machine.world.dimensions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.utils.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import static me.pesekjak.machine.chunk.Chunk.CHUNK_SECTION_SIZE;

/**
 * Manages multiple dimension types of the server, each dimension type
 * has to reference manager it was created for.
 */
@RequiredArgsConstructor
public class DimensionTypeManagerImpl implements DimensionTypeManager {

    protected final AtomicInteger ID_COUNTER = new AtomicInteger(0);
    private final static String CODEC_TYPE = "minecraft:dimension_type";

    private final Set<DimensionType> dimensionTypes = new CopyOnWriteArraySet<>();
    @Getter
    private final Machine server;

    /**
     * Creates manager with default settings.
     * @param server server to create manager for
     * @return newly created manager
     */
    public static @NotNull DimensionTypeManagerImpl createDefault(Machine server) {
        DimensionTypeManagerImpl manager = new DimensionTypeManagerImpl(server);
        manager.addDimension(DimensionTypeImpl.createDefault());
        return manager;
    }

    /**
     * Registers the dimension type to this manager if it's not registered already
     * in a different one.
     * @param dimensionType dimension type to register
     */
    public void addDimension(@NotNull DimensionType dimensionType) {
        if(dimensionType.getManagerReference().get() != null && dimensionType.getManagerReference().get() != this)
            throw new IllegalStateException("Dimension type '" + dimensionType.getName() + "' is already registered in a different DimensionManager");
        if(dimensionType.getMinY() % CHUNK_SECTION_SIZE != 0 || dimensionType.getHeight() % CHUNK_SECTION_SIZE != 0 || dimensionType.getLogicalHeight() % CHUNK_SECTION_SIZE != 0)
            throw new IllegalStateException("Dimension type height levels has to be multiple of 16");
        if(dimensionType.getHeight() < 0 || dimensionType.getHeight() > 4064)
            throw new IllegalStateException("Dimension type height has to be between -2032 and 2016");
        if(dimensionType.getHeight() < dimensionType.getLogicalHeight())
            throw new IllegalStateException("Logical height of dimension type can't be higher than its height");
        if(dimensionType.getMinY() < -2032 || dimensionType.getMinY() > 2016)
            throw new IllegalStateException("Dimension type minimal Y level has to be between -2032 and 2016");

        dimensionType.getManagerReference().set(this);
        dimensionType.getIdReference().set(ID_COUNTER.getAndIncrement());
        dimensionTypes.add(dimensionType);
    }

    /**
     * Removes the dimension type from the manager if it's registered in this manager.
     * @param dimensionType dimension type that should be removed
     * @return true if the dimension type was removed successfully
     */
    public boolean removeDimension(@NotNull DimensionType dimensionType) {
        if(dimensionType.getManagerReference().get() != this) return false;
        if(dimensionTypes.remove(dimensionType)) {
            dimensionType.getManagerReference().set(null);
            dimensionType.getIdReference().set(-1);
            return true;
        }
        return false;
    }

    /**
     * Checks if the dimension type with given name exists.
     * @param name name of the dimension type
     * @return true if the dimension type exists
     */
    public boolean isRegistered(@NotNull NamespacedKey name) {
        final DimensionType dimension = getDimension(name);
        if(dimension == null) return false;
        return isRegistered(dimension);
    }

    /**
     * Checks if the dimension type is registered in this manager.
     * @param dimensionType dimension type to check for
     * @return true if the dimension type is registered in this manager
     */
    public boolean isRegistered(DimensionTypeImpl dimensionType) {
        return dimensionTypes.contains(dimensionType);
    }

    /**
     * Searches for registered dimension type with the given name in this manager.
     * @param name name of the dimension type to search for
     * @return dimension type with the given name
     */
    public DimensionType getDimension(@NotNull NamespacedKey name) {
        for(DimensionType dimensionType : getDimensions()) {
            if(!(dimensionType.getName().equals(name))) continue;
            return dimensionType;
        }
        return null;
    }

    /**
     * Searches for registered dimension type with the given id in this manager.
     * @param id id of the dimension type to search for
     * @return dimension type with the given id
     */
    public @Nullable DimensionType getById(int id) {
        for(DimensionType dimensionType : getDimensions()) {
            if (dimensionType.getIdReference().get() != id) continue;
            return dimensionType;
        }
        return null;
    }

    /**
     * Collection of all registered dimension types in this manager
     * @return collection of all registered dimension types
     */
    public @NotNull Set<DimensionType> getDimensions() {
        return Collections.unmodifiableSet(dimensionTypes);
    }

    @Override
    public @NotNull String getCodecType() {
        return CODEC_TYPE;
    }

    @Override
    public @NotNull List<NBT> getCodecElements() {
        return new ArrayList<>(dimensionTypes.stream()
                .map(DimensionType::toNBT)
                .toList());
    }

}
