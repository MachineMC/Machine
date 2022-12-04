package me.pesekjak.machine.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import me.pesekjak.machine.auth.*;
import me.pesekjak.machine.entities.player.PlayerTextures;
import me.pesekjak.machine.entities.player.PlayerTexturesImpl;
import me.pesekjak.machine.inventory.Item;
import me.pesekjak.machine.inventory.ItemStack;
import me.pesekjak.machine.world.BlockPosition;
import me.pesekjak.machine.world.Material;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Special ByteBuffer for implementing Minecraft Protocol.
 */
public class FriendlyByteBuf implements ServerBuffer {

    private final ByteBuf buf;

    private static final int SEGMENT_BITS = 0x7F;
    private static final int CONTINUE_BIT = 0x80;

    public FriendlyByteBuf() {
        this(new byte[0]);
    }

    public FriendlyByteBuf(byte[] bytes) {
        buf = Unpooled.buffer(0);
        buf.writeBytes(bytes);
    }

    public FriendlyByteBuf(ByteBuf byteBuf) {
        buf = byteBuf;
    }

    public FriendlyByteBuf(DataInputStream dataInputStream) throws IOException {
        this(dataInputStream.readAllBytes());
    }

    /**
     * Returns content of the full buffer, doesn't move with
     * reader index and starts from byte 0.
     * @return all bytes of the buffer
     */
    public byte @NotNull [] bytes() {
        int length = buf.writerIndex();
        int reader = buf.readerIndex();
        buf.readerIndex(0);
        byte[] bytes = new byte[length];
        for(int i = 0; i < length; i++)
            bytes[i] = readByte();
        buf.readerIndex(reader);
        return bytes;
    }

    /**
     * Reads all remaining bytes of the buffer, moves
     * reader index at the end.
     * @return all remaining bytes of the buffer to read
     */
    public byte @NotNull [] finish() {
        int length = buf.writerIndex();
        int reader = buf.readerIndex();
        byte[] bytes = new byte[length - reader];
        for(int i = 0; i < length - reader; i++)
            bytes[i] = readByte();
        return bytes;
    }

    public @NotNull DataOutputStream stream() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(buffer);
        stream.write(bytes());
        return stream;
    }

    public @NotNull DataOutputStream writeToStream(@NotNull DataOutputStream stream) throws IOException {
        stream.write(bytes());
        return stream;
    }

    public @NotNull FriendlyByteBuf write(@NotNull Writable writable) {
        writable.write(this);
        return this;
    }

    public boolean readBoolean() {
        return buf.readBoolean();
    }

    public @NotNull FriendlyByteBuf writeBoolean(boolean value) {
        buf.writeBoolean(value);
        return this;
    }

    public byte readByte() {
        return buf.readByte();
    }

    public @NotNull FriendlyByteBuf writeByte(byte value) {
        buf.writeByte(value);
        return this;
    }

    public byte @NotNull [] readBytes(int length) {
        byte[] result = new byte[length];
        for(int i = 0; i < length; i++)
            result[i] = readByte();
        return result;
    }

    public @NotNull FriendlyByteBuf writeBytes(byte @NotNull ... bytes) {
        buf.writeBytes(bytes);
        return this;
    }

    public byte @NotNull [] readByteArray() {
        int length = readVarInt();
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++)
            bytes[i] = readByte();
        return bytes;
    }

    public @NotNull FriendlyByteBuf writeByteArray(byte @NotNull [] bytes) {
        writeVarInt(bytes.length);
        for (byte b : bytes)
            writeByte(b);
        return this;
    }

    public short readShort() {
        return buf.readShort();
    }

    public @NotNull FriendlyByteBuf writeShort(short value) {
        buf.writeShort(value);
        return this;
    }

    public int readInt() {
        return buf.readInt();
    }

    public @NotNull FriendlyByteBuf writeInt(int value) {
        buf.writeInt(value);
        return this;
    }

    public long readLong() {
        return buf.readLong();
    }

    public @NotNull FriendlyByteBuf writeLong(long value) {
        buf.writeLong(value);
        return this;
    }

    public long[] readLongArray() {
        int length = readVarInt();
        long[] longs = new long[length];
        for(int i = 0; i < length; i++)
            longs[i] = readLong();
        return longs;
    }

    public @NotNull FriendlyByteBuf writeLongArray(long @NotNull [] longs) {
        writeVarInt(longs.length);
        for(long l : longs)
            writeLong(l);
        return this;
    }

    public float readFloat() {
        return buf.readFloat();
    }

    public @NotNull FriendlyByteBuf writeFloat(float value) {
        buf.writeFloat(value);
        return this;
    }

    public double readDouble() {
        return buf.readDouble();
    }

    public @NotNull FriendlyByteBuf writeDouble(double value) {
        buf.writeDouble(value);
        return this;
    }

    public int readVarInt() {
        int value = 0;
        int position = 0;
        byte currentByte;
        while (true) {
            currentByte = readByte();
            value |= (currentByte & SEGMENT_BITS) << position;
            if ((currentByte & CONTINUE_BIT) == 0) break;
            position += 7;
            if (position >= 32) throw new RuntimeException("VarInt is too big");
        }
        return value;
    }

    public @NotNull FriendlyByteBuf writeVarInt(int value) {
        while (true) {
            if ((value & ~SEGMENT_BITS) == 0) {
                writeByte((byte) value);
                return this;
            }
            writeByte((byte) ((value & SEGMENT_BITS) | CONTINUE_BIT));
            value >>>= 7;
        }
    }

    public int @NotNull [] readVarIntArray() {
        int length = readVarInt();
        int[] ints = new int[length];
        for(int i = 0; i < length; i++)
            ints[i] = readVarInt();
        return ints;
    }

    public @NotNull FriendlyByteBuf writeVarIntArray(int @NotNull [] ints) {
        writeVarInt(ints.length);
        for(int i : ints)
            writeVarInt(i);
        return this;
    }

    public long readVarLong() {
        long value = 0;
        int position = 0;
        byte currentByte;
        while (true) {
            currentByte = readByte();
            value |= (long) (currentByte & SEGMENT_BITS) << position;
            if ((currentByte & CONTINUE_BIT) == 0) break;
            position += 7;
            if (position >= 64) throw new RuntimeException("VarLong is too big");
        }
        return value;
    }

    public @NotNull FriendlyByteBuf writeVarLong(long value) {
        while (true) {
            if ((value & ~((long) SEGMENT_BITS)) == 0) {
                writeByte((byte) value);
                return this;
            }
            writeByte((byte) ((value & SEGMENT_BITS) | CONTINUE_BIT));
            value >>>= 7;
        }
    }

    public @NotNull String readString(@NotNull Charset charset) {
        int length = readVarInt();
        if (length == -1) return null;
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++)
            bytes[i] = buf.readByte();
        return new String(bytes, charset);
    }

    public @NotNull FriendlyByteBuf writeString(@NotNull String value, @NotNull Charset charset) {
        byte[] bytes = value.getBytes(charset);
        writeVarInt(bytes.length);
        buf.writeBytes(bytes);
        return this;
    }

    public @NotNull List<String> readStringList(@NotNull Charset charset) {
        final List<String> strings = new ArrayList<>();
        int length = readVarInt();
        for (int i = 0; i < length; i++)
            strings.add(readString(charset));
        return strings;
    }

    public @NotNull FriendlyByteBuf writeStringList(@NotNull List<String> strings, @NotNull Charset charset) {
        writeVarInt(strings.size());
        for(String string : strings)
            writeString(string, charset);
        return this;
    }

    public @NotNull UUID readUUID() {
        return new UUID(readLong(), readLong());
    }

    public @NotNull FriendlyByteBuf writeUUID(@NotNull UUID uuid) {
        writeLong(uuid.getMostSignificantBits());
        writeLong(uuid.getLeastSignificantBits());
        return this;
    }

    public @NotNull BlockPosition readBlockPos() {
        return BlockPosition.of(readLong());
    }

    public @NotNull FriendlyByteBuf writeBlockPos(@NotNull BlockPosition position) {
        writeLong(position.asLong());
        return this;
    }

    public @NotNull NBT readNBT() {
        byte[] bytes = buf.array();
        ByteArrayInputStream buffer = new ByteArrayInputStream(bytes, buf.readerIndex(), bytes.length);
        NBTReader reader = new NBTReader(buffer, CompressedProcesser.NONE);
        NBT tag = null;
        try {
            tag = reader.read();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        buf.readerIndex(bytes.length - buffer.available());
        return tag;
    }

    public @NotNull FriendlyByteBuf writeNBT(@NotNull String name, @NotNull NBT tag) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        NBTWriter writer = new NBTWriter(buffer, CompressedProcesser.NONE);
        try {
            writer.writeNamed(name, tag);
        } catch (IOException ignored) { }
        buf.writeBytes(buffer.toByteArray());
        return this;
    }

    public @NotNull Component readComponent() {
        return GsonComponentSerializer.gson().deserialize(readString(StandardCharsets.UTF_8));
    }

    public @NotNull FriendlyByteBuf writeComponent(@NotNull Component component) {
        writeString(GsonComponentSerializer.gson().serialize(component), StandardCharsets.UTF_8);
        return this;
    }

    public @NotNull NamespacedKey readNamespacedKey() {
        final @Subst("machine:server") String namespaceKey = readString(StandardCharsets.UTF_8);
        return NamespacedKey.parse(namespaceKey);
    }

    public @NotNull FriendlyByteBuf writeNamespacedKey(@NotNull NamespacedKey namespacedKey) {
        writeString(namespacedKey.toString(), StandardCharsets.UTF_8);
        return this;
    }

    public @NotNull Instant readInstant() {
        return Instant.ofEpochMilli(readLong());
    }

    public @NotNull FriendlyByteBuf writeInstant(@NotNull Instant instant) {
        buf.writeLong(instant.toEpochMilli());
        return this;
    }

    public @NotNull Item readSlot() {
        if(!readBoolean())
            return new ItemStack(Material.AIR);
        ItemStack itemStack = new ItemStack(ItemStack.getMaterial(readVarInt()), readByte());
        itemStack.setNbtCompound((NBTCompound) readNBT());
        return itemStack;
    }

    public @NotNull FriendlyByteBuf writeSlot(@NotNull Item itemStack) {
        writeBytes(itemStack.serialize());
        return this;
    }

    public @NotNull PublicKeyDataImpl readPublicKey() {
        Instant instant = Instant.ofEpochMilli(readLong());
        return new PublicKeyDataImpl(Crypt.pubicKeyFrom(readByteArray()), readByteArray(), instant);
    }

    public @NotNull FriendlyByteBuf writePublicKey(@NotNull PublicKeyData publicKeyData) {
        writeLong(publicKeyData.timestamp().toEpochMilli())
                .writeByteArray(publicKeyData.publicKey().getEncoded())
                .writeByteArray(publicKeyData.signature());
        return this;
    }

    public @Nullable PlayerTexturesImpl readTextures() {
        if (readVarInt() == 0)
            return null;
        readString(StandardCharsets.UTF_8);
        String value = readString(StandardCharsets.UTF_8);
        String signature = null;
        if (readBoolean())
            signature = readString(StandardCharsets.UTF_8);
        try {
            return PlayerTexturesImpl.buildSkin(value, signature);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public @NotNull FriendlyByteBuf writeTextures(@Nullable PlayerTextures playerSkin) {
        if (playerSkin == null) {
            writeVarInt(0);
            return this;
        }
        writeVarInt(1);
        writeString("textures", StandardCharsets.UTF_8);
        writeString(playerSkin.value(), StandardCharsets.UTF_8);
        final String signature = playerSkin.signature();
        if (signature != null) {
            writeBoolean(true);
            writeString(signature, StandardCharsets.UTF_8);
        } else writeBoolean(false);
        return this;
    }

    public @NotNull MessageSignatureImpl readSignature() {
        Instant timestamp = readInstant();
        long salt = readLong();
        byte[] signature = readByteArray();
        return new MessageSignatureImpl(timestamp, salt, signature);
    }

    public @NotNull FriendlyByteBuf writeSignature(@NotNull MessageSignature messageSignature) {
        write(messageSignature);
        return this;
    }

    public float readAngle() {
        return readByte() * 360f / 256f;
    }

    public @NotNull FriendlyByteBuf writeAngle(float angle) {
        writeByte((byte) (angle * 256f / 360f));
        return this;
    }

    public int readableBytes() {
        return buf.readableBytes();
    }

    @Override
    public int readerIndex() {
        return buf.readerIndex();
    }

    @Override
    public @NotNull ServerBuffer setReaderIndex(int index) {
        buf.readerIndex(index);
        return this;
    }

    @Override
    public int writerIndex() {
        return buf.writerIndex();
    }

    @Override
    public @NotNull ServerBuffer setWriterIndex(int index) {
        buf.writerIndex(index);
        return this;
    }

}
