package me.pesekjak.machine.world;

import com.google.common.base.Objects;

import java.util.Arrays;

// This class is used by the code generators, edit with caution.
public class BlockData implements Cloneable {

    private Material material;
    private int id;

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

    @Override
    public BlockData clone() throws CloneNotSupportedException {
        return (BlockData) super.clone();
    }

    @Override
    public String toString() {
        if(getMaterial() == null) return "none[]";
        return getMaterial().getName().getKey() + Arrays.toString(getData());
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
