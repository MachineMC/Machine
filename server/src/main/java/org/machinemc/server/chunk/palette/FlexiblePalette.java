package org.machinemc.server.chunk.palette;

import io.netty.util.collection.IntObjectHashMap;
import org.machinemc.api.chunk.palette.Palette;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.utils.IntegerList;
import org.machinemc.server.utils.math.MathUtils;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Palette able to take any value anywhere.
 */
public class FlexiblePalette implements Palette {

    private static final ThreadLocal<int[]> WRITE_CACHE = ThreadLocal.withInitial(() -> new int[4096]);

    protected byte bitsPerEntry;
    protected int count;

    protected final AdaptivePalette adaptivePalette;
    protected long[] values;

    protected IntegerList paletteToValueList;
    protected IntObjectHashMap<Integer> valueToPaletteMap;

    protected FlexiblePalette(final AdaptivePalette adaptivePalette, final byte bitsPerEntry) {
        this.adaptivePalette = adaptivePalette;
        this.bitsPerEntry = bitsPerEntry;
        this.paletteToValueList = new IntegerList(1);
        this.paletteToValueList.add(0);
        this.valueToPaletteMap = new IntObjectHashMap<>(1);
        this.valueToPaletteMap.put(0, (Integer) 0);

        final int valuesPerLong = Long.SIZE / bitsPerEntry;
        values = new long[(maxSize() + valuesPerLong - 1) / valuesPerLong];
    }

    protected FlexiblePalette(final AdaptivePalette adaptivePalette) {
        this(adaptivePalette, adaptivePalette.defaultBitsPerEntry);
    }

    @Override
    public int get(final int x, final int y, final int z) {
        final int bitsPerEntry = this.bitsPerEntry;
        final int sectionIndex = getSectionIndex(dimension(), x, y, z);
        final int valuesPerLong = Long.SIZE / bitsPerEntry;
        final int index = sectionIndex / valuesPerLong;
        final int bitIndex = (sectionIndex - index * valuesPerLong) * bitsPerEntry;
        final int value = (int) (values[index] >> bitIndex) & ((1 << bitsPerEntry) - 1);
        return hasPalette() ? paletteToValueList.get(value) : value;
    }

    @Override
    public void getAll(final EntryConsumer consumer) {
        retrieveAll(consumer, true);
    }

    @Override
    public void getAllPresent(final EntryConsumer consumer) {
        retrieveAll(consumer, false);
    }

    @Override
    public void set(final int x, final int y, final int z, final int value) {
        final int valueIndex = getPaletteIndex(value);
        final int bitsPerEntry = this.bitsPerEntry;
        final long[] values = this.values;

        final int valuesPerLong = Long.SIZE / bitsPerEntry;
        final int sectionIndex = getSectionIndex(dimension(), x, y, z);
        final int index = sectionIndex / valuesPerLong;
        final int bitIndex = (sectionIndex - index * valuesPerLong) * bitsPerEntry;

        final long block = values[index];
        final long clear = (1L << bitsPerEntry) - 1L;
        final long oldBlock = block >> bitIndex & clear;
        values[index] = block & ~(clear << bitIndex) | ((long) valueIndex << bitIndex);

        final boolean currentAir = oldBlock == 0;
        if (currentAir != (valueIndex == 0)) count += currentAir ? 1 : -1;
    }

    @Override
    public void fill(final int value) {
        if (value == 0) {
            Arrays.fill(values, 0);
            count = 0;
            return;
        }
        final int valueIndex = getPaletteIndex(value);
        final int bitsPerEntry = this.bitsPerEntry;
        final int valuesPerLong = Long.SIZE / bitsPerEntry;
        final long[] values = this.values;
        long block = 0;
        for (int i = 0; i < valuesPerLong; i++)
            block |= (long) valueIndex << i * bitsPerEntry;
        Arrays.fill(values, block);
        count = maxSize();
    }

    @Override
    public void setAll(final EntrySupplier supplier) {
        final int[] cache = WRITE_CACHE.get();
        final int dimension = dimension();
        int fillValue = -1;
        int count = 0;
        int index = 0;

        for (int x = 0; x < dimension; x++) {
            for (int y = 0; y < dimension; y++) {
                for (int z = 0; z < dimension; z++) {
                    int value = supplier.get(x, y, z);

                    // Support for using fill except of updateAll if
                    // supplier returns constant value.
                    if (fillValue != -2) {
                        if (fillValue == -1)
                            fillValue = value;
                        else if (fillValue != value)
                            fillValue = -2;
                    }

                    if (value != 0) {
                        value = getPaletteIndex(value);
                        count++;
                    }
                    cache[index++] = value;
                }
            }
        }

        assert index == maxSize();
        if (fillValue < 0) {
            updateAll(cache);
            this.count = count;
        } else {
            fill(fillValue);
        }
    }

    @Override
    public void replace(final int x, final int y, final int z, final int value) {
        final int oldValue = get(x, y, z);
        if (oldValue != value) set(x, y, z, value);
    }

    @Override
    public void replaceAll(final EntryFunction function) {
        final int[] cache = WRITE_CACHE.get();
        final AtomicInteger arrayIndex = new AtomicInteger();
        final AtomicInteger count = new AtomicInteger();
        getAll((x, y, z, value) -> {
            final int newValue = function.apply(x, y, z, value);
            final int index = arrayIndex.getPlain();
            arrayIndex.setPlain(index + 1);
            cache[index] = newValue != value ? getPaletteIndex(newValue) : value;
            if (newValue != 0) count.setPlain(count.getPlain() + 1);
        });
        assert arrayIndex.getPlain() == maxSize();
        updateAll(cache);
        this.count = count.getPlain();
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public int bitsPerEntry() {
        return bitsPerEntry;
    }

    @Override
    public int maxBitsPerEntry() {
        return adaptivePalette.maxBitsPerEntry();
    }

    @Override
    public int dimension() {
        return adaptivePalette.dimension();
    }

    @Override
    public Palette clone() {
        final FlexiblePalette palette;
        try {
            palette = (FlexiblePalette) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
        palette.values = values.clone();
        palette.paletteToValueList = new IntegerList();
        palette.valueToPaletteMap = new IntObjectHashMap<>();
        for (final int i : paletteToValueList.toArray())
            palette.paletteToValueList.add(i);
        for (final int i : valueToPaletteMap.keySet())
            palette.valueToPaletteMap.put(i, valueToPaletteMap.get(i));
        palette.count = count;
        palette.bitsPerEntry = bitsPerEntry;
        return palette;
    }

    @Override
    public void write(final ServerBuffer buf) {
        buf.writeByte(bitsPerEntry);
        if (bitsPerEntry <= maxBitsPerEntry()) {
            buf.writeVarInt(paletteToValueList.size());
            for (final int i : paletteToValueList.toArray())
                buf.writeVarInt(i);
        }
        buf.writeVarInt(values.length);
        for (final long l : values)
            buf.writeLong(l);
    }

    /**
     * Accepts all values of the palette.
     * @param consumer consumer
     * @param consumeEmpty if empty (zero) values should be accepted
     */
    private void retrieveAll(final EntryConsumer consumer, final boolean consumeEmpty) {
        if (!consumeEmpty && count == 0) return;

        final long[] values = this.values;
        final int dimension = this.dimension();
        final int bitsPerEntry = this.bitsPerEntry;
        final int magicMask = (1 << bitsPerEntry) - 1;
        final int valuesPerLong = Long.SIZE / bitsPerEntry;
        final int size = maxSize();
        final int dimensionMinus = dimension - 1;
        final int[] ids = hasPalette() ? paletteToValueList.toArray() : null;
        final int dimensionBitCount = MathUtils.bitsToRepresent(dimensionMinus);
        final int shiftedDimensionBitCount = dimensionBitCount << 1;

        for (int i = 0; i < values.length; i++) {
            final long value = values[i];

            final int startIndex = i * valuesPerLong;
            final int endIndex = Math.min(startIndex + valuesPerLong, size);

            for (int index = startIndex; index < endIndex; index++) {
                final int bitIndex = (index - startIndex) * bitsPerEntry;
                final int paletteIndex = (int) (value >> bitIndex & magicMask);
                if (consumeEmpty || paletteIndex != 0) {
                    final int y = index >> shiftedDimensionBitCount;
                    final int z = index >> dimensionBitCount & dimensionMinus;
                    final int x = index & dimensionMinus;
                    final int result = ids != null ? ids[paletteIndex] : paletteIndex;
                    consumer.accept(x, y, z, result);
                }
            }
        }
    }

    /**
     * Updates all palette values.
     * @param paletteValues new palette values
     */
    private void updateAll(final int[] paletteValues) {
        final int size = maxSize();
        assert paletteValues.length >= size;

        final int bitsPerEntry = this.bitsPerEntry;
        final int valuesPerLong = Long.SIZE / bitsPerEntry;

        final long clear = (1L << bitsPerEntry) - 1L;
        final long[] values = this.values;

        for (int i = 0; i < values.length; i++) {
            long block = values[i];

            final int startIndex = i * valuesPerLong;
            final int endIndex = Math.min(startIndex + valuesPerLong, size);

            for (int index = startIndex; index < endIndex; index++) {
                final int bitIndex = (index - startIndex) * bitsPerEntry;
                block = block & ~(clear << bitIndex) | ((long) paletteValues[index] << bitIndex);
            }
            values[i] = block;
        }
    }

    /**
     * Sets new bits per entry of this palette.
     * @param newBitsPerEntry new bits per entry
     */
    protected void resize(final byte newBitsPerEntry) {
        // Fixes invalid bitsPerEntry values.
        // https://wiki.vg/Chunk_Format#Direct
        final byte cappedBitsPerEntry = newBitsPerEntry > maxBitsPerEntry() ? 15 : newBitsPerEntry;

        final FlexiblePalette palette = new FlexiblePalette(adaptivePalette, cappedBitsPerEntry);
        palette.paletteToValueList = paletteToValueList;
        palette.valueToPaletteMap = valueToPaletteMap;
        getAll(palette::set);
        this.bitsPerEntry = palette.bitsPerEntry;
        this.values = palette.values;
        assert this.count == palette.count;
    }

    /**
     * Returns the index of a value in the palette, if not found
     * the value is added to the palette.
     * @param value value to get index for
     * @return index of the value
     */
    private int getPaletteIndex(final int value) {
        if (!hasPalette()) return value;
        final int lastPaletteIndex = paletteToValueList.size();
        final byte bitsPerEntry = this.bitsPerEntry;

        // if the palette is full, must resize and repeat
        if (lastPaletteIndex >= maxPaletteSize(bitsPerEntry)) {
            resize((byte) (bitsPerEntry + 1));
            return getPaletteIndex(value);
        }

        final Integer lookup = valueToPaletteMap.putIfAbsent(value, lastPaletteIndex);
        if (lookup != null) return lookup; // was found

        // wasn't found and is added
        paletteToValueList.add(value);
        assert lastPaletteIndex < maxPaletteSize(bitsPerEntry);
        return lastPaletteIndex;
    }

    /**
     * @return true if has valid AdaptivePalette
     */
    protected boolean hasPalette() {
        return bitsPerEntry <= maxBitsPerEntry();
    }

    /**
     * Gets the index of the block on the section array based on the block position.
     * @param dimension dimension
     * @param x x
     * @param y y
     * @param z z
     * @return index of the block on the section array
     */
    private static int getSectionIndex(final int dimension, final int x, final int y, final int z) {
        final int dimensionMask = dimension - 1;
        final int dimensionBitCount = MathUtils.bitsToRepresent(dimensionMask);
        return (y & dimensionMask) << (dimensionBitCount << 1)
                | (z & dimensionMask) << dimensionBitCount
                | (x & dimensionMask);
    }

    /**
     * Returns maximal elements size of a palette.
     * @param bitsPerEntry bits per entry of the palette to check for
     * @return maximal size of a palette
     */
    private static int maxPaletteSize(final int bitsPerEntry) {
        return 1 << bitsPerEntry;
    }

}
