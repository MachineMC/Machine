package me.pesekjak.machine.world;

import com.google.common.base.Objects;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

// This class is used by the code generators, edit with caution.
public class BlockData implements Cloneable {

    private static final Map<Integer, BlockData> REGISTRY = new HashMap<>();

    private Material material;
    private int id;

    public static BlockData getBlockData(int id) {
        return REGISTRY.getOrDefault(id, new BlockData(id)).clone();
    }

    public static int getId(BlockData blockData) {
        return blockData.getId();
    }

    @SuppressWarnings("unchecked")
    private static void loadStates(Class<?> blockDataClass, @Nullable Material material) {
        try {
            BlockData defaultBlockData;
            Constructor<?> constructor = blockDataClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            defaultBlockData = (BlockData) constructor.newInstance();
            defaultBlockData.setMaterial(material);
            constructor.setAccessible(false);

            Field[] fields = blockDataClass.getDeclaredFields();
            for(Field field : fields)
                field.setAccessible(true);

            HashMap<String, Integer> idMap = (HashMap<String, Integer>) fields[0].get(null);

            for(String key : idMap.keySet()) {
                BlockData blockData = defaultBlockData.clone();
                String[] data = key.split(";");
                for(int i = 0; i < data.length; i++) {
                    String value = data[i];
                    Field field = fields[i+1];
                    if(field.getType() == int.class) {
                        field.set(blockData, Integer.parseInt(value));
                    } else if(field.getType() == boolean.class) {
                        field.set(blockData, Boolean.parseBoolean(value));
                    } else {
                        Object custom = field.getType()
                                .getDeclaredField(value.toUpperCase()).get(null);
                        field.set(blockData, custom);
                    }
                }
                REGISTRY.put(idMap.get(key), blockData);
            }

            for(Field field : fields)
                field.setAccessible(false);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    protected BlockData(int id) {
        this.id = id;
    }

    protected BlockData() {
    }

    public Material getMaterial() {
        return material;
    }

    protected void setMaterial(Material material) {
        this.material = material;
        register();
    }

    public int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    protected Object[] getData() {
        return new Object[0];
    }

    private void register() {
        if(getMaterial() == null) return;
        if(REGISTRY.containsKey(getId())) return;
        REGISTRY.put(getId(), this);
        if(getClass() != BlockData.class)
            loadStates(getClass(), getMaterial());
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
