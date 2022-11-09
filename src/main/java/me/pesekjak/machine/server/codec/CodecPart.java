package me.pesekjak.machine.server.codec;

import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTType;

import java.util.List;
import java.util.Map;

public interface CodecPart {

    /**
     * @return String identifier of the codec part
     */
    String getCodecType();

    /**
     * @return NBT values of the codec
     */
    List<NBT> getCodecElements();

    /**
     * Builds the full NBTCompound of the codec type and its elements.
     * @return full NBTCompound of the codec part
     */
    default NBTCompound getCodecNBT() {
        return NBT.Compound(Map.of(
                "type", NBT.String(getCodecType()),
                "value", NBT.List(NBTType.TAG_Compound, getCodecElements())
        ));
    }

}
