package org.machinemc.server.chunk.palette;

import lombok.RequiredArgsConstructor;
import org.machinemc.api.chunk.palette.Palette;
import org.machinemc.api.utils.ServerBuffer;

/**
 * Palette fully filled with one single value, can be used
 * as empty of singe-value fully filled palette.
 */
@RequiredArgsConstructor
public class FilledPalette implements Palette {

    protected final byte dimension;
    protected final int value;

    @Override
    public int get(int x, int y, int z) {
        return value;
    }

    @Override
    public void getAll(EntryConsumer consumer) {
        final byte dimension = this.dimension;
        final int value = this.value;
        for (byte x = 0; x < dimension; x++)
            for (byte y = 0; y < dimension; y++)
                for (byte z = 0; z < dimension; z++)
                    consumer.accept(x, y, z, value);
    }

    @Override
    public void getAllPresent(EntryConsumer consumer) {
        if (value != 0) getAll(consumer);
    }

    @Override
    public void set(int x, int y, int z, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fill(int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAll(EntrySupplier supplier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void replace(int x, int y, int z, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void replaceAll(EntryFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int count() {
        return maxSize();
    }

    @Override
    public int bitsPerEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int maxBitsPerEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int dimension() {
        return dimension;
    }

    @Override
    public Palette clone() {
        try {
            return (Palette) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public void write(ServerBuffer buf) {
        buf.writeByte((byte) 0);
        buf.writeVarInt(value);
        buf.writeVarInt(0);
    }

}
