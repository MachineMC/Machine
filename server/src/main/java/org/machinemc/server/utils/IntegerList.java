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
package org.machinemc.server.utils;

import java.util.*;

/**
 *
 * @author Sam Harwell
 */
public class IntegerList {

    private static final int[] EMPTY_DATA = new int[0];

    private static final int INITIAL_SIZE = 4;
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;


    private int[] data;

    private int size;

    public IntegerList() {
        data = EMPTY_DATA;
    }

    public IntegerList(final int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException();
        }

        if (capacity == 0) {
            data = EMPTY_DATA;
        } else {
            data = new int[capacity];
        }
    }

    public IntegerList(final IntegerList list) {
        data = list.data.clone();
        size = list.size;
    }

    public IntegerList(final Collection<Integer> list) {
        this(list.size());
        for (final Integer value : list) {
            add(value);
        }
    }

    /**
     * Adds new value to this integer list.
     * @param value value to add
     */
    public final void add(final int value) {
        if (data.length == size) {
            ensureCapacity(size + 1);
        }

        data[size] = value;
        size++;
    }

    /**
     * Adds all integers from integer array to this
     * integer list.
     * @param array array to add
     */
    public final void addAll(final int[] array) {
        ensureCapacity(size + array.length);
        System.arraycopy(array, 0, data, size, array.length);
        size += array.length;
    }

    /**
     * Adds all integers from another integer list to this
     * integer list.
     * @param list integer list to add
     */
    public final void addAll(final IntegerList list) {
        ensureCapacity(size + list.size);
        System.arraycopy(list.data, 0, data, size, list.size);
        size += list.size;
    }

    /**
     * Adds all integers from given collection to this list.
     * @param list list to add
     */
    public final void addAll(final Collection<Integer> list) {
        ensureCapacity(size + list.size());
        int current = 0;
        for (final int x : list) {
            data[size + current] = x;
            current++;
        }
        size += list.size();
    }

    /**
     * Returns value at specific index.
     * @param index index
     * @return value at given index
     */
    public final int get(final int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        return data[index];
    }

    /**
     * Checks whether the list contains the given value.
     * @param value value
     * @return whether the list contains the value
     */
    public final boolean contains(final int value) {
        for (int i = 0; i < size; i++) {
            if (data[i] == value) {
                return true;
            }
        }

        return false;
    }

    /**
     * Sets new entry at given index.
     * @param index index
     * @param value new value
     * @return previous value
     */
    public final int set(final int index, final int value) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        final int previous = data[index];
        data[index] = value;
        return previous;
    }

    /**
     * Removes entry at specific index.
     * @param index index
     * @return removed value
     */
    public final int removeAt(final int index) {
        final int value = get(index);
        System.arraycopy(data, index + 1, data, index, size - index - 1);
        data[size - 1] = 0;
        size--;
        return value;
    }

    /**
     * Removes all entries between two indices.
     * @param fromIndex start index
     * @param toIndex end index
     */
    public final void removeRange(final int fromIndex, final int toIndex) {
        if (fromIndex < 0 || toIndex < 0 || fromIndex > size || toIndex > size) {
            throw new IndexOutOfBoundsException();
        }
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException();
        }

        System.arraycopy(data, toIndex, data, fromIndex, size - toIndex);
        Arrays.fill(data, size - (toIndex - fromIndex), size, 0);
        size -= toIndex - fromIndex;
    }

    /**
     * @return whether the list is empty
     */
    public final boolean isEmpty() {
        return size == 0;
    }

    /**
     * @return size of this list
     */
    public final int size() {
        return size;
    }

    /**
     * Trims the size of backing data array.
     */
    public final void trimToSize() {
        if (data.length == size) {
            return;
        }

        data = Arrays.copyOf(data, size);
    }

    /**
     * Clears the list.
     */
    public final void clear() {
        Arrays.fill(data, 0, size, 0);
        size = 0;
    }

    /**
     * Converts the integer list to an array.
     * @return array copy for this list
     */
    public final int[] toArray() {
        if (size == 0) {
            return EMPTY_DATA;
        }

        return Arrays.copyOf(data, size);
    }

    /**
     * Sorts the integer list.
     */
    public final void sort() {
        Arrays.sort(data, 0, size);
    }

    /**
     * Compares the specified object with this list for equality.  Returns
     * {@code true} if and only if the specified object is also an {@link IntegerList},
     * both lists have the same size, and all corresponding pairs of elements in
     * the two lists are equal.  In other words, two lists are defined to be
     * equal if they contain the same elements in the same order.
     * <p>
     * This implementation first checks if the specified object is this
     * list. If so, it returns {@code true}; if not, it checks if the
     * specified object is an {@link IntegerList}. If not, it returns {@code false};
     * if so, it checks the size of both lists. If the lists are not the same size,
     * it returns {@code false}; otherwise it iterates over both lists, comparing
     * corresponding pairs of elements.  If any comparison returns {@code false},
     * this method returns {@code false}.
     *
     * @param o the object to be compared for equality with this list
     * @return {@code true} if the specified object is equal to this list
     */
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof IntegerList other)) {
            return false;
        }

        if (size != other.size) {
            return false;
        }

        for (int i = 0; i < size; i++) {
            if (data[i] != other.data[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the hash code value for this list.
     *
     * <p>This implementation uses exactly the code that is used to define the
     * list hash function in the documentation for the {@link List#hashCode}
     * method.</p>
     *
     * @return the hash code value for this list
     */
    @Override
    public int hashCode() {
        int hashCode = 1;
        for (int i = 0; i < size; i++) {
            hashCode = 31 * hashCode + data[i];
        }

        return hashCode;
    }

    /**
     * Returns a string representation of this list.
     */
    @Override
    public String toString() {
        return Arrays.toString(toArray());
    }

    /**
     * Searches a range of the specified array of ints for the
     * specified value using the binary search algorithm.
     * @param key key
     * @return found value
     */
    public final int binarySearch(final int key) {
        return Arrays.binarySearch(data, 0, size, key);
    }

    /**
     * Searches a range of the specified array of ints for
     * the specified value using the binary search algorithm.
     * @param fromIndex start index
     * @param toIndex end index
     * @param key key
     * @return found value
     */
    public final int binarySearch(final int fromIndex, final int toIndex, final int key) {
        if (fromIndex < 0 || toIndex < 0 || fromIndex > size || toIndex > size) {
            throw new IndexOutOfBoundsException();
        }
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException();
        }

        return Arrays.binarySearch(data, fromIndex, toIndex, key);
    }

    private void ensureCapacity(final int capacity) {
        if (capacity < 0 || capacity > MAX_ARRAY_SIZE) {
            throw new OutOfMemoryError();
        }

        int newLength;
        if (data.length == 0) {
            newLength = INITIAL_SIZE;
        } else {
            newLength = data.length;
        }

        while (newLength < capacity) {
            newLength = newLength * 2;
            if (newLength < 0 || newLength > MAX_ARRAY_SIZE) {
                newLength = MAX_ARRAY_SIZE;
            }
        }

        data = Arrays.copyOf(data, newLength);
    }

    /** Convert the int list to a char array where values > 0x7FFFF take 2 bytes.
     *  If all values are less
     *  than the 0x7FFF 16-bit code point limit (1 bit taken to indicate then this is just a char array
     *  of 16-bit char as usual. For values in the supplementary range, encode
     * them as two UTF-16 code units.
     * @return integer list converted as character array
     */
    public final char[] toCharArray() {
        // Optimize for the common case (all data values are
        // < 0xFFFF) to avoid an extra scan
        char[] resultArray = new char[size];
        int resultIdx = 0;
        boolean calculatedPreciseResultSize = false;
        for (int i = 0; i < size; i++) {
            final int codePoint = data[i];
            // Calculate the precise result size if we encounter
            // a code point > 0xFFFF
            if (!calculatedPreciseResultSize
                    && Character.isSupplementaryCodePoint(codePoint)) {
                resultArray = Arrays.copyOf(resultArray, charArraySize());
                calculatedPreciseResultSize = true;
            }
            // This will throw IllegalArgumentException if
            // the code point is not a valid Unicode code point
            final int charsWritten = Character.toChars(codePoint, resultArray, resultIdx);
            resultIdx += charsWritten;
        }
        return resultArray;
    }

    private int charArraySize() {
        int result = 0;
        for (int i = 0; i < size; i++) {
            result += Character.charCount(data[i]);
        }
        return result;
    }
}
