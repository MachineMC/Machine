package org.machinemc.api.world;

import com.google.common.collect.Iterables;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 * Default block data implementation without any special
 * properties.
 */
// This class is used by the code generators, edit with caution.
@ApiStatus.Internal
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlockDataImpl extends BlockData {

    private static final Map<Integer, BlockDataImpl> REGISTRY = new TreeMap<>();
    private static BlockDataImpl[] REGISTRY_ARRAY = new BlockDataImpl[0];

    private @Nullable Material material;
    private int id;

    /**
     * Finishes the registration of the block data to materials.
     */
    public static void finishRegistration() {
        Integer size = Iterables.getLast(REGISTRY.keySet());
        REGISTRY_ARRAY = new BlockDataImpl[++size];
        for(Integer stateId : REGISTRY.keySet())
            REGISTRY_ARRAY[stateId] = REGISTRY.get(stateId);
    }

    /**
     * Returns new instance of block data from id (mapped by vanilla server reports).
     * @param id id of the block data
     * @return new instance of the block data with the given id
     */
    public static @Nullable BlockData getBlockData(int id) {
        if(id == -1) return null;
        if(REGISTRY_ARRAY.length <= id) return null;
        BlockDataImpl data = REGISTRY_ARRAY[id];
        if(data == null) return null;
        return data.clone();
    }

    /**
     * Returns id of the block data
     * @param blockData block data to get id from
     * @return id of the given block data
     */
    public static int getId(@NotNull BlockData blockData) {
        return blockData.getId();
    }

    protected BlockDataImpl(int id) {
        this.id = id;
    }

    @Override
    protected @NotNull BlockDataImpl setMaterial(@NotNull Material material) {
        this.material = material;
        Map<Integer, BlockData> stateMap = getIdMap();
        for (Integer stateId : stateMap.keySet()) {
            BlockData data = stateMap.get(stateId).clone();
            if(!(data instanceof BlockDataImpl blockData))
                throw new IllegalStateException();
            blockData.material = material;
            REGISTRY.put(stateId, blockData);
        }
        return this;
    }

    @Override
    protected Object @NotNull [] getData() {
        return new Object[0];
    }

    /**
     * @return all block data states for material of this block data mapped to ids
     */
    protected @Unmodifiable @NotNull Map<Integer, BlockData> getIdMap() {
        return Map.of(id, this);
    }

}
