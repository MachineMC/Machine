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

import org.machinemc.api.chunk.palette.Palette;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.utils.math.MathUtils;

import java.util.LinkedHashSet;
import java.util.Set;

public class AdaptivePalette implements Palette {

    protected final byte dimension;
    protected final byte defaultBitsPerEntry;
    protected final byte maxBitsPerEntry;

    protected Palette palette;

    /**
     * @return default palette for blocks
     */
    public static Palette blocks() {
        return newPalette(16, 8, 4);
    }

    /**
     * @return default palette for biomes
     */
    public static Palette biomes() {
        return newPalette(4, 3, 1);
    }

    /**
     * Creates new palette filled with zeros.
     * @param dimension dimension of the palette
     * @param maxBitsPerEntry max bits per entry
     * @param bitsPerEntry min bits per entry
     * @return created palette
     */
    public static Palette newPalette(final int dimension, final int maxBitsPerEntry, final int bitsPerEntry) {
        return new AdaptivePalette((byte) dimension, (byte) maxBitsPerEntry, (byte) bitsPerEntry);
    }

    AdaptivePalette(final byte dimension, final byte maxBitsPerEntry, final byte bitsPerEntry) {
        validateDimension(dimension);
        this.dimension = dimension;
        this.maxBitsPerEntry = maxBitsPerEntry;
        this.defaultBitsPerEntry = bitsPerEntry;
        this.palette = new FilledPalette(dimension, 0);
    }

    @Override
    public int get(final int x, final int y, final int z) {
        if (x < 0 || y < 0 || z < 0)
            throw new IllegalArgumentException("Coordinates must be positive");
        return palette.get(x, y, z);
    }

    @Override
    public void getAll(final EntryConsumer consumer) {
        palette.getAll(consumer);
    }

    @Override
    public void getAllPresent(final EntryConsumer consumer) {
        palette.getAllPresent(consumer);
    }

    @Override
    public void set(final int x, final int y, final int z, final int value) {
        if (x < 0 || y < 0 || z < 0)
            throw new IllegalArgumentException("Coordinates must be positive");
        flexiblePalette().set(x, y, z, value);
    }

    @Override
    public void fill(final int value) {
        palette = new FilledPalette(dimension, value);
    }

    @Override
    public void setAll(final EntrySupplier supplier) {
        final Palette newPalette = new FlexiblePalette(this);
        newPalette.setAll(supplier);
        palette = newPalette;
    }

    @Override
    public void replace(final int x, final int y, final int z, final int value) {
        if (x < 0 || y < 0 || z < 0)
            throw new IllegalArgumentException("Coordinates must be positive");
        flexiblePalette().replace(x, y, z, value);
    }

    @Override
    public void replaceAll(final EntryFunction function) {
        flexiblePalette().replaceAll(function);
    }

    @Override
    public int count() {
        return palette.count();
    }

    @Override
    public int bitsPerEntry() {
        return palette.bitsPerEntry();
    }

    @Override
    public int maxBitsPerEntry() {
        return maxBitsPerEntry;
    }

    @Override
    public int dimension() {
        return dimension;
    }

    @Override
    public Palette clone() {
        try {
            final AdaptivePalette adaptivePalette = (AdaptivePalette) super.clone();
            adaptivePalette.palette = palette.clone();
            return adaptivePalette;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public void write(final ServerBuffer buf) {
        final Palette optimized = optimizedPalette();
        this.palette = optimized;
        optimized.write(buf);
    }

    /**
     * Optimizes the wrapped palette, either converts FlexiblePalette to
     * FilledPalette if contains only one entry or resizes the FlexiblePalette
     * if possible.
     * @return optimized palette
     */
    Palette optimizedPalette() {
        final Palette currentPalette = palette;

        if (!(currentPalette instanceof FlexiblePalette flexiblePalette))
            return currentPalette;

        final int count = flexiblePalette.count();
        if (count == 0)
            return new FilledPalette(dimension, 0);

        final Set<Integer> entries = new LinkedHashSet<>(flexiblePalette.paletteToValueList.size());
        flexiblePalette.getAll((x, y, z, value) -> entries.add(value));

        final int currentBitsPerEntry = flexiblePalette.bitsPerEntry();
        final int bitsPerEntry;

        if (entries.size() == 1) {
            return new FilledPalette(dimension, entries.iterator().next());
        } else if (currentBitsPerEntry > defaultBitsPerEntry
                && (bitsPerEntry = MathUtils.bitsToRepresent(entries.size() - 1)) < currentBitsPerEntry) {
            flexiblePalette.resize((byte) bitsPerEntry);
            return flexiblePalette;
        }

        return currentPalette;
    }

    /**
     * Converts wrapped palette of this adaptive palette to
     * FlexiblePalette in case it's FilledPalette.
     * @return converted flexible palette
     */
    Palette flexiblePalette() {
        Palette currentPalette = palette;
        if (currentPalette instanceof FilledPalette filledPalette) {
            currentPalette = new FlexiblePalette(this);
            currentPalette.fill(filledPalette.value);
            palette = currentPalette;
        }
        return currentPalette;
    }

    /**
     * Checks if provided dimension is valid (power of 2).
     * @param dimension dimension
     */
    private static void validateDimension(final int dimension) {
        if (dimension <= 1 || (dimension & dimension - 1) != 0)
            throw new IllegalArgumentException("Dimension must be a positive power of 2");
    }

}
