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
package org.machinemc.network.protocol;

import com.google.common.base.Preconditions;
import io.netty.buffer.*;
import io.netty.util.ByteProcessor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * A packet byte buf is a specialized byte buf with utility methods adapted
 * to Minecraft's protocol. It has serialization and deserialization of
 * custom objects.
 */
public class FriendlyByteBuf extends ByteBuf {

    private final ByteBuf delegate;

    /**
     * Creates new friendly buffer that delegates all methods calls to
     * another, already existing one.
     *
     * @param delegate delegate buffer
     */
    public FriendlyByteBuf(final ByteBuf delegate) {
        this.delegate = Preconditions.checkNotNull(delegate, "Delegate buffer can not be null");
    }

    /**
     * Creates new friendly byte buffer from Unpooled buffer with
     * unlimited capacity.
     *
     * @return buffer
     */
    public static FriendlyByteBuf unpooled() {
        return new FriendlyByteBuf(Unpooled.buffer());
    }

    /**
     * Returns input stream wrapped around this buffer.
     *
     * @return input stream
     */
    public InputStream asInputStream() {
        return new ByteBufInputStream(this);
    }

    /**
     * Returns output stream wrapped around this buffer.
     *
     * @return output stream
     */
    public OutputStream asOutputstream() {
        return new ByteBufOutputStream(this);
    }

    /**
     * Uses function to read custom element from the buffer.
     *
     * @param function function
     * @return read element
     * @param <T> element
     */
    public <T> T read(final Function<FriendlyByteBuf, T> function) {
        Preconditions.checkNotNull(function);
        return function.apply(this);
    }

    /**
     * Uses bi-consumer to write custom element to the buffer.
     *
     * @param element element to write
     * @param consumer consumer
     * @return this
     * @param <T> type of the element
     */
    @Contract("_, _ -> this")
    public <T> FriendlyByteBuf write(final @Nullable T element, final BiConsumer<FriendlyByteBuf, T> consumer) {
        Preconditions.checkNotNull(consumer);
        consumer.accept(this, element);
        return this;
    }

    /**
     * Reads with var int length-prefixed array from the buffer.
     *
     * @param generator array generator
     * @param function function for reading elements
     * @return array
     * @param <T> type of the array
     */
    public <T> T[] readArray(final IntFunction<T[]> generator, final Function<FriendlyByteBuf, T> function) {
        Preconditions.checkNotNull(generator);
        Preconditions.checkNotNull(function);
        final T[] array = generator.apply(readVarInt());
        for (int i = 0; i < array.length; i++) array[i] = function.apply(this);
        return array;
    }

    /**
     * Writes array to the buffer length-prefixed with var int.
     *
     * @param array array to write
     * @param consumer bi-consumer for writing elements
     * @return this
     * @param <T> type of the array
     */
    @Contract("_, _ -> this")
    public <T> FriendlyByteBuf writeArray(final T[] array, final BiConsumer<FriendlyByteBuf, T> consumer) {
        Preconditions.checkNotNull(array);
        Preconditions.checkNotNull(consumer);
        writeVarInt(array.length);
        for (final T value : array) consumer.accept(this, value);
        return this;
    }

    /**
     * Reads with var int length-prefixed list from the buffer.
     *
     * @param function function for reading elements
     * @return list
     * @param <T> type of the list
     */
    public <T> List<T> readList(final Function<FriendlyByteBuf, T> function) {
        Preconditions.checkNotNull(function);
        final List<T> list = new ArrayList<>();
        final int length = readVarInt();
        for (int i = 0; i < length; i++)
            list.add(function.apply(this));
        return Collections.unmodifiableList(list);
    }

    /**
     * Writes list to the buffer length-prefixed with var int.
     *
     * @param list list to write
     * @param consumer bi-consumer for writing elements
     * @return this
     * @param <T> type of the list
     */
    @Contract("_, _ -> this")
    public <T> FriendlyByteBuf writeList(final List<T> list, final BiConsumer<FriendlyByteBuf, T> consumer) {
        Preconditions.checkNotNull(list);
        Preconditions.checkNotNull(consumer);
        writeVarInt(list.size());
        for (final T item : list)
            consumer.accept(this, item);
        return this;
    }

    /**
     * Reads optional element from the buffer.
     *
     * @param function function to read the element
     * @return optional
     * @param <T> type of the element
     */
    public <T> Optional<T> readOptional(final Function<FriendlyByteBuf, T> function) {
        Preconditions.checkNotNull(function);
        if (!readBoolean()) return Optional.empty();
        return Optional.ofNullable(function.apply(this));
    }

    /**
     * Writes optional element to the buffer.
     *
     * @param value value to write
     * @param consumer consumer to write the element
     * @return this
     * @param <T> type of the element
     */
    @Contract("_, _ -> this")
    public <T> FriendlyByteBuf writeOptional(final @Nullable T value, final BiConsumer<FriendlyByteBuf, T> consumer) {
        Preconditions.checkNotNull(consumer);
        writeBoolean(value != null);
        if (value != null) consumer.accept(this, value);
        return this;
    }

    /**
     * Reads var int from the buffer.
     * <p>
     * For more information about var integers see
     * <a href="https://wiki.vg/Protocol#VarInt_and_VarLong">VarInt and VarLong</a>.
     *
     * @return next var int
     */
    public int readVarInt() {
        return ProtocolUtils.readVarInt(this);
    }

    /**
     * Writes var int to the buffer.
     * <p>
     * For more information about var integers see
     * <a href="https://wiki.vg/Protocol#VarInt_and_VarLong">VarInt and VarLong</a>.
     *
     * @param value value to write
     * @return this
     */
    @Contract("_ -> this")
    public FriendlyByteBuf writeVarInt(final int value) {
        ProtocolUtils.writeVarInt(this, value);
        return this;
    }

    /**
     * Reads var long from the buffer.
     * <p>
     * For more information about var longs see
     * <a href="https://wiki.vg/Protocol#VarInt_and_VarLong">VarInt and VarLong</a>.
     *
     * @return next var long
     */
    public long readVarLong() {
        return ProtocolUtils.readVarLong(this);
    }

    /**
     * Writes var long to the buffer.
     * <p>
     * For more information about var longs see
     * <a href="https://wiki.vg/Protocol#VarInt_and_VarLong">VarInt and VarLong</a>.
     *
     * @param value value to write
     * @return this
     */
    @Contract("_ -> this")
    public FriendlyByteBuf writeVarLong(final long value) {
        ProtocolUtils.writeVarLong(this, value);
        return this;
    }

    /**
     * Reads byte array prefixed with var int from this buffer.
     *
     * @return byte array
     */
    public byte[] readByteArray() {
        return readBytes(new byte[readVarInt()]).array();
    }

    /**
     * Writes byte array prefixed with var int to this buffer.
     *
     * @param bytes byte array to write
     * @return this
     */
    @Contract("_ -> this")
    public FriendlyByteBuf writeByteArray(final byte[] bytes) {
        Preconditions.checkNotNull(bytes);
        writeVarInt(bytes.length);
        writeBytes(bytes);
        return this;
    }

    /**
     * Reads long array prefixed with var int from this buffer.
     *
     * @return long array
     */
    public long[] readLongArray() {
        final int length = readVarInt();
        final long[] longs = new long[length];
        for (int i = 0; i < length; i++) longs[i] = readLong();
        return longs;
    }

    /**
     * Writes long array prefixed with var int to this buffer.
     *
     * @param longs long array to write
     * @return this
     */
    @Contract("_ -> this")
    public FriendlyByteBuf writeLongArray(final long[] longs) {
        Preconditions.checkNotNull(longs);
        writeVarInt(longs.length);
        for (final long l : longs) writeLong(l);
        return this;
    }

    /**
     * Reads var int array prefixed with var int from this buffer.
     *
     * @return var int array
     */
    public int[] readVarIntArray() {
        final int length = readVarInt();
        final int[] ints = new int[length];
        for (int i = 0; i < length; i++) ints[i] = readVarInt();
        return ints;
    }

    /**
     * Writes var int array prefixed with var int to this buffer.
     *
     * @param ints int array to write
     * @return this
     */
    @Contract("_ -> this")
    public FriendlyByteBuf writeVarIntArray(final int[] ints) {
        Preconditions.checkNotNull(ints);
        writeVarInt(ints.length);
        for (final int i : ints) writeVarInt(i);
        return this;
    }

    /**
     * Reads next string from the buffer using provided charset.
     *
     * @param charset charset to use
     * @return next string
     */
    public String readString(final Charset charset) {
        Preconditions.checkNotNull(charset);
        final int length = readVarInt();
        if (length < 0) throw new IllegalStateException("String has illegal length of: " + length);
        final byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++)
            bytes[i] = readByte();
        return new String(bytes, charset);
    }

    /**
     * Writes next string to the buffer.
     *
     * @param value string to write
     * @param charset charset to use
     * @return this
     */
    @Contract("_, _ -> this")
    public FriendlyByteBuf writeString(final String value, final Charset charset) {
        Preconditions.checkNotNull(value);
        Preconditions.checkNotNull(charset);
        final byte[] bytes = value.getBytes(charset);
        writeVarInt(bytes.length);
        writeBytes(bytes);
        return this;
    }

    /**
     * Reads next UUID from this buffer.
     *
     * @return next uuid
     */
    public UUID readUUID() {
        return new UUID(readLong(), readLong());
    }

    /**
     * Writes UUID to this buffer.
     *
     * @param uuid uuid to write
     * @return this
     */
    @Contract("_ -> this")
    public FriendlyByteBuf writeUUID(final UUID uuid) {
        Preconditions.checkNotNull(uuid);
        writeLong(uuid.getMostSignificantBits());
        writeLong(uuid.getLeastSignificantBits());
        return this;
    }

    /**
     * Reads next bitset from the buffer.
     *
     * @return next bitset
     */
    public BitSet readBitSet() {
        return BitSet.valueOf(readLongArray());
    }

    /**
     * Reads next bitset from the buffer.
     *
     * @param size fixed size of the bitset
     * @return next bitset
     */
    public BitSet readBitSet(final int size) {
        final byte[] bytes = readBytes(-Math.floorDiv(-size, 8)).array();
        return BitSet.valueOf(bytes);
    }

    /**
     * Writes bitset to this buffer.
     *
     * @param bitSet bitset to write
     * @return this
     */
    @Contract("_ -> this")
    public FriendlyByteBuf writeBitSet(final BitSet bitSet) {
        Preconditions.checkNotNull(bitSet);
        writeLongArray(bitSet.toLongArray());
        return this;
    }

    /**
     * Writes bitset to this buffer.
     *
     * @param bitSet bitset to write
     * @param size fixed size of the bitset
     * @return this
     */
    @Contract("_, _ -> this")
    public FriendlyByteBuf writeBitSet(final BitSet bitSet, final int size) {
        Preconditions.checkNotNull(bitSet);
        if (bitSet.length() > size)
            throw new RuntimeException("BitSet is larger than expected size");
        final byte[] bytes = bitSet.toByteArray();
        writeBytes(Arrays.copyOf(bytes, -Math.floorDiv(-size, 8)));
        return this;
    }

    /**
     * Reads next instant from this buffer.
     *
     * @return next instant
     */
    public Instant readInstant() {
        return Instant.ofEpochMilli(readLong());
    }

    /**
     * Writes instant to this buffer.
     *
     * @param instant instant to write
     * @return this
     */
    @Contract("_ -> this")
    public FriendlyByteBuf writeInstant(final Instant instant) {
        Preconditions.checkNotNull(instant);
        writeLong(instant.toEpochMilli());
        return this;
    }

    /**
     * Reads next enum constant from this buffer.
     *
     * @param enumClass class of the enum
     * @return enum constant
     * @param <T> enum type
     */
    public <T extends Enum<T>> T readEnum(final Class<T> enumClass) {
        Preconditions.checkNotNull(enumClass);
        final T[] constants = enumClass.getEnumConstants();
        final int index = readVarInt();
        if (index >= constants.length)
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for enum " + enumClass.getName());
        return constants[index];
    }

    /**
     * Writes enum constant to this buffer.
     *
     * @param enumConstant enum constant to write
     * @return this
     * @param <T> enum type
     */
    @Contract("_ -> this")
    public <T extends Enum<T>> FriendlyByteBuf writeEnum(final T enumConstant) {
        Preconditions.checkNotNull(enumConstant);
        return writeVarInt(enumConstant.ordinal());
    }

    //<editor-fold desc="Delegate methods">

    @Override
    public int capacity() {
        return delegate.capacity();
    }

    @Override
    public ByteBuf capacity(final int i) {
        return delegate.capacity(i);
    }

    @Override
    public int maxCapacity() {
        return delegate.maxCapacity();
    }

    @Override
    public ByteBufAllocator alloc() {
        return delegate.alloc();
    }

    @Override
    @Deprecated
    public ByteOrder order() {
        return delegate.order();
    }

    @Override
    @Deprecated
    public ByteBuf order(final ByteOrder byteOrder) {
        return delegate.order(byteOrder);
    }

    @Override
    public ByteBuf unwrap() {
        return delegate.unwrap();
    }

    @Override
    public boolean isDirect() {
        return delegate.isDirect();
    }

    @Override
    public boolean isReadOnly() {
        return delegate.isReadOnly();
    }

    @Override
    public ByteBuf asReadOnly() {
        return delegate.asReadOnly();
    }

    @Override
    public int readerIndex() {
        return delegate.readerIndex();
    }

    @Override
    public ByteBuf readerIndex(final int i) {
        return delegate.readerIndex(i);
    }

    @Override
    public int writerIndex() {
        return delegate.writerIndex();
    }

    @Override
    public ByteBuf writerIndex(final int i) {
        return delegate.writerIndex(i);
    }

    @Override
    public ByteBuf setIndex(final int i, final int i1) {
        return delegate.setIndex(i, i1);
    }

    @Override
    public int readableBytes() {
        return delegate.readableBytes();
    }

    @Override
    public int writableBytes() {
        return delegate.writableBytes();
    }

    @Override
    public int maxWritableBytes() {
        return delegate.maxWritableBytes();
    }

    @Override
    public boolean isReadable() {
        return delegate.isReadable();
    }

    @Override
    public boolean isReadable(final int i) {
        return delegate.isReadable(i);
    }

    @Override
    public boolean isWritable() {
        return delegate.isWritable();
    }

    @Override
    public boolean isWritable(final int i) {
        return delegate.isWritable(i);
    }

    @Override
    public ByteBuf clear() {
        return delegate.clear();
    }

    @Override
    public ByteBuf markReaderIndex() {
        return delegate.markReaderIndex();
    }

    @Override
    public ByteBuf resetReaderIndex() {
        return delegate.resetReaderIndex();
    }

    @Override
    public ByteBuf markWriterIndex() {
        return delegate.markWriterIndex();
    }

    @Override
    public ByteBuf resetWriterIndex() {
        return delegate.resetWriterIndex();
    }

    @Override
    public ByteBuf discardReadBytes() {
        return delegate.discardReadBytes();
    }

    @Override
    public ByteBuf discardSomeReadBytes() {
        return delegate.discardReadBytes();
    }

    @Override
    public ByteBuf ensureWritable(final int i) {
        return delegate.ensureWritable(i);
    }

    @Override
    public int ensureWritable(final int i, final boolean b) {
        return delegate.ensureWritable(i, b);
    }

    @Override
    public boolean getBoolean(final int i) {
        return delegate.getBoolean(i);
    }

    @Override
    public byte getByte(final int i) {
        return delegate.getByte(i);
    }

    @Override
    public short getUnsignedByte(final int i) {
        return delegate.getUnsignedByte(i);
    }

    @Override
    public short getShort(final int i) {
        return delegate.getShort(i);
    }

    @Override
    public short getShortLE(final int i) {
        return delegate.getShortLE(i);
    }

    @Override
    public int getUnsignedShort(final int i) {
        return delegate.getUnsignedShort(i);
    }

    @Override
    public int getUnsignedShortLE(final int i) {
        return delegate.getUnsignedShortLE(i);
    }

    @Override
    public int getMedium(final int i) {
        return delegate.getMedium(i);
    }

    @Override
    public int getMediumLE(final int i) {
        return delegate.getMediumLE(i);
    }

    @Override
    public int getUnsignedMedium(final int i) {
        return delegate.getUnsignedMedium(i);
    }

    @Override
    public int getUnsignedMediumLE(final int i) {
        return delegate.getUnsignedMediumLE(i);
    }

    @Override
    public int getInt(final int i) {
        return delegate.getInt(i);
    }

    @Override
    public int getIntLE(final int i) {
        return delegate.getIntLE(i);
    }

    @Override
    public long getUnsignedInt(final int i) {
        return delegate.getUnsignedInt(i);
    }

    @Override
    public long getUnsignedIntLE(final int i) {
        return delegate.getUnsignedIntLE(i);
    }

    @Override
    public long getLong(final int i) {
        return delegate.getLong(i);
    }

    @Override
    public long getLongLE(final int i) {
        return delegate.getLongLE(i);
    }

    @Override
    public char getChar(final int i) {
        return delegate.getChar(i);
    }

    @Override
    public float getFloat(final int i) {
        return delegate.getFloat(i);
    }

    @Override
    public double getDouble(final int i) {
        return delegate.getDouble(i);
    }

    @Override
    public ByteBuf getBytes(final int i, final ByteBuf byteBuf) {
        return delegate.getBytes(i, byteBuf);
    }

    @Override
    public ByteBuf getBytes(final int i, final ByteBuf byteBuf, final int i1) {
        return delegate.getBytes(i, byteBuf, i1);
    }

    @Override
    public ByteBuf getBytes(final int i, final ByteBuf byteBuf, final int i1, final int i2) {
        return delegate.getBytes(i, byteBuf, i1, i2);
    }

    @Override
    public ByteBuf getBytes(final int i, final byte[] bytes) {
        return delegate.getBytes(i, bytes);
    }

    @Override
    public ByteBuf getBytes(final int i, final byte[] bytes, final int i1, final int i2) {
        return delegate.getBytes(i, bytes, i1, i2);
    }

    @Override
    public ByteBuf getBytes(final int i, final ByteBuffer byteBuffer) {
        return delegate.getBytes(i, byteBuffer);
    }

    @Override
    public ByteBuf getBytes(final int i, final OutputStream outputStream, final int i1) throws IOException {
        return delegate.getBytes(i, outputStream, i1);
    }

    @Override
    public int getBytes(final int i, final GatheringByteChannel gatheringByteChannel, final int i1) throws IOException {
        return delegate.getBytes(i, gatheringByteChannel, i1);
    }

    @Override
    public int getBytes(final int i, final FileChannel fileChannel, final long l, final int i1) throws IOException {
        return delegate.getBytes(i, fileChannel, l, i1);
    }

    @Override
    public CharSequence getCharSequence(final int i, final int i1, final Charset charset) {
        return delegate.getCharSequence(i, i1, charset);
    }

    @Override
    public ByteBuf setBoolean(final int i, final boolean b) {
        return delegate.setBoolean(i, b);
    }

    @Override
    public ByteBuf setByte(final int i, final int i1) {
        return delegate.setByte(i, i1);
    }

    @Override
    public ByteBuf setShort(final int i, final int i1) {
        return delegate.setShort(i, i1);
    }

    @Override
    public ByteBuf setShortLE(final int i, final int i1) {
        return delegate.setShortLE(i, i1);
    }

    @Override
    public ByteBuf setMedium(final int i, final int i1) {
        return delegate.setMedium(i, i1);
    }

    @Override
    public ByteBuf setMediumLE(final int i, final int i1) {
        return delegate.setMediumLE(i, i1);
    }

    @Override
    public ByteBuf setInt(final int i, final int i1) {
        return delegate.setInt(i, i1);
    }

    @Override
    public ByteBuf setIntLE(final int i, final int i1) {
        return delegate.setIntLE(i, i1);
    }

    @Override
    public ByteBuf setLong(final int i, final long l) {
        return delegate.setLong(i, l);
    }

    @Override
    public ByteBuf setLongLE(final int i, final long l) {
        return delegate.setLongLE(i, l);
    }

    @Override
    public ByteBuf setChar(final int i, final int i1) {
        return delegate.setChar(i, i1);
    }

    @Override
    public ByteBuf setFloat(final int i, final float v) {
        return delegate.setFloat(i, v);
    }

    @Override
    public ByteBuf setDouble(final int i, final double v) {
        return delegate.setDouble(i, v);
    }

    @Override
    public ByteBuf setBytes(final int i, final ByteBuf byteBuf) {
        return delegate.setBytes(i, byteBuf);
    }

    @Override
    public ByteBuf setBytes(final int i, final ByteBuf byteBuf, final int i1) {
        return delegate.setBytes(i, byteBuf, i1);
    }

    @Override
    public ByteBuf setBytes(final int i, final ByteBuf byteBuf, final int i1, final int i2) {
        return delegate.setBytes(i, byteBuf, i1, i2);
    }

    @Override
    public ByteBuf setBytes(final int i, final byte[] bytes) {
        return delegate.setBytes(i, bytes);
    }

    @Override
    public ByteBuf setBytes(final int i, final byte[] bytes, final int i1, final int i2) {
        return delegate.setBytes(i, bytes, i1, i2);
    }

    @Override
    public ByteBuf setBytes(final int i, final ByteBuffer byteBuffer) {
        return delegate.setBytes(i, byteBuffer);
    }

    @Override
    public int setBytes(final int i, final InputStream inputStream, final int i1) throws IOException {
        return delegate.setBytes(i, inputStream, i1);
    }

    @Override
    public int setBytes(final int i, final ScatteringByteChannel scatteringByteChannel, final int i1) throws IOException {
        return delegate.setBytes(i, scatteringByteChannel, i1);
    }

    @Override
    public int setBytes(final int i, final FileChannel fileChannel, final long l, final int i1) throws IOException {
        return delegate.setBytes(i, fileChannel, l, i1);
    }

    @Override
    public ByteBuf setZero(final int i, final int i1) {
        return delegate.setZero(i, i1);
    }

    @Override
    public int setCharSequence(final int i, final CharSequence charSequence, final Charset charset) {
        return delegate.setCharSequence(i, charSequence, charset);
    }

    @Override
    public boolean readBoolean() {
        return delegate.readBoolean();
    }

    @Override
    public byte readByte() {
        return delegate.readByte();
    }

    @Override
    public short readUnsignedByte() {
        return delegate.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return delegate.readShort();
    }

    @Override
    public short readShortLE() {
        return delegate.readShortLE();
    }

    @Override
    public int readUnsignedShort() {
        return delegate.readUnsignedShort();
    }

    @Override
    public int readUnsignedShortLE() {
        return delegate.readUnsignedShortLE();
    }

    @Override
    public int readMedium() {
        return delegate.readMedium();
    }

    @Override
    public int readMediumLE() {
        return delegate.readMediumLE();
    }

    @Override
    public int readUnsignedMedium() {
        return delegate.readUnsignedMedium();
    }

    @Override
    public int readUnsignedMediumLE() {
        return delegate.readUnsignedMediumLE();
    }

    @Override
    public int readInt() {
        return delegate.readInt();
    }

    @Override
    public int readIntLE() {
        return delegate.readIntLE();
    }

    @Override
    public long readUnsignedInt() {
        return delegate.readUnsignedInt();
    }

    @Override
    public long readUnsignedIntLE() {
        return delegate.readUnsignedIntLE();
    }

    @Override
    public long readLong() {
        return delegate.readLong();
    }

    @Override
    public long readLongLE() {
        return delegate.readLongLE();
    }

    @Override
    public char readChar() {
        return delegate.readChar();
    }

    @Override
    public float readFloat() {
        return delegate.readFloat();
    }

    @Override
    public double readDouble() {
        return delegate.readDouble();
    }

    @Override
    public ByteBuf readBytes(final int i) {
        return delegate.readBytes(i);
    }

    @Override
    public ByteBuf readSlice(final int i) {
        return delegate.readSlice(i);
    }

    @Override
    public ByteBuf readRetainedSlice(final int i) {
        return delegate.readRetainedSlice(i);
    }

    @Override
    public ByteBuf readBytes(final ByteBuf byteBuf) {
        return delegate.readBytes(byteBuf);
    }

    @Override
    public ByteBuf readBytes(final ByteBuf byteBuf, final int i) {
        return delegate.readBytes(byteBuf, i);
    }

    @Override
    public ByteBuf readBytes(final ByteBuf byteBuf, final int i, final int i1) {
        return delegate.readBytes(byteBuf, i, i1);
    }

    @Override
    public ByteBuf readBytes(final byte[] bytes) {
        return delegate.readBytes(bytes);
    }

    @Override
    public ByteBuf readBytes(final byte[] bytes, final int i, final int i1) {
        return delegate.readBytes(bytes, i, i1);
    }

    @Override
    public ByteBuf readBytes(final ByteBuffer byteBuffer) {
        return delegate.readBytes(byteBuffer);
    }

    @Override
    public ByteBuf readBytes(final OutputStream outputStream, final int i) throws IOException {
        return delegate.readBytes(outputStream, i);
    }

    @Override
    public int readBytes(final GatheringByteChannel gatheringByteChannel, final int i) throws IOException {
        return delegate.readBytes(gatheringByteChannel, i);
    }

    @Override
    public CharSequence readCharSequence(final int i, final Charset charset) {
        return delegate.readCharSequence(i, charset);
    }

    @Override
    public int readBytes(final FileChannel fileChannel, final long l, final int i) throws IOException {
        return delegate.readBytes(fileChannel, l, i);
    }

    @Override
    public ByteBuf skipBytes(final int i) {
        return delegate.skipBytes(i);
    }

    @Override
    public ByteBuf writeBoolean(final boolean b) {
        return delegate.writeBoolean(b);
    }

    @Override
    public ByteBuf writeByte(final int i) {
        return delegate.writeByte(i);
    }

    @Override
    public ByteBuf writeShort(final int i) {
        return delegate.writeShort(i);
    }

    @Override
    public ByteBuf writeShortLE(final int i) {
        return delegate.writeShortLE(i);
    }

    @Override
    public ByteBuf writeMedium(final int i) {
        return delegate.writeMedium(i);
    }

    @Override
    public ByteBuf writeMediumLE(final int i) {
        return delegate.writeMediumLE(i);
    }

    @Override
    public ByteBuf writeInt(final int i) {
        return delegate.writeInt(i);
    }

    @Override
    public ByteBuf writeIntLE(final int i) {
        return delegate.writeIntLE(i);
    }

    @Override
    public ByteBuf writeLong(final long l) {
        return delegate.writeLong(l);
    }

    @Override
    public ByteBuf writeLongLE(final long l) {
        return delegate.writeLongLE(l);
    }

    @Override
    public ByteBuf writeChar(final int i) {
        return delegate.writeChar(i);
    }

    @Override
    public ByteBuf writeFloat(final float v) {
        return delegate.writeFloat(v);
    }

    @Override
    public ByteBuf writeDouble(final double v) {
        return delegate.writeDouble(v);
    }

    @Override
    public ByteBuf writeBytes(final ByteBuf byteBuf) {
        return delegate.writeBytes(byteBuf);
    }

    @Override
    public ByteBuf writeBytes(final ByteBuf byteBuf, final int i) {
        return delegate.writeBytes(byteBuf, i);
    }

    @Override
    public ByteBuf writeBytes(final ByteBuf byteBuf, final int i, final int i1) {
        return delegate.writeBytes(byteBuf, i, i1);
    }

    @Override
    public ByteBuf writeBytes(final byte[] bytes) {
        return delegate.writeBytes(bytes);
    }

    @Override
    public ByteBuf writeBytes(final byte[] bytes, final int i, final int i1) {
        return delegate.writeBytes(bytes, i, i1);
    }

    @Override
    public ByteBuf writeBytes(final ByteBuffer byteBuffer) {
        return delegate.writeBytes(byteBuffer);
    }

    @Override
    public int writeBytes(final InputStream inputStream, final int i) throws IOException {
        return delegate.writeBytes(inputStream, i);
    }

    @Override
    public int writeBytes(final ScatteringByteChannel scatteringByteChannel, final int i) throws IOException {
        return delegate.writeBytes(scatteringByteChannel, i);
    }

    @Override
    public int writeBytes(final FileChannel fileChannel, final long l, final int i) throws IOException {
        return delegate.writeBytes(fileChannel, l, i);
    }

    @Override
    public ByteBuf writeZero(final int i) {
        return delegate.writeZero(i);
    }

    @Override
    public int writeCharSequence(final CharSequence charSequence, final Charset charset) {
        return delegate.writeCharSequence(charSequence, charset);
    }

    @Override
    public int indexOf(final int i, final int i1, final byte b) {
        return delegate.indexOf(i, i1, b);
    }

    @Override
    public int bytesBefore(final byte b) {
        return delegate.bytesBefore(b);
    }

    @Override
    public int bytesBefore(final int i, final byte b) {
        return delegate.bytesBefore(i, b);
    }

    @Override
    public int bytesBefore(final int i, final int i1, final byte b) {
        return delegate.bytesBefore(i, i1, b);
    }

    @Override
    public int forEachByte(final ByteProcessor byteProcessor) {
        return delegate.forEachByte(byteProcessor);
    }

    @Override
    public int forEachByte(final int i, final int i1, final ByteProcessor byteProcessor) {
        return delegate.forEachByte(i, i1, byteProcessor);
    }

    @Override
    public int forEachByteDesc(final ByteProcessor byteProcessor) {
        return delegate.forEachByteDesc(byteProcessor);
    }

    @Override
    public int forEachByteDesc(final int i, final int i1, final ByteProcessor byteProcessor) {
        return delegate.forEachByteDesc(i, i1, byteProcessor);
    }

    @Override
    public ByteBuf copy() {
        return delegate.copy();
    }

    @Override
    public ByteBuf copy(final int i, final int i1) {
        return delegate.copy(i, i1);
    }

    @Override
    public ByteBuf slice() {
        return delegate.slice();
    }

    @Override
    public ByteBuf retainedSlice() {
        return delegate.retainedSlice();
    }

    @Override
    public ByteBuf slice(final int i, final int i1) {
        return delegate.slice(i, i1);
    }

    @Override
    public ByteBuf retainedSlice(final int i, final int i1) {
        return delegate.retainedSlice(i, i1);
    }

    @Override
    public ByteBuf duplicate() {
        return delegate.duplicate();
    }

    @Override
    public ByteBuf retainedDuplicate() {
        return delegate.retainedDuplicate();
    }

    @Override
    public int nioBufferCount() {
        return delegate.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer() {
        return delegate.nioBuffer();
    }

    @Override
    public ByteBuffer nioBuffer(final int i, final int i1) {
        return delegate.nioBuffer(i, i1);
    }

    @Override
    public ByteBuffer internalNioBuffer(final int i, final int i1) {
        return delegate.internalNioBuffer(i, i1);
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return delegate.nioBuffers();
    }

    @Override
    public ByteBuffer[] nioBuffers(final int i, final int i1) {
        return delegate.nioBuffers(i, i1);
    }

    @Override
    public boolean hasArray() {
        return delegate.hasArray();
    }

    @Override
    public byte[] array() {
        return delegate.array();
    }

    @Override
    public int arrayOffset() {
        return delegate.arrayOffset();
    }

    @Override
    public boolean hasMemoryAddress() {
        return delegate.hasMemoryAddress();
    }

    @Override
    public long memoryAddress() {
        return delegate.memoryAddress();
    }

    @Override
    public String toString(final Charset charset) {
        return delegate.toString(charset);
    }

    @NotNull
    @Override
    public String toString(final int i, final int i1, final Charset charset) {
        return delegate.toString(i, i1, charset);
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof FriendlyByteBuf that)) return false;
        return delegate.equals(that.delegate);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public int compareTo(final ByteBuf byteBuf) {
        return delegate.compareTo(byteBuf);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public ByteBuf retain(final int i) {
        return delegate.retain(i);
    }

    @Override
    public int refCnt() {
        return delegate.refCnt();
    }

    @Override
    public ByteBuf retain() {
        return delegate.retain();
    }

    @Override
    public ByteBuf touch() {
        return delegate.touch();
    }

    @Override
    public ByteBuf touch(final Object o) {
        return delegate.touch(o);
    }

    @Override
    public boolean release() {
        return delegate.release();
    }

    @Override
    public boolean release(final int decrement) {
        return delegate.release(decrement);
    }

    //</editor-fold>

}
