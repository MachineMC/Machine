package me.pesekjak.machine.world;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * BlockData created from {@link Material}, to create a new instance
 * use {@link Material#createBlockData()}, each BlockData will
 * return unique id depending on its material and properties.
 */
// This class is used by the code generators, edit with caution.
public class BlockData implements Cloneable {

    private static final Map<Integer, BlockData> REGISTRY = new TreeMap<>();
    private static BlockData[] REGISTRY_ARRAY = new BlockData[0];

    private Material material;
    private int id;

    public static void finishRegistration() {
        Integer size = Iterables.getLast(REGISTRY.keySet());
        REGISTRY_ARRAY = new BlockData[++size];
        for(Integer stateId : REGISTRY.keySet())
            REGISTRY_ARRAY[stateId] = REGISTRY.get(stateId);
    }

    /**
     * Returns new instance of BlockData from id (mapped by vanilla server reports).
     * @param id id of the BlockData
     * @return new instance of the BlockData with the id
     */
    public static BlockData getBlockData(int id) {
        if(id == -1) return null;
        if(REGISTRY_ARRAY.length <= id) return null;
        BlockData data = REGISTRY_ARRAY[id];
        if(data == null) return null;
        return data.clone();
    }

    /**
     * Returns id of the BlockData
     * @param blockData BlockData to get id from
     * @return id of the BlockData
     */
    public static int getId(BlockData blockData) {
        return blockData.getId();
    }

    protected BlockData(int id) {
        this.id = id;
    }

    protected BlockData() {
    }

    /**
     * @return Material associated with this BlockData
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Changes Material associated with this BlockData,
     * should be used only internally, it also re-adds all states of the
     * current BlockData to registry.
     * @param material new material
     */
    protected BlockData setMaterial(Material material) {
        this.material = material;
        Map<Integer, BlockData> stateMap = getIdMap();
        for (Integer stateId : stateMap.keySet()) {
            BlockData data = stateMap.get(stateId).clone();
            data.material = material;
            REGISTRY.put(stateId, data);
        }
        return this;
    }

    /**
     * @return id of the material mapped by vanilla server.
     */
    public int getId() {
        return id;
    }

    /**
     * @return all data used by this BlockData (used for hashing)
     */
    protected Object[] getData() {
        return new Object[0];
    }

    /**
     * @return blockdata for material of this blockdata mapped to ids
     */
    protected Map<Integer, BlockData> getIdMap() {
        return Map.of(id, this);
    }

    @Override
    public BlockData clone() {
        try {
            return (BlockData) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        if(getMaterial() != null)
            return getMaterial().getName().getKey() + Arrays.toString(getData());
        return "none" + Arrays.toString(getData());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockData blockData)) return false;
        if(material != blockData.material) return false;
        Object[] original = getData();
        Object[] compare = blockData.getData();
        if(original.length != compare.length) return false;
        for(int i = 0; i < original.length; i++) {
            if(original[i] != compare[i]) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(material, getData());
    }

}
