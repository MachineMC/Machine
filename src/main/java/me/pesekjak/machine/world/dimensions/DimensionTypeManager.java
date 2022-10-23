package me.pesekjak.machine.world.dimensions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.server.ServerProperty;
import me.pesekjak.machine.server.codec.CodecPart;
import me.pesekjak.machine.utils.NamespacedKey;
import org.jglrxavpok.hephaistos.nbt.NBT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages multiple dimension types of the server, each dimension type
 * has to reference manager it was created for.
 */
@RequiredArgsConstructor
public class DimensionTypeManager implements CodecPart, ServerProperty {

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
    public static DimensionTypeManager createDefault(Machine server) {
        DimensionTypeManager manager = new DimensionTypeManager(server);
        manager.addDimension(DimensionType.createDefault());
        return manager;
    }

    /**
     * Registers the dimension type to this manager if it's not registered already
     * in a different one.
     * @param dimensionType dimension type to register
     */
    public void addDimension(DimensionType dimensionType) {
        if(dimensionType.getManager().get() != null && dimensionType.getManager().get() != this)
            throw new IllegalStateException("Dimension type '" + dimensionType.getName() + "' is already registered in a different DimensionManager");
        dimensionType.getManager().set(this);
        dimensionType.id.set(ID_COUNTER.getAndIncrement());
        dimensionTypes.add(dimensionType);
    }

    /**
     * Removes the dimension type from the manager if it's registered in this manager.
     * @param dimensionType dimension type that should be removed
     * @return true if the dimension type was removed successfully
     */
    public boolean removeDimension(DimensionType dimensionType) {
        if(dimensionType.getManager().get() != this) return false;
        if(dimensionTypes.remove(dimensionType)) {
            dimensionType.getManager().set(null);
            dimensionType.id.set(-1);
            return true;
        }
        return false;
    }

    /**
     * Checks if the dimension type with given name exists.
     * @param name name of the dimension type
     * @return true if the dimension type exists
     */
    public boolean isRegistered(NamespacedKey name) {
        return isRegistered(getDimension(name));
    }

    /**
     * Checks if the dimension type is registered in this manager.
     * @param dimensionType dimension type to check for
     * @return true if the dimension type is registered in this manager
     */
    public boolean isRegistered(DimensionType dimensionType) {
        return dimensionTypes.contains(dimensionType);
    }

    /**
     * Searches for registered dimension type with the given name in this manager.
     * @param name name of the dimension type to search for
     * @return dimension type with the given name
     */
    public DimensionType getDimension(NamespacedKey name) {
        for(DimensionType dimensionType : getDimensions()) {
            if(!(dimensionType.getName().equals(name))) continue;
            return dimensionType;
        }
        return null;
    }

    /**
     * Collection of all registered dimension types in this manager
     * @return collection of all registered dimension types
     */
    public Set<DimensionType> getDimensions() {
        return Collections.unmodifiableSet(dimensionTypes);
    }

    @Override
    public String getCodecType() {
        return CODEC_TYPE;
    }

    @Override
    public List<NBT> getCodecElements() {
        return new ArrayList<>(dimensionTypes.stream()
                .map(DimensionType::toNBT)
                .toList());
    }

}
