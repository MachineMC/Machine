package me.pesekjak.machine.server.codec;

import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTType;

import java.util.List;
import java.util.Map;

public interface CodecPart {

    String getCodecType();

    List<NBT> getCodecElements();

    default NBTCompound getCodecNBT() {
        return NBT.Compound(Map.of(
                "type", NBT.String(getCodecType()),
                "value", NBT.List(NBTType.TAG_Compound, getCodecElements())
        ));
    }

}
