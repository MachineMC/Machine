package me.pesekjak.machine.chunk.palette;

import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

/**
 * Palette-based storage of entries.
 * Palette Containers have an associated global palette
 * (either block states ids or biome ids as of now),
 * where values are mapped from.
 *
 * {@see "https://wiki.vg/Chunk_Format#Paletted_Container_structure"}
 */
public interface Palette extends Cloneable {

    /**
     * @return default palette for blocks
     */
    static Palette blocks() {
        return newPalette(16, 8, 4);
    }

    /**
     * @return default palette for biomes
     */
    static Palette biomes() {
        return newPalette(4, 3, 1);
    }

    /**
     * Creates new palette filled with zeros
     * @param dimension dimension of the palette
     * @param maxBitsPerEntry max bits per entry
     * @param bitsPerEntry min bits per entry
     * @return created palette
     */
    static Palette newPalette(int dimension, int maxBitsPerEntry, int bitsPerEntry) {
        return new AdaptivePalette((byte) dimension, (byte) maxBitsPerEntry, (byte) bitsPerEntry);
    }

    /**
     * @param x x coordinate of the value
     * @param y y coordinate of the value
     * @param z z coordinate of the value
     * @return value at given coordinates
     */
    int get(int x, int y, int z);

    /**
     * @param consumer consumer for all values in palette
     */
    void getAll(@NotNull EntryConsumer consumer);

    /**
     * @param consumer consumer for all non-zero values in palette
     */
    void getAllPresent(@NotNull EntryConsumer consumer);

    /**
     * @param x x coordinate of the value
     * @param y y coordinate of the value
     * @param z z coordinate of the value
     * @param value new value at given coordinates
     */
    void set(int x, int y, int z, int value);

    /**
     * Fills the full palette with single value
     * @param value value to fill the palette with
     */
    void fill(int value);

    /**
     * @param supplier supplier for all values in palette
     */
    void setAll(@NotNull EntrySupplier supplier);

    /**
     * @param x x coordinate of the value
     * @param y y coordinate of the value
     * @param z z coordinate of the value
     * @param value value to replace the old value with
     */
    void replace(int x, int y, int z, int value);

    /**
     * @param function function for all values in palette
     */
    void replaceAll(@NotNull EntryFunction function);

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

    Palette clone();

    /**
     * Writes the palette to the buffer
     * @param buf buffer to write in
     */
    void write(@NotNull FriendlyByteBuf buf);

    @FunctionalInterface
    interface EntrySupplier {
        int get(int x, int y, int z);
    }

    @FunctionalInterface
    interface EntryConsumer {
        void accept(int x, int y, int z, int value);
    }

    @FunctionalInterface
    interface EntryFunction {
        int apply(int x, int y, int z, int value);
    }

}
