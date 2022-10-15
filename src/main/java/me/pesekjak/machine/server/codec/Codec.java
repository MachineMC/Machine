package me.pesekjak.machine.server.codec;

import lombok.NoArgsConstructor;
import me.pesekjak.machine.server.NBTSerializable;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class Codec implements NBTSerializable {

    private final List<CodecPart> codecParts = new ArrayList<>();

    public Codec(CodecPart... codecParts) {
        this.codecParts.addAll(List.of(codecParts));
    }

    @Override
    public NBTCompound toNBT() {
        Map<String, NBTCompound> parts = new LinkedHashMap<>();
        for(CodecPart part : codecParts)
            parts.put(part.getCodecType(), part.getCodecNBT());
        return new NBTCompound(parts);
    }

}
