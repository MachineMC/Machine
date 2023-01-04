package me.pesekjak.machine.server.codec;

import lombok.NoArgsConstructor;
import me.pesekjak.machine.server.NBTSerializable;
import mx.kenzie.nbt.NBTCompound;
import org.jetbrains.annotations.NotNull;

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
