package me.pesekjak.machine.chunk.palette;

import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.MathUtils;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;

public class AdaptivePalette implements Palette {

    protected final byte
            dimension,
            defaultBitsPerEntry,
            maxBitsPerEntry;
    protected Palette palette;

    AdaptivePalette(byte dimension, byte maxBitsPerEntry, byte bitsPerEntry) {
        validateDimension(dimension);
        this.dimension = dimension;
        this.maxBitsPerEntry = maxBitsPerEntry;
        this.defaultBitsPerEntry = bitsPerEntry;
        this.palette = new FilledPalette(dimension, 0);
    }

    @Override
    public int get(int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0)
            throw new IllegalArgumentException("Coordinates must be positive");
        return palette.get(x, y, z);
    }

    @Override
    public void getAll(@NotNull EntryConsumer consumer) {
        palette.getAll(consumer);
    }

    @Override
    public void getAllPresent(@NotNull EntryConsumer consumer) {
        palette.getAllPresent(consumer);
    }

    @Override
    public void set(int x, int y, int z, int value) {
        if (x < 0 || y < 0 || z < 0)
            throw new IllegalArgumentException("Coordinates must be positive");
        flexiblePalette().set(x, y, z, value);
    }

    @Override
    public void fill(int value) {
        palette = new FilledPalette(dimension, value);
    }

    @Override
    public void setAll(@NotNull EntrySupplier supplier) {
        Palette newPalette = new FlexiblePalette(this);
        newPalette.setAll(supplier);
        palette = newPalette;
    }

    @Override
    public void replace(int x, int y, int z, int value) {
        if (x < 0 || y < 0 || z < 0)
            throw new IllegalArgumentException("Coordinates must be positive");
        flexiblePalette().replace(x, y, z, value);
    }

    @Override
    public void replaceAll(@NotNull EntryFunction function) {
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
            AdaptivePalette adaptivePalette = (AdaptivePalette) super.clone();
            adaptivePalette.palette = palette.clone();
            return adaptivePalette;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public void write(@NotNull FriendlyByteBuf buf) {
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

        if(!(currentPalette instanceof FlexiblePalette flexiblePalette))
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
        } else if (currentBitsPerEntry > defaultBitsPerEntry &&
                (bitsPerEntry = MathUtils.bitsToRepresent(entries.size() - 1)) < currentBitsPerEntry) {
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
     * Checks if provided dimension is valid (power of 2)
     * @param dimension dimension
     */
    private static void validateDimension(int dimension) {
        if (dimension <= 1 || (dimension & dimension - 1) != 0)
            throw new IllegalArgumentException("Dimension must be a positive power of 2");
    }

}
