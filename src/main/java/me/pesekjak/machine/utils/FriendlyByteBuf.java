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
 * Special byte buffer for implementing Minecraft Protocol.
 */
public class FriendlyByteBuf implements ServerBuffer {

    private final @NotNull ByteBuf buf;

    private static final int SEGMENT_BITS = 0x7F;
    private static final int CONTINUE_BIT = 0x80;

    public FriendlyByteBuf() {
        this(new byte[0]);
    }

    public FriendlyByteBuf(byte @NotNull [] bytes) {
        buf = Unpooled.buffer(0);
        buf.writeBytes(bytes);
    }

    public FriendlyByteBuf(@NotNull ByteBuf byteBuf) {
        buf = byteBuf;
    }

    public FriendlyByteBuf(@NotNull DataInputStream dataInputStream) throws IOException {
        this(dataInputStream.readAllBytes());
    }

    @Override
    public byte @NotNull [] bytes() {
        final int length = buf.writerIndex();
        final int reader = buf.readerIndex();
        buf.readerIndex(0);
        final byte[] bytes = new byte[length];
        for(int i = 0; i < length; i++)
            bytes[i] = readByte();
        buf.readerIndex(reader);
        return bytes;
    }

    @Override
    public byte @NotNull [] finish() {
        final int length = buf.writerIndex();
        final int reader = buf.readerIndex();
        byte[] bytes = new byte[length - reader];
        for(int i = 0; i < length - reader; i++)
            bytes[i] = readByte();
        return bytes;
    }

    @Override
    public @NotNull DataOutputStream stream() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(buffer);
        stream.write(bytes());
        return stream;
    }

    @Override
    public @NotNull DataOutputStream writeToStream(@NotNull DataOutputStream stream) throws IOException {
        stream.write(bytes());
        return stream;
    }

    @Override
    public @NotNull FriendlyByteBuf write(@NotNull Writable writable) {
        writable.write(this);
        return this;
    }

    @Override
    public boolean readBoolean() {
        return buf.readBoolean();
    }

    @Override
    public @NotNull FriendlyByteBuf writeBoolean(boolean value) {
        buf.writeBoolean(value);
        return this;
    }

    @Override
    public byte readByte() {
        return buf.readByte();
    }

    @Override
    public @NotNull FriendlyByteBuf writeByte(byte value) {
        buf.writeByte(value);
        return this;
    }

    @Override
    public byte @NotNull [] readBytes(int length) {
        final byte[] result = new byte[length];
        for(int i = 0; i < length; i++)
            result[i] = readByte();
        return result;
    }

    @Override
    public @NotNull FriendlyByteBuf writeBytes(byte @NotNull ... bytes) {
        buf.writeBytes(bytes);
        return this;
    }

    @Override
    public byte @NotNull [] readByteArray() {
        int length = readVarInt();
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++)
            bytes[i] = readByte();
        return bytes;
    }

    @Override
    public @NotNull FriendlyByteBuf writeByteArray(byte @NotNull [] bytes) {
        writeVarInt(bytes.length);
        for (byte b : bytes)
            writeByte(b);
        return this;
    }

    @Override
    public short readShort() {
        return buf.readShort();
    }

    @Override
    public @NotNull FriendlyByteBuf writeShort(short value) {
        buf.writeShort(value);
        return this;
    }

    @Override
    public int readInt() {
        return buf.readInt();
    }

    @Override
    public @NotNull FriendlyByteBuf writeInt(int value) {
        buf.writeInt(value);
        return this;
    }

    @Override
    public long readLong() {
        return buf.readLong();
    }

    @Override
    public @NotNull FriendlyByteBuf writeLong(long value) {
        buf.writeLong(value);
        return this;
    }

    @Override
    public long[] readLongArray() {
        int length = readVarInt();
        long[] longs = new long[length];
        for(int i = 0; i < length; i++)
            longs[i] = readLong();
        return longs;
    }

    @Override
    public @NotNull FriendlyByteBuf writeLongArray(long @NotNull [] longs) {
        writeVarInt(longs.length);
        for(long l : longs)
            writeLong(l);
        return this;
    }

    @Override
    public float readFloat() {
        return buf.readFloat();
    }

    @Override
    public @NotNull FriendlyByteBuf writeFloat(float value) {
        buf.writeFloat(value);
        return this;
    }

    @Override
    public double readDouble() {
        return buf.readDouble();
    }

    @Override
    public @NotNull FriendlyByteBuf writeDouble(double value) {
        buf.writeDouble(value);
        return this;
    }

    @Override
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

    @Override
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

    @Override
    public int @NotNull [] readVarIntArray() {
        int length = readVarInt();
        int[] ints = new int[length];
        for(int i = 0; i < length; i++)
            ints[i] = readVarInt();
        return ints;
    }

    @Override
    public @NotNull FriendlyByteBuf writeVarIntArray(int @NotNull [] ints) {
        writeVarInt(ints.length);
        for(int i : ints)
            writeVarInt(i);
        return this;
    }

    @Override
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

    @Override
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

    @Override
    public @NotNull String readString(@NotNull Charset charset) {
        final int length = readVarInt();
        if (length < 0) throw new IllegalStateException();
        final byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++)
            bytes[i] = buf.readByte();
        return new String(bytes, charset);
    }

    @Override
    public @NotNull FriendlyByteBuf writeString(@NotNull String value, @NotNull Charset charset) {
        final byte[] bytes = value.getBytes(charset);
        writeVarInt(bytes.length);
        buf.writeBytes(bytes);
        return this;
    }

    @Override
    public @NotNull List<String> readStringList(@NotNull Charset charset) {
        final List<String> strings = new ArrayList<>();
        int length = readVarInt();
        for (int i = 0; i < length; i++)
            strings.add(readString(charset));
        return strings;
    }

    @Override
    public @NotNull FriendlyByteBuf writeStringList(@NotNull List<String> strings, @NotNull Charset charset) {
        writeVarInt(strings.size());
        for(String string : strings)
            writeString(string, charset);
        return this;
    }

    public @NotNull UUID readUUID() {
        return new UUID(readLong(), readLong());
    }

    @Override
    public @NotNull FriendlyByteBuf writeUUID(@NotNull UUID uuid) {
        writeLong(uuid.getMostSignificantBits());
        writeLong(uuid.getLeastSignificantBits());
        return this;
    }

    @Override
    public @NotNull BlockPosition readBlockPos() {
        final long packedPos = readLong();
        return new BlockPosition(
                (int) (packedPos >> 38),
                (int) ((packedPos << 52) >> 52),
                (int) ((packedPos << 26) >> 38));
    }

    @Override
    public @NotNull FriendlyByteBuf writeBlockPos(@NotNull BlockPosition position) {
        writeLong((((long) position.getX() & BlockPosition.PACKED_X_MASK) << 38) |
                (((long) position.getY() & BlockPosition.PACKED_Y_MASK)) |
                (((long) position.getZ() & BlockPosition.PACKED_Z_MASK) << 12));
        return this;
    }

    @Override
    public @NotNull NBT readNBT() {
        final byte[] bytes = buf.array();
        final ByteArrayInputStream buffer = new ByteArrayInputStream(bytes, buf.readerIndex(), bytes.length);
        final NBTReader reader = new NBTReader(buffer, CompressedProcesser.NONE);
        final NBT tag;
        try {
            tag = reader.read();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        buf.readerIndex(bytes.length - buffer.available());
        return tag;
    }

    @Override
    public @NotNull FriendlyByteBuf writeNBT(@NotNull String name, @NotNull NBT tag) {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final NBTWriter writer = new NBTWriter(buffer, CompressedProcesser.NONE);
        try {
            writer.writeNamed(name, tag);
        } catch (IOException ignored) { }
        buf.writeBytes(buffer.toByteArray());
        return this;
    }

    @Override
    public @NotNull Component readComponent() {
        return GsonComponentSerializer.gson().deserialize(readString(StandardCharsets.UTF_8));
    }

    @Override
    public @NotNull FriendlyByteBuf writeComponent(@NotNull Component component) {
        writeString(GsonComponentSerializer.gson().serialize(component), StandardCharsets.UTF_8);
        return this;
    }

    @Override
    public @NotNull NamespacedKey readNamespacedKey() {
        final @Subst("machine:server") String namespaceKey = readString(StandardCharsets.UTF_8);
        return NamespacedKey.parse(namespaceKey);
    }

    @Override
    public @NotNull FriendlyByteBuf writeNamespacedKey(@NotNull NamespacedKey namespacedKey) {
        writeString(namespacedKey.toString(), StandardCharsets.UTF_8);
        return this;
    }

    @Override
    public @NotNull Instant readInstant() {
        return Instant.ofEpochMilli(readLong());
    }

    @Override
    public @NotNull FriendlyByteBuf writeInstant(@NotNull Instant instant) {
        buf.writeLong(instant.toEpochMilli());
        return this;
    }

    @Override
    public @NotNull Item readSlot() {
        if(!readBoolean())
            return new ItemStack(Material.AIR);
        final Material material = ItemStack.getMaterial(readVarInt());
        ItemStack itemStack = new ItemStack(material != null ? material : Material.AIR, readByte());
        itemStack.setNbtCompound((NBTCompound) readNBT());
        return itemStack;
    }

    @Override
    public @NotNull FriendlyByteBuf writeSlot(@NotNull Item itemStack) {
        if(itemStack.getMaterial() == Material.AIR) {
            writeBoolean(false);
            return this;
        }
        writeBoolean(true);
        writeVarInt(itemStack.getMaterial().getId());
        writeByte(itemStack.getAmount());
        if(itemStack.getNbtCompound().getSize() != 0)
            writeNBT("", itemStack.getNbtCompound());
        else
            writeBoolean(false);
        return this;
    }

    @Override
    public @NotNull PublicKeyDataImpl readPublicKey() {
        final Instant instant = Instant.ofEpochMilli(readLong());
        return new PublicKeyDataImpl(Crypt.pubicKeyFrom(readByteArray()), readByteArray(), instant);
    }

    @Override
    public @NotNull FriendlyByteBuf writePublicKey(@NotNull PublicKeyData publicKeyData) {
        writeLong(publicKeyData.timestamp().toEpochMilli())
                .writeByteArray(publicKeyData.publicKey().getEncoded())
                .writeByteArray(publicKeyData.signature());
        return this;
    }

    @Override
    public @Nullable PlayerTexturesImpl readTextures() {
        if (readVarInt() == 0)
            return null;
        readString(StandardCharsets.UTF_8);
        final String value = readString(StandardCharsets.UTF_8);
        final String signature = readBoolean() ? readString(StandardCharsets.UTF_8) : null;
        try {
            return PlayerTexturesImpl.buildSkin(value, signature);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
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

    @Override
    public @NotNull MessageSignatureImpl readSignature() {
        final Instant timestamp = readInstant();
        final long salt = readLong();
        final byte[] signature = readByteArray();
        return new MessageSignatureImpl(timestamp, salt, signature);
    }

    @Override
    public @NotNull FriendlyByteBuf writeSignature(@NotNull MessageSignature messageSignature) {
        writeInstant(messageSignature.timestamp());
        writeLong(messageSignature.salt());
        writeByteArray(messageSignature.signature());
        return this;
    }

    @Override
    public float readAngle() {
        return readByte() * 360f / 256f;
    }

    @Override
    public @NotNull FriendlyByteBuf writeAngle(float angle) {
        writeByte((byte) (angle * 256f / 360f));
        return this;
    }

    @Override
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

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public @NotNull FriendlyByteBuf clone() {
        return new FriendlyByteBuf(bytes());
    }

}
