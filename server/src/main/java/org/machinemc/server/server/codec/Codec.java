package org.machinemc.server.server.codec;

import lombok.NoArgsConstructor;
import org.machinemc.api.server.NBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.machinemc.api.server.codec.CodecPart;
import org.machinemc.nbt.NBTCompound;

import java.util.ArrayList;
import java.util.List;

/**
 * Codec containing NBTCompound representing certain registries
 * that are sent from the server and are applied on the client.
 */
@NoArgsConstructor
public class Codec implements NBTSerializable {

    private final List<CodecPart> codecParts = new ArrayList<>();

    public Codec(CodecPart... codecParts) {
        this.codecParts.addAll(List.of(codecParts));
    }

    @Override
    public @NotNull NBTCompound toNBT() {
        NBTCompound compound = new NBTCompound();
        for(CodecPart part : codecParts)
            compound.set(part.getCodecType(), part.getCodecNBT());
        return compound;
    }

}
