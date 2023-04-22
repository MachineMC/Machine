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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.auth.Crypt;
import org.machinemc.api.auth.MessageSignature;
import org.machinemc.api.auth.PublicKeyData;
import org.machinemc.api.entities.player.PlayerTextures;
import org.machinemc.api.inventory.Item;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.utils.Writable;
import org.machinemc.api.world.BlockPosition;
import org.machinemc.api.world.Material;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.serialization.ComponentSerializer;
import org.machinemc.scriptive.serialization.ComponentSerializerImpl;
import org.machinemc.server.auth.MessageSignatureImpl;
import org.machinemc.server.auth.PublicKeyDataImpl;
import org.machinemc.server.entities.player.PlayerTexturesImpl;
import org.machinemc.server.inventory.ItemStack;

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

    private final ByteBuf buf;

    public static final int SEGMENT_BITS = 0x7F;
    public static final int CONTINUE_BIT = 0x80;

    public FriendlyByteBuf() {
        this(new byte[0]);
    }

    public FriendlyByteBuf(final byte[] bytes) {
        buf = Unpooled.buffer(0);
        buf.writeBytes(bytes);
    }

    public FriendlyByteBuf(final ByteBuf byteBuf) {
        buf = byteBuf;
    }

    public FriendlyByteBuf(final DataInputStream dataInputStream) throws IOException {
        this(dataInputStream.readAllBytes());
    }

    @Override
    public byte[] bytes() {
        final int length = buf.writerIndex();
        final int reader = buf.readerIndex();
        buf.readerIndex(0);
        final byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++)
            bytes[i] = readByte();
        buf.readerIndex(reader);
        return bytes;
    }

    @Override
    public byte[] finish() {
        final int length = buf.writerIndex();
        final int reader = buf.readerIndex();
        final byte[] bytes = new byte[length - reader];
        for (int i = 0; i < length - reader; i++)
            bytes[i] = readByte();
        return bytes;
    }

    @Override
    public DataOutputStream stream() throws IOException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final DataOutputStream stream = new DataOutputStream(buffer);
        stream.write(bytes());
        return stream;
    }

    @Override
    public DataOutputStream writeToStream(final DataOutputStream stream) throws IOException {
        stream.write(bytes());
        return stream;
    }

    @Override
    public FriendlyByteBuf write(final Writable writable) {
        writable.write(this);
        return this;
    }

    @Override
    public boolean readBoolean() {
        return buf.readBoolean();
    }

    @Override
    public FriendlyByteBuf writeBoolean(final boolean value) {
        buf.writeBoolean(value);
        return this;
    }

    @Override
    public byte readByte() {
        return buf.readByte();
    }

    @Override
    public FriendlyByteBuf writeByte(final byte value) {
        buf.writeByte(value);
        return this;
    }

    @Override
    public byte[] readBytes(final int length) {
        final byte[] result = new byte[length];
        for (int i = 0; i < length; i++)
            result[i] = readByte();
        return result;
    }

    @Override
    public FriendlyByteBuf writeBytes(final byte... bytes) {
        buf.writeBytes(bytes);
        return this;
    }

    @Override
    public byte[] readByteArray() {
        final int length = readVarInt();
        final byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++)
            bytes[i] = readByte();
        return bytes;
    }

    @Override
    public FriendlyByteBuf writeByteArray(final byte[] bytes) {
        writeVarInt(bytes.length);
        for (final byte b : bytes)
            writeByte(b);
        return this;
    }

    @Override
    public short readShort() {
        return buf.readShort();
    }

    @Override
    public FriendlyByteBuf writeShort(final short value) {
        buf.writeShort(value);
        return this;
    }

    @Override
    public int readInt() {
        return buf.readInt();
    }

    @Override
    public FriendlyByteBuf writeInt(final int value) {
        buf.writeInt(value);
        return this;
    }

    @Override
    public long readLong() {
        return buf.readLong();
    }

    @Override
    public FriendlyByteBuf writeLong(final long value) {
        buf.writeLong(value);
        return this;
    }

    @Override
    public long[] readLongArray() {
        final int length = readVarInt();
        final long[] longs = new long[length];
        for (int i = 0; i < length; i++)
            longs[i] = readLong();
        return longs;
    }

    @Override
    public FriendlyByteBuf writeLongArray(final long[] longs) {
        writeVarInt(longs.length);
        for (final long l : longs)
            writeLong(l);
        return this;
    }

    @Override
    public float readFloat() {
        return buf.readFloat();
    }

    @Override
    public FriendlyByteBuf writeFloat(final float value) {
        buf.writeFloat(value);
        return this;
    }

    @Override
    public double readDouble() {
        return buf.readDouble();
    }

    @Override
    public FriendlyByteBuf writeDouble(final double value) {
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
    public FriendlyByteBuf writeVarInt(final int value) {
        int i = value;
        while (true) {
            if ((i & ~SEGMENT_BITS) == 0) {
                writeByte((byte) i);
                return this;
            }
            writeByte((byte) ((i & SEGMENT_BITS) | CONTINUE_BIT));
            i >>>= 7;
        }
    }

    @Override
    public int[] readVarIntArray() {
        final int length = readVarInt();
        final int[] ints = new int[length];
        for (int i = 0; i < length; i++)
            ints[i] = readVarInt();
        return ints;
    }

    @Override
    public FriendlyByteBuf writeVarIntArray(final int[] ints) {
        writeVarInt(ints.length);
        for (final int i : ints)
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
    public FriendlyByteBuf writeVarLong(final long value) {
        long i = value;
        while (true) {
            if ((i & ~((long) SEGMENT_BITS)) == 0) {
                writeByte((byte) i);
                return this;
            }
            writeByte((byte) ((i & SEGMENT_BITS) | CONTINUE_BIT));
            i >>>= 7;
        }
    }

    @Override
    public String readString(final Charset charset) {
        final int length = readVarInt();
        if (length < 0) throw new IllegalStateException();
        final byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++)
            bytes[i] = buf.readByte();
        return new String(bytes, charset);
    }

    @Override
    public FriendlyByteBuf writeString(final String value, final Charset charset) {
        final byte[] bytes = value.getBytes(charset);
        writeVarInt(bytes.length);
        buf.writeBytes(bytes);
        return this;
    }

    @Override
    public List<String> readStringList(final Charset charset) {
        final List<String> strings = new ArrayList<>();
        final int length = readVarInt();
        for (int i = 0; i < length; i++)
            strings.add(readString(charset));
        return strings;
    }

    @Override
    public FriendlyByteBuf writeStringList(final List<String> strings, final Charset charset) {
        writeVarInt(strings.size());
        for (final String string : strings)
            writeString(string, charset);
        return this;
    }

    @Override
    public UUID readUUID() {
        return new UUID(readLong(), readLong());
    }

    @Override
    public FriendlyByteBuf writeUUID(final UUID uuid) {
        writeLong(uuid.getMostSignificantBits());
        writeLong(uuid.getLeastSignificantBits());
        return this;
    }

    @Override
    public BlockPosition readBlockPos() {
        final long packedPos = readLong();
        return new BlockPosition(
                (int) (packedPos >> 38),
                (int) ((packedPos << 52) >> 52),
                (int) ((packedPos << 26) >> 38));
    }

    @Override
    public FriendlyByteBuf writeBlockPos(final BlockPosition position) {
        writeLong((((long) position.getX() & BlockPosition.PACKED_X_MASK) << 38)
                | (((long) position.getY() & BlockPosition.PACKED_Y_MASK))
                | (((long) position.getZ() & BlockPosition.PACKED_Z_MASK) << 12));
        return this;
    }

    @Override
    public NBTCompound readNBT() {
        final byte[] bytes = buf.array();
        final ByteArrayInputStream is = new ByteArrayInputStream(bytes, buf.readerIndex(), bytes.length);
        final NBTCompound compound;
        try {
            compound = new NBTCompound();
            compound.readAll(is);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        buf.readerIndex(bytes.length - is.available());
        try {
            is.close();
        } catch (IOException ignored) { }
        return compound;
    }

    @Override
    public FriendlyByteBuf writeNBT(final NBTCompound compound) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        compound.writeAll(os);
        buf.writeBytes(os.toByteArray());
        try {
            os.close();
        } catch (IOException ignored) { }
        return this;
    }

    @Override
    public Component readComponent() {
        return readComponent(new ComponentSerializerImpl());
    }

    @Override
    public Component readComponent(final ComponentSerializer serializer) {
        return serializer.deserializeJson(readString(StandardCharsets.UTF_8));
    }

    @Override
    public FriendlyByteBuf writeComponent(final Component component) {
        writeString(component.toJson(), StandardCharsets.UTF_8);
        return this;
    }

    @Override
    public NamespacedKey readNamespacedKey() {
        return NamespacedKey.parse(readString(StandardCharsets.UTF_8));
    }

    @Override
    public FriendlyByteBuf writeNamespacedKey(final NamespacedKey namespacedKey) {
        writeString(namespacedKey.toString(), StandardCharsets.UTF_8);
        return this;
    }

    @Override
    public Instant readInstant() {
        return Instant.ofEpochMilli(readLong());
    }

    @Override
    public FriendlyByteBuf writeInstant(final Instant instant) {
        buf.writeLong(instant.toEpochMilli());
        return this;
    }

    @Override
    public Item readSlot() {
        if (!readBoolean())
            return new ItemStack(Material.AIR);
        final Material material = ItemStack.getMaterial(readVarInt());
        final ItemStack itemStack = new ItemStack(material != null ? material : Material.AIR, readByte());
        final NBTCompound compound = readNBT();
        itemStack.setNbtCompound(compound);
        return itemStack;
    }

    @Override
    public FriendlyByteBuf writeSlot(final Item itemStack) {
        if (itemStack.getMaterial() == Material.AIR) {
            writeBoolean(false);
            return this;
        }
        writeBoolean(true);
        writeVarInt(itemStack.getMaterial().getId());
        writeByte(itemStack.getAmount());
        if (itemStack.getNbtCompound().size() != 0)
            writeNBT(itemStack.getNbtCompound());
        else
            writeBoolean(false);
        return this;
    }

    @Override
    public PublicKeyData readPublicKey() {
        final Instant instant = Instant.ofEpochMilli(readLong());
        return new PublicKeyDataImpl(Crypt.pubicKeyFrom(readByteArray()), readByteArray(), instant);
    }

    @Override
    public FriendlyByteBuf writePublicKey(final PublicKeyData publicKeyData) {
        writeLong(publicKeyData.timestamp().toEpochMilli())
                .writeByteArray(publicKeyData.publicKey().getEncoded())
                .writeByteArray(publicKeyData.signature());
        return this;
    }

    @Override
    public @Nullable PlayerTextures readTextures() {
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
    public FriendlyByteBuf writeTextures(final @Nullable PlayerTextures playerSkin) {
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
    public MessageSignature readSignature() {
        final Instant timestamp = readInstant();
        final long salt = readLong();
        final byte[] signature = readByteArray();
        return new MessageSignatureImpl(timestamp, salt, signature);
    }

    @Override
    public FriendlyByteBuf writeSignature(final MessageSignature messageSignature) {
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
    public FriendlyByteBuf writeAngle(final float angle) {
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
    public ServerBuffer setReaderIndex(final int index) {
        buf.readerIndex(index);
        return this;
    }

    @Override
    public int writerIndex() {
        return buf.writerIndex();
    }

    @Override
    public ServerBuffer setWriterIndex(final int index) {
        buf.writerIndex(index);
        return this;
    }

    @Override
    public void release() {
        buf.release();
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public FriendlyByteBuf clone() {
        return new FriendlyByteBuf(bytes());
    }

}
