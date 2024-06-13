/*
 * This file is part of Machine.
 *
 * Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Machine.
 * If not, see https://www.gnu.org/licenses/.
 */
package org.machinemc.server.chunk.palette;

import lombok.RequiredArgsConstructor;
import org.machinemc.api.chunk.palette.Palette;
import org.machinemc.api.utils.ServerBuffer;

import java.util.Objects;

/**
 * Palette fully filled with one single value, can be used
 * as empty of singe-value fully filled palette.
 */
@RequiredArgsConstructor
public class FilledPalette implements Palette {

    protected final byte dimension;
    protected final int value;

    @Override
    public int get(final int x, final int y, final int z) {
        return value;
    }

    @Override
    public void getAll(final EntryConsumer consumer) {
        Objects.requireNonNull(consumer, "Consumer can not be null");

        final byte dimension = this.dimension;
        final int value = this.value;
        for (byte x = 0; x < dimension; x++)
            for (byte y = 0; y < dimension; y++)
                for (byte z = 0; z < dimension; z++)
                    consumer.accept(x, y, z, value);
    }

    @Override
    public void getAllPresent(final EntryConsumer consumer) {
        if (value != 0) getAll(consumer);
    }

    @Override
    public void set(final int x, final int y, final int z, final int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fill(final int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAll(final EntrySupplier supplier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void replace(final int x, final int y, final int z, final int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void replaceAll(final EntryFunction function) {
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
    public void write(final ServerBuffer buf) {
        buf.writeByte((byte) 0);
        buf.writeVarInt(value);
        buf.writeVarInt(0);
    }

}
