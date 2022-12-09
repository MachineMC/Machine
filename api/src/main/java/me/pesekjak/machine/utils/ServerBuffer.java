package me.pesekjak.machine.utils;

import me.pesekjak.machine.Server;
import me.pesekjak.machine.auth.MessageSignature;
import me.pesekjak.machine.auth.PublicKeyData;
import me.pesekjak.machine.entities.player.PlayerTextures;
import me.pesekjak.machine.inventory.Item;
import me.pesekjak.machine.world.BlockPosition;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Special byte buffer for implementing Minecraft Protocol.
 */
public interface ServerBuffer extends Cloneable {

    /**
     * Creates new instance of the classic buffer implementation.
     * @return new default server buffer
     * @throws UnsupportedOperationException if the creator hasn't been initialized
     */
    static @NotNull ServerBuffer create() {
        return Server.createServerBuffer();
    }

    /**
     * Returns content of the full buffer, doesn't move with
     * reader index and starts from byte 0.
     * @return all bytes of the buffer
     */
    byte @NotNull [] bytes();

    /**
     * Reads all remaining bytes of the buffer, moves
     * reader index at the end.
     * @return all remaining bytes of the buffer to read
     */
    byte @NotNull [] finish();

    /**
     * @return data output stream with all bytes of this buffer
     * @throws IOException if an I/O error occurs during writing the bytes
     */
    @Contract(pure = true)
    @NotNull DataOutputStream stream() throws IOException;

    /**
     * Writes the bytes of this buffer in a data output stream
     * @param stream stream to write into
     * @return given stream
     * @throws IOException if an I/O error occurs during writing the bytes
     */
    @Contract("_ -> param1")
    @NotNull DataOutputStream writeToStream(@NotNull DataOutputStream stream) throws IOException;

    /**
     * Writes a writable object into this buffer
     * @param writable object to write
     * @return this
     */
    @Contract("_ -> this")
    @NotNull ServerBuffer write(@NotNull Writable writable);

    /**
     * @return next boolean
     */
    boolean readBoolean();

    /**
     * @param value boolean to write
     * @return this
     */
    @Contract("_ -> this")
    @NotNull ServerBuffer writeBoolean(boolean value);

    /**
     * @return next byte
     */
    byte readByte();

    /**
     * @param value byte to write
     * @return this
     */
    @Contract("_ -> this")
    @NotNull ServerBuffer writeByte(byte value);

    /**
     * Reads multiple bytes in a row and returns an array.
     * @param length how many bytes to read
     * @return array of the bytes
     */
    byte @NotNull [] readBytes(int length);

    /**
     * Writes multiple bytes in the buffer.
     * @param bytes bytes to write
     * @return this
     */
    @Contract("_ -> this")
    @NotNull ServerBuffer writeBytes(byte @NotNull ... bytes);

    /**
     * @return next byte array
     */
    byte @NotNull [] readByteArray();

    /**
     * @param bytes byte array to write
     * @return this
     */
    @Contract("_ -> this")
    @NotNull ServerBuffer writeByteArray(byte @NotNull [] bytes);

    /**
     * @return next short
     */
    short readShort();

    /**
     * @param value short to write
     * @return this
     */
    @Contract("_ -> this")
    @NotNull ServerBuffer writeShort(short value);

    /**
     * @return next integer
     */
    int readInt();

    /**
     * @param value integer to write
     * @return this
     */
    @Contract("_ -> this")
    @NotNull ServerBuffer writeInt(int value);

    /**
     * @return next long
     */
    long readLong();

    /**
     * @param value long to write
     * @return this
     */
    @Contract("_ -> this")
    @NotNull ServerBuffer writeLong(long value);

    /**
     * @return next long array
     */
    long[] readLongArray();

    /**
     * @param longs long array to write
     * @return this
     */
    @Contract("_ -> this")
    @NotNull ServerBuffer writeLongArray(long @NotNull [] longs);

    /**
     * @return next float
     */
    float readFloat();

    /**
     * @param value float to write
     * @return this
     */
    @Contract("_ -> this")
    @NotNull ServerBuffer writeFloat(float value);

    /**
     * @return next double
     */
    double readDouble();

    /**
     * @param value double to write
     * @return this
     */
    @Contract("_ -> this")
    @NotNull ServerBuffer writeDouble(double value);

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
    @NotNull ServerBuffer writeVarInt(int value);

    /**
     * @return next VarInt array
     */
    int @NotNull [] readVarIntArray();

    /**
     * @param ints VarInt array to write
     * @return this
     */
    @Contract("_ -> this")
    @NotNull ServerBuffer writeVarIntArray(int @NotNull [] ints);

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
    @NotNull ServerBuffer writeVarLong(long value);

    /**
     * @param charset charset used by the string
     * @return next string
     */
    @Contract("_ -> new")
    @NotNull String readString(@NotNull Charset charset);

    /**
     * @param value string to write
     * @param charset charset used by the string
     * @return this
     */
    @Contract("_, _, -> this")
    @NotNull ServerBuffer writeString(@NotNull String value, @NotNull Charset charset);

    /**
     * @param charset charset used by the strings in the list
     * @return next string list
     */
    @Contract("_ -> new")
    @NotNull List<String> readStringList(@NotNull Charset charset);

    /**
     *
     * @param strings string list to write
     * @param charset charset used by the strings in the list
     * @return this
     */
    @Contract("_, _ -> new")
    @NotNull ServerBuffer writeStringList(@NotNull List<String> strings, @NotNull Charset charset);

    /**
     * @return next uuid
     */
    @Contract("-> new")
    @NotNull UUID readUUID();

    /**
     * @param uuid uuid to write
     * @return this
     */
    @Contract("_ -> this")
    @NotNull ServerBuffer writeUUID(@NotNull UUID uuid);

    /**
     * @return next block position
     */
    @Contract("-> new")
    @NotNull BlockPosition readBlockPos();

    /**
     * @param position block position to write
     * @return this
     */
    @Contract("_ -> this")
    @NotNull ServerBuffer writeBlockPos(@NotNull BlockPosition position);

    /**
     * @return next nbt
     */
    @Contract("-> new")
    @NotNull NBT readNBT();

    /**
     * @param name name of the NBT
     * @param tag NBT to write
     * @return this
     */
    @Contract("_, _ -> this")
    @NotNull ServerBuffer writeNBT(@NotNull String name, @NotNull NBT tag);

    /**
     * @return next component
     */
    @Contract("-> new")
    @NotNull Component readComponent();

    /**
     * @param component component to write
     * @return this
     */
    @Contract("_ -> this")
    @NotNull ServerBuffer writeComponent(@NotNull Component component);

    /**
     * @return next namespaced key
     */
    @Contract("-> new")
    @NotNull NamespacedKey readNamespacedKey();

    /**
     * @param namespacedKey namespaced key to write
     * @return this
     */
    @Contract("_ -> this")
    @NotNull ServerBuffer writeNamespacedKey(@NotNull NamespacedKey namespacedKey);

    /**
     * @return next instant
     */
    @Contract("-> new")
    @NotNull Instant readInstant();

    /**
     * @param instant instant to write
     * @return this
     */
    @Contract("_ -> this")
    @NotNull ServerBuffer writeInstant(@NotNull Instant instant);

    /**
     * @return next item
     */
    @Contract("-> new")
    @NotNull Item readSlot();

    /**
     * @param itemStack item to write
     * @return this
     */
    @Contract("_ -> this")
    @NotNull ServerBuffer writeSlot(@NotNull Item itemStack);

    /**
     * @return next public key data
     */
    @Contract("-> new")
    @NotNull PublicKeyData readPublicKey();

    /**
     * @param publicKeyData public key data to write
     * @return this
     */
    @Contract("_ -> this")
    @NotNull ServerBuffer writePublicKey(@NotNull PublicKeyData publicKeyData);

    /**
     * @return next player textures
     */
    @Contract("-> _")
    @Nullable PlayerTextures readTextures();

    /**
     * @param playerSkin player textures to write
     * @return this
     */
    @Contract("_ -> this")
    @NotNull ServerBuffer writeTextures(@Nullable PlayerTextures playerSkin);

    /**
     * @return next message signature
     */
    @Contract("-> new")
    @NotNull MessageSignature readSignature();

    /**
     * @param messageSignature message signature to write
     * @return this
     */
    @Contract("_ -> this")
    @NotNull ServerBuffer writeSignature(@NotNull MessageSignature messageSignature);

    /**
     * @return next angle
     */
    float readAngle();

    /**
     * @param angle angle to write
     * @return this
     */
    @Contract("_ -> this")
    @NotNull ServerBuffer writeAngle(float angle);

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
    @NotNull ServerBuffer setReaderIndex(int index);

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
    @NotNull ServerBuffer setWriterIndex(int index);

    @NotNull ServerBuffer clone();

}
