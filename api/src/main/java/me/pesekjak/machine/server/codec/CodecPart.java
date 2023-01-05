package me.pesekjak.machine.server.codec;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTType;

import java.util.List;
import java.util.Map;

/**
 * Part of Minecraft's codec.
 */
public interface CodecPart {

    /**
     * @return String identifier of the codec part
     */
    @NotNull @NonNls String getCodecType();

    /**
     * @return NBT values of the codec
     */
    @NotNull List<NBT> getCodecElements();

    /**
     * Builds the full NBTCompound of the codec type and its elements.
     * @return full NBTCompound of the codec part
     */
    default @NotNull NBTCompound getCodecNBT() {
        return NBT.Compound(Map.of(
                "type", NBT.String(getCodecType()),
                "value", NBT.List(NBTType.TAG_Compound, getCodecElements())
        ));
    }

}
