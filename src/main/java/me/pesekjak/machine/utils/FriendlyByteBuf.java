package me.pesekjak.machine.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.pesekjak.machine.world.BlockPosition;
import org.jglrxavpok.hephaistos.nbt.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Special ByteBuffer for implementing Minecraft Protocol.
 */
@SuppressWarnings("UnusedReturnValue")
@NoArgsConstructor
@AllArgsConstructor
public class FriendlyByteBuf {

    @SuppressWarnings("FieldMayBeFinal")
    private ByteBuf buf = Unpooled.buffer(0);

    private static final int SEGMENT_BITS = 0x7F;
    private static final int CONTINUE_BIT = 0x80;

    public FriendlyByteBuf(byte[] bytes) {
        buf.writeBytes(bytes);
    }

    /**
     * Returns content of the full buffer, doesn't move with
     * reader index and starts from byte 0.
     * @return all bytes of the buffer
     */
    public byte[] bytes() {
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
    public byte[] finish() {
        int length = buf.writerIndex();
        int reader = buf.readerIndex();
        byte[] bytes = new byte[length - reader];
        for(int i = 0; i < length - reader; i++)
            bytes[i] = readByte();
        return bytes;
    }

    public DataOutputStream stream() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(buffer);
        stream.write(bytes());
        return stream;
    }

    public DataOutputStream writeToStream(DataOutputStream stream) throws IOException {
        stream.write(bytes());
        return stream;
    }

    public boolean readBoolean() {
        return buf.readBoolean();
    }

    public FriendlyByteBuf writeBoolean(boolean value) {
        buf.writeBoolean(value);
        return this;
    }

    public byte readByte() {
        return buf.readByte();
    }

    public FriendlyByteBuf writeByte(byte value) {
        buf.writeByte(value);
        return this;
    }

    public FriendlyByteBuf writeBytes(byte... bytes) {
        buf.writeBytes(bytes);
        return this;
    }

    public short readShort() {
        return buf.readShort();
    }

    public FriendlyByteBuf writeShort(short value) {
        buf.writeShort(value);
        return this;
    }

    public int readInt() {
        return buf.readInt();
    }

    public FriendlyByteBuf writeInt(int value) {
        buf.writeInt(value);
        return this;
    }

    public long readLong() {
        return buf.readLong();
    }

    public FriendlyByteBuf writeLong(long value) {
        buf.writeLong(value);
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

    public FriendlyByteBuf writeVarInt(int value) {
        while (true) {
            if ((value & ~SEGMENT_BITS) == 0) {
                writeByte((byte) value);
                return this;
            }
            writeByte((byte) ((value & SEGMENT_BITS) | CONTINUE_BIT));
            value >>>= 7;
        }
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

    public FriendlyByteBuf writeVarLong(long value) {
        while (true) {
            if ((value & ~((long) SEGMENT_BITS)) == 0) {
                writeByte((byte) value);
                return this;
            }
            writeByte((byte) ((value & SEGMENT_BITS) | CONTINUE_BIT));
            value >>>= 7;
        }
    }

    public String readString(Charset charset) {
        int length = readVarInt();
        if (length == -1) return null;
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++)
            bytes[i] = buf.readByte();
        return new String(bytes, charset);
    }

    public FriendlyByteBuf writeString(String value, Charset charset) {
        byte[] bytes = value.getBytes(charset);
        writeVarInt(bytes.length);
        buf.writeBytes(bytes);
        return this;
    }

    public List<String> readStringList(Charset charset) {
        final List<String> strings = new ArrayList<>();
        int length = readVarInt();
        for (int i = 0; i < length; i++)
            strings.add(readString(charset));
        return strings;
    }

    public FriendlyByteBuf writeStringList(List<String> strings, Charset charset) {
        writeVarInt(strings.size());
        for(String string : strings)
            writeString(string, charset);
        return this;
    }

    public UUID readUUID() {
        return new UUID(readLong(), readLong());
    }

    public FriendlyByteBuf writeUUID(UUID uuid) {
        writeLong(uuid.getMostSignificantBits());
        writeLong(uuid.getLeastSignificantBits());
        return this;
    }

    public BlockPosition readBlockPos() {
        return BlockPosition.of(readLong());
    }

    public FriendlyByteBuf writeBlockPos(BlockPosition position) {
        writeLong(position.asLong());
        return this;
    }

    public FriendlyByteBuf writeNBT(String name, NBT tag) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        NBTWriter writer = new NBTWriter(buffer, CompressedProcesser.NONE);
        try {
            writer.writeNamed(name, tag);
        } catch (IOException ignored) { }
        buf.writeBytes(buffer.toByteArray());
        return this;
    }


    public NBT readNBT() {
        byte[] bytes = buf.array();
        ByteArrayInputStream buffer = new ByteArrayInputStream(bytes, buf.readerIndex(), bytes.length);
        NBTReader reader = new NBTReader(buffer, CompressedProcesser.NONE);
        NBT tag = null;
        try {
            tag = reader.read();
        } catch (Exception ignored) { }
        buf.readerIndex(bytes.length - buffer.available());
        return tag;
    }

}