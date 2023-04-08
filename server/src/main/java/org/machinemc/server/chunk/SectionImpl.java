package org.machinemc.server.chunk;

import lombok.*;
import org.machinemc.api.chunk.Chunk;
import org.machinemc.api.chunk.Section;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.server.chunk.palette.AdaptivePalette;
import org.machinemc.api.chunk.palette.Palette;
import org.machinemc.api.utils.ServerBuffer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Default implementation of the section.
 */
@Getter
public class SectionImpl implements Section {

    private final Chunk source;
    private final int index;

    private final Palette blockPalette;
    private final Palette biomePalette;
    @Setter
    private byte[] skyLight;
    @Setter
    private byte[] blockLight;

    private final Supplier<NBTCompound> dataSupplier;

    private final Map<Integer, BlockEntity> clientBlockEntities = new ConcurrentHashMap<>();

    public SectionImpl(final Chunk source, final int index, final Supplier<NBTCompound> dataSupplier) {
        this.source = source;
        this.index = index;
        blockPalette = AdaptivePalette.blocks();
        biomePalette = AdaptivePalette.biomes();
        skyLight = new byte[0];
        blockLight = new byte[0];
        this.dataSupplier = dataSupplier;
    }

    @Override
    public Map<Integer, BlockEntity> getClientBlockEntities() {
        return clientBlockEntities;
    }

    @Override
    @Synchronized
    public NBTCompound getData() {
        return dataSupplier.get().clone();
    }

    @Override
    @Synchronized
    public void mergeData(NBTCompound compound) {
        dataSupplier.get().putAll(compound);
    }

    @Override
    @Synchronized
    public void setData(NBTCompound compound) {
        final NBTCompound original = dataSupplier.get();
        original.clear();
        original.putAll(compound);
    }

    @Override
    public void clear() {
        this.blockPalette.fill(0);
        this.biomePalette.fill(0);
        this.skyLight = new byte[0];
        this.blockLight = new byte[0];
    }

    @Override
    public void write(ServerBuffer buf) {
        buf.writeShort((short) blockPalette.count());
        blockPalette.write(buf);
        biomePalette.write(buf);
    }

}
