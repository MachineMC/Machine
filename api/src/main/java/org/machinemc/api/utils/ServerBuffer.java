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
package org.machinemc.api.utils;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.machinemc.api.auth.PublicKeyData;
import org.machinemc.api.entities.player.PlayerTextures;
import org.machinemc.api.inventory.Item;
import org.machinemc.api.world.BlockPosition;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.serialization.ComponentProperties;
import org.machinemc.scriptive.serialization.ComponentSerializer;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * Special byte buffer for implementing Minecraft Protocol.
 */
public interface ServerBuffer extends Cloneable {

    /**
     * Returns content of the full buffer, doesn't move with
     * reader index and starts from byte 0.
     * @return all bytes of the buffer
     */
    byte[] bytes();

    /**
     * Reads all remaining bytes of the buffer, moves
     * reader index at the end.
     * @return all remaining bytes of the buffer to read
     */
    byte[] finish();

    /**
     * Reads an object from the buffer using a function.
     * @param function function to initiate the instance
     * @param <T> type of the item
     * @return the object
     */
    @Contract("_ -> new")
    <T> T read(Function<ServerBuffer, T> function);

    /**
     * Writes an object to the buffer using a consumer.
     * @param item item to write
     * @param consumer consumer that writes to the buffer
     * @param <T> type of the item
     * @return this
     */
    @Contract("_, _ -> this")
    <T> FriendlyByteBuf write(@Nullable T item, BiConsumer<ServerBuffer, T> consumer);

    /**
     * Writes a writable object into this buffer.
     * @param writable object to write
     * @return this
     */
    @Contract("_ -> this")
    ServerBuffer write(Writable writable);

    /**
     * Writes contents of another server buffer into this buffer.
     * @param buf buf to write
     * @return this
     */
    @Contract("_ -> this")
    ServerBuffer write(ServerBuffer buf);

    /**
     * Writes contents of another server buffer into this buffer.
     * @param buf buf to write
     * @param length number of bytes to write
     * @return this
     */
    @Contract("_, _ -> this")
    ServerBuffer write(ServerBuffer buf, int length);

    /**
     * Writes contents of another server buffer into this buffer.
     * @param buf buf to write
     * @return this
     */
    @Contract("_ -> this")
    ServerBuffer write(ByteBuf buf);

    /**
     * Writes contents of another server buffer into this buffer.
     * @param buf buf to write
     * @param length number of bytes to write
     * @return this
     */
    @Contract("_, _ -> this")
    ServerBuffer write(ByteBuf buf, int length);

    /**
     * Writes contents of another server buffer into this buffer.
     * @param buf buf to write
     * @return this
     */
    @Contract("_ -> this")
    ServerBuffer write(ByteBuffer buf);

    /**
     * Writes contents of another server buffer into this buffer.
     * @param buf buf to write
     * @param length number of bytes to write
     * @return this
     */
    @Contract("_, _ -> this")
    ServerBuffer write(ByteBuffer buf, int length);

    /**
     * Reads an array using a function.
     * @param generator a function which produces a new array of the desired type and the provided length
     * @param function function to initiate the read items
     * @param <T> type of the array
     * @return array
     */
    <T> T[] readArray(IntFunction<T[]> generator, Function<ServerBuffer, T> function);

    /**
     * Writes an array to the buffer.
     * @param array array to write
     * @param consumer consumer for writing
     * @param <T> type of the array
     * @return this
     */
    @Contract("_, _ -> this")
    <T> ServerBuffer writeArray(T[] array, BiConsumer<ServerBuffer, T> consumer);

    /**
     * Reads a list using a function.
     * @param function function to initiate the read items
     * @param <T> type of the list
     * @return list
     */
    @Contract("_ -> new")
    <T> @Unmodifiable List<T> readList(Function<ServerBuffer, T> function);

    /**
     * Writes a list to the buffer.
     * @param list list to write
     * @param consumer consumer for writing
     * @param <T> type of the list
     * @return this
     */
    @Contract("_, _ -> this")
    <T> FriendlyByteBuf writeList(List<T> list, BiConsumer<ServerBuffer, T> consumer);

    /**
     * Reads an optional value using a function.
     * @param function function to initiate the read items
     * @param <T> type of the optional value
     * @return optional value
     */
    <T> Optional<T> readOptional(Function<ServerBuffer, T> function);

    /**
     * Writes an optional value to the buffer.
     * @param value optional value to write
     * @param consumer consumer for writing
     * @param <T> type of the optional value
     * @return this
     */
    @Contract("_, _ -> this")
    <T> ServerBuffer writeOptional(@Nullable T value, BiConsumer<ServerBuffer, T> consumer);

    /**
     * @return next boolean
     */
    boolean readBoolean();

    /**
     * @param value boolean to write
     * @return this
     */
    @Contract("_ -> this")
    ServerBuffer writeBoolean(boolean value);

    /**
     * @return next byte
     */
    byte readByte();

    /**
     * @param value byte to write
     * @return this
     */
    @Contract("_ -> this")
    ServerBuffer writeByte(byte value);

    /**
     * Reads multiple bytes in a row and returns an array.
     * @param length how many bytes to read
     * @return array of the bytes
     */
    byte[] readBytes(int length);

    /**
     * Writes multiple bytes in the buffer.
     * @param bytes bytes to write
     * @return this
     */
    @Contract("_ -> this")
    ServerBuffer writeBytes(byte... bytes);

    /**
     * @return next byte array
     */
    byte[] readByteArray();

    /**
     * @param bytes byte array to write
     * @return this
     */
    @Contract("_ -> this")
    ServerBuffer writeByteArray(byte[] bytes);

    /**
     * @return next short
     */
    short readShort();

    /**
     * @param value short to write
     * @return this
     */
    @Contract("_ -> this")
    ServerBuffer writeShort(short value);

    /**
     * @return next integer
     */
    int readInt();

    /**
     * @param value integer to write
     * @return this
     */
    @Contract("_ -> this")
    ServerBuffer writeInt(int value);

    /**
     * @return next long
     */
    long readLong();

    /**
     * @param value long to write
     * @return this
     */
    @Contract("_ -> this")
    ServerBuffer writeLong(long value);

    /**
     * @return next long array
     */
    long[] readLongArray();

    /**
     * @param longs long array to write
     * @return this
     */
    @Contract("_ -> this")
    ServerBuffer writeLongArray(long[] longs);

    /**
     * @return next float
     */
    float readFloat();

    /**
     * @param value float to write
     * @return this
     */
    @Contract("_ -> this")
    ServerBuffer writeFloat(float value);

    /**
     * @return next double
     */
    double readDouble();

    /**
     * @param value double to write
     * @return this
     */
    @Contract("_ -> this")
    ServerBuffer writeDouble(double value);

    /**
     * @return next VarInt
     * @see <a href="https://wiki.vg/Protocol#VarInt_and_VarLong">VatInt and VarLong</a>
     */
    int readVarInt();

    /**
     * @param value VarInt to write
     * @return this
     * @see <a href="https://wiki.vg/Protocol#VarInt_and_VarLong">VatInt and VarLong</a>
     */
    @Contract("_ -> this")
    ServerBuffer writeVarInt(int value);

    /**
     * @return next VarInt array
     */
    int[] readVarIntArray();

    /**
     * @param ints VarInt array to write
     * @return this
     */
    @Contract("_ -> this")
    ServerBuffer writeVarIntArray(int[] ints);

    /**
     * @return next VarLong
     * @see <a href="https://wiki.vg/Protocol#VarInt_and_VarLong">VatInt and VarLong</a>
     */
    long readVarLong();

    /**
     * @param value VarLong to write
     * @return this
     * @see <a href="https://wiki.vg/Protocol#VarInt_and_VarLong">VatInt and VarLong</a>
     */
    @Contract("_ -> this")
    ServerBuffer writeVarLong(long value);

    /**
     * @param charset charset used by the string
     * @return next string
     */
    @Contract("_ -> new")
    String readString(Charset charset);

    /**
     * @param value string to write
     * @param charset charset used by the string
     * @return this
     */
    @Contract("_, _, -> this")
    ServerBuffer writeString(String value, Charset charset);

    /**
     * @param charset charset used by the strings in the list
     * @return next string list
     */
    @Contract("_ -> new")
    @Unmodifiable List<String> readStringList(Charset charset);

    /**
     * @param strings string list to write
     * @param charset charset used by the strings in the list
     * @return this
     */
    @Contract("_, _ -> this")
    ServerBuffer writeStringList(List<String> strings, Charset charset);

    /**
     * @return next uuid
     */
    @Contract("-> new")
    UUID readUUID();

    /**
     * @param uuid uuid to write
     * @return this
     */
    @Contract("_ -> this")
    ServerBuffer writeUUID(UUID uuid);

    /**
     * @return next bitset
     */
    @Contract("-> new")
    BitSet readBitSet();

    /**
     * @param size size of the bitset
     * @return next bitset
     */
    @Contract("_ -> new")
    BitSet readBitSet(int size);

    /**
     * @param bitSet bitset to write
     * @return this
     */
    @Contract("_ -> this")
    ServerBuffer writeBitSet(BitSet bitSet);

    /**
     * @param bitSet bitset to write
     * @param size size of the bitset
     * @return this
     */
    @Contract("_, _ -> this")
    ServerBuffer writeBitSet(BitSet bitSet, int size);

    /**
     * @return next block position
     */
    @Contract("-> new")
    BlockPosition readBlockPos();

    /**
     * @param position block position to write
     * @return this
     */
    @Contract("_ -> this")
    ServerBuffer writeBlockPos(BlockPosition position);

    /**
     * @return next nbt
     */
    @Contract("-> new")
    NBTCompound readNBT();

    /**
     * @param tag NBT to write
     * @return this
     */
    @Contract("_ -> this")
    ServerBuffer writeNBT(NBTCompound tag);

    /**
     * @return next component
     */
    @Contract("-> new")
    ComponentProperties readComponent();

    /**
     * @param component component to write
     * @return this
     */
    @Contract("_ -> this")
    default ServerBuffer writeComponent(Component component) {
        return writeComponent(component.getProperties());
    }

    /**
     * @param properties component to write
     * @return this
     */
    @Contract("_ -> this")
    ServerBuffer writeComponent(ComponentProperties properties);

    /**
     * @return next namespaced key
     */
    @Contract("-> new")
    NamespacedKey readNamespacedKey();

    /**
     * @param namespacedKey namespaced key to write
     * @return this
     */
    @Contract("_ -> this")
    ServerBuffer writeNamespacedKey(NamespacedKey namespacedKey);

    /**
     * @return next instant
     */
    @Contract("-> new")
    Instant readInstant();

    /**
     * @param instant instant to write
     * @return this
     */
    @Contract("_ -> this")
    ServerBuffer writeInstant(Instant instant);

    /**
     * @return next item
     */
    @Contract("-> new")
    Item readSlot();

    /**
     * @param itemStack item to write
     * @return this
     */
    @Contract("_ -> this")
    ServerBuffer writeSlot(Item itemStack);

    /**
     * @return next public key data
     */
    @Contract("-> new")
    PublicKeyData readPublicKey();

    /**
     * @param publicKeyData public key data to write
     * @return this
     */
    @Contract("_ -> this")
    ServerBuffer writePublicKey(PublicKeyData publicKeyData);

    /**
     * @return next player textures
     */
    @Contract("-> _")
    Optional<PlayerTextures> readTextures();

    /**
     * @param playerSkin player textures to write
     * @return this
     */
    @Contract("_ -> this")
    ServerBuffer writeTextures(@Nullable PlayerTextures playerSkin);

    /**
     * @return next angle
     */
    float readAngle();

    /**
     * @param angle angle to write
     * @return this
     */
    @Contract("_ -> this")
    ServerBuffer writeAngle(float angle);

    /**
     * @return number of readable bytes (difference between reader and writer index)
     */
    int readableBytes();

    /**
     * @return index of the reader
     */
    int readerIndex();

    /**
     * Changes the index of the reader.
     * @param index new index
     * @return this
     */
    @Contract("_ -> this")
    ServerBuffer readerIndex(int index);

    /**
     * @return index of the writer
     */
    int writerIndex();

    /**
     * Changes the index of the writer.
     * @param index new index
     * @return this
     */
    @Contract("_ -> this")
    ServerBuffer writerIndex(int index);

    /**
     * Deallocates this buffer.
     */
    void release();

    /**
     * @return clone of this buffer
     */
    ServerBuffer clone();

}
