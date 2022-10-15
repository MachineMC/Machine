package me.pesekjak.machine.world.dimensions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.server.ServerProperty;
import me.pesekjak.machine.server.codec.CodecPart;
import me.pesekjak.machine.utils.NamespacedKey;
import org.jglrxavpok.hephaistos.nbt.NBT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RequiredArgsConstructor
public class DimensionTypeManager implements CodecPart, ServerProperty {

    private final static String CODEC_TYPE = "minecraft:dimension_type";

    private final List<DimensionType> dimensionTypes = new CopyOnWriteArrayList<>();
    @Getter
    private final Machine server;

    public static DimensionTypeManager createDefault(Machine server) {
        DimensionTypeManager manager = new DimensionTypeManager(server);
        manager.addDimension(DimensionType.OVERWORLD);
        return manager;
    }

    public void addDimension(DimensionType dimensionType) {
        if(!dimensionTypes.contains(dimensionType))
            dimensionTypes.add(dimensionType);
    }

    public boolean removeDimension(DimensionType dimensionType) {
        return dimensionTypes.remove(dimensionType);
    }

    public boolean isRegistered(NamespacedKey name) {
        return isRegistered(getDimension(name));
    }

    public boolean isRegistered(DimensionType dimensionType) {
        return dimensionTypes.contains(dimensionType);
    }


    public DimensionType getDimension(NamespacedKey name) {
        for(DimensionType dimensionType : getDimensions()) {
            if(!(dimensionType.getName().equals(name))) continue;
            return dimensionType;
        }
        return null;
    }

    public List<DimensionType> getDimensions() {
        return Collections.unmodifiableList(dimensionTypes);
    }

    @Override
    public String getCodecType() {
        return CODEC_TYPE;
    }

    @Override
    public List<NBT> getCodecElements() {
        return new ArrayList<>(dimensionTypes.stream()
                .map(DimensionType::toNBT)
                .toList());
    }

}
