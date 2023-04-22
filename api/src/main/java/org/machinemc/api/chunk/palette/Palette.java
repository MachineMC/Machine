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
 * If not, see <https://www.gnu.org/licenses/>.
 */
package org.machinemc.api.chunk.palette;

import org.machinemc.api.utils.Writable;

/**
 * Palette-based storage of entries.
 * Palette Containers have an associated global palette
 * (either block states ids or biome ids as of now),
 * where values are mapped from.
 *
 * @see <a href="https://wiki.vg/Chunk_Format#Paletted_Container_structure">Paletted Container Structure</a>
 */
public interface Palette extends Writable, Cloneable {

    /**
     * Returns value at given coordinates in the palette.
     * @param x x coordinate of the value
     * @param y y coordinate of the value
     * @param z z coordinate of the value
     * @return value at given coordinates
     */
    int get(int x, int y, int z);

    /**
     * Accepts all values in the palette.
     * @param consumer consumer for all values in palette
     */
    void getAll(EntryConsumer consumer);

    /**
     * Accepts all non-zero values in the palette.
     * @param consumer consumer for all non-zero values in palette
     */
    void getAllPresent(EntryConsumer consumer);

    /**
     * Changes the value at given coordinates in the palette.
     * @param x x coordinate of the value
     * @param y y coordinate of the value
     * @param z z coordinate of the value
     * @param value new value at given coordinates
     */
    void set(int x, int y, int z, int value);

    /**
     * Fills the full palette with single value.
     * @param value value to fill the palette with
     */
    void fill(int value);

    /**
     * Changes all values in the palette using supplier.
     * @param supplier supplier for all values in palette
     */
    void setAll(EntrySupplier supplier);

    /**
     * Replaces value at given coordinates with new value.
     * @param x x coordinate of the value
     * @param y y coordinate of the value
     * @param z z coordinate of the value
     * @param value value to replace the old value with
     */
    void replace(int x, int y, int z, int value);

    /**
     * Replaces all values in the palette using function.
     * @param function function for all values in palette
     */
    void replaceAll(EntryFunction function);

    /**
     * @return number of entries in this palette.
     */
    int count();

    /**
     * @return number of bits used per entry.
     */
    int bitsPerEntry();

    /**
     * @return number of bits maximally used per entry.
     */
    int maxBitsPerEntry();

    /**
     * @return dimension of the palette
     */
    int dimension();

    /**
     * @return maximal size of the palette (dimension^3)
     */
    default int maxSize() {
        final int dimension = dimension();
        return dimension * dimension * dimension;
    }

    /**
     * @return clone of this palette
     */
    Palette clone();

    /**
     * Special supplier used for operations with the palettes.
     */
    @FunctionalInterface
    interface EntrySupplier {
        /**
         * Sets new entry in the palette.
         * @param x x coordinate
         * @param y y coordinate
         * @param z z coordinate
         * @return new value
         */
        int get(int x, int y, int z);
    }

    /**
     * Special consumer used for operations with the palettes.
     */
    @FunctionalInterface
    interface EntryConsumer {
        /**
         * Accepts the entry in the palette.
         * @param x x coordinate
         * @param y y coordinate
         * @param z z coordinate
         * @param value value
         */
        void accept(int x, int y, int z, int value);
    }

    /**
     * Special function used for operations with the palettes.
     */
    @FunctionalInterface
    interface EntryFunction {
        /**
         * Replaces the entry in the palette.
         * @param x x coordinate
         * @param y y coordinate
         * @param z z coordinate
         * @param value old value
         * @return new value
         */
        int apply(int x, int y, int z, int value);
    }

}
