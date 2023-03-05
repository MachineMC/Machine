package org.machinemc.api.server.codec;

import org.machinemc.nbt.NBTCompound;
import org.machinemc.nbt.NBTList;

import java.util.List;
import java.util.Map;

/**
 * Part of Minecraft's codec.
 */
public interface CodecPart {

    /**
     * @return String identifier of the codec part
     */
    String getCodecType();

    /**
     * @return NBT values of the codec
     */
    List<NBTCompound> getCodecElements();

    /**
     * Builds the full NBTCompound of the codec type and its elements.
     * @return full NBTCompound of the codec part
     */
    default NBTCompound getCodecNBT() {
        return new NBTCompound(Map.of(
                "type", getCodecType(),
                "value", new NBTList(getCodecElements())
        ));
    }

}
