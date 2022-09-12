package me.pesekjak.machine.network;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketIn;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.network.packets.out.PacketLoginSetCompression;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.utils.Pair;
import me.pesekjak.machine.utils.ZLib;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Handles the stream of the packets of ClientConnection.
 */
@RequiredArgsConstructor
public class Channel implements AutoCloseable {

    private final List<Pair<NamespacedKey, PacketHandler>> handlers = new CopyOnWriteArrayList<>();

    @Getter
    private final ClientConnection connection;
    protected final DataInputStream input;
    protected final DataOutputStream output;

    @Getter
    private boolean open = true;

    @Getter @Setter(AccessLevel.PROTECTED)
    private boolean compressed;
    @Getter @Setter(AccessLevel.PROTECTED)
    private int threshold;

    /**
     * Adds new handler before the all existing ones.
     * @param key namespaced key of the handler
     * @param handler new handler
     */
    public void addHandlerBefore(NamespacedKey key, PacketHandler handler) {
        assert handler != null;
        handlers.add(0, new Pair<>(key, handler));
    }

    /**
     * Adds new handler after the all existing ones.
     * @param key namespaced key of the handler
     * @param handler new handler
     */
    public void addHandlerAfter(NamespacedKey key, PacketHandler handler) {
        assert handler != null;
        handlers.add(new Pair<>(key, handler));
    }

    /**
     * Removes the handler from the channel
     * @param key key of the handler to remove
     */
    public void removeHandler(NamespacedKey key) {
        handlers.removeIf(pair -> pair.getFirst().equals(key));
    }

    /**
     * Enables the packet compression for this channel.
     * @param threshold maximum packet size without compression
     * @return true if client accepted the compression
     */
    public boolean setCompression(int threshold) throws IOException {
        if(threshold <= 0) threshold = -1;
        boolean success = writePacket(
                new PacketLoginSetCompression(new FriendlyByteBuf()
                        .writeVarInt(threshold)));
        if(success) {
            compressed = true;
            this.threshold = threshold;
        }
        return success;
    }

    /**
     * Disables the packet compression for this channel.
     * @return true if client accepted cancellation of the compression
     */
    public boolean disableCompression() throws IOException {
        return setCompression(-1);
    }

    /**
     * Reads the packet, handles, and returns.
     * @return handled packet
     */
    @Nullable
    protected PacketIn readPacket(ClientConnection.ClientState state) throws IOException {
        if(!open) return null;
        FriendlyByteBuf buf = new FriendlyByteBuf();
        int length = readVarInt(input);
        if(compressed) {
            FriendlyByteBuf compressed = new FriendlyByteBuf();
            compressed.writeBytes(input.readNBytes(length));
            if(compressed.readVarInt() == 0) { // Was too small to be compressed
                byte[] uncompressedData = compressed.finish();
                buf.writeVarInt(uncompressedData.length);
                buf.writeBytes(uncompressedData);
            } else { // Actually compressed
                buf.writeVarInt(compressed.readVarInt());
                byte[] decompressedData = ZLib.decompress(compressed.finish());
                buf.writeBytes(decompressedData);
            }
        } else {
            buf.writeVarInt(length);
            buf.writeBytes(input.readNBytes(length));
        }
        PacketReader read = new PacketReader(buf, state.in);
        for(Pair<NamespacedKey, PacketHandler> pair : handlers)
            read = pair.getSecond().read(this, read);
        return read.getPacket();
    }

    /**
     * Serializes the packet, handlers, and writes.
     * @param packet packet to write
     * @return true if packet wasn't cancelled
     */
    protected boolean writePacket(PacketOut packet) throws IOException {
        if(!open) return false;
        PacketWriter write = new PacketWriter(packet);
        for(Pair<NamespacedKey, PacketHandler> pair : handlers)
            write = pair.getSecond().write(this, write);
        if(write.getPacket() == null) return false;
        FriendlyByteBuf buf = new FriendlyByteBuf();
        if(compressed)
            buf.writeBytes(write.getPacket().rawCompressedSerialize(threshold));
        else
            buf.writeBytes(write.getPacket().rawSerialize());
        output.write(buf.bytes());
        return true;
    }

    @Override
    public void close() throws Exception {
        open = false;
        input.close();
        output.close();
    }

    private static int readVarInt(DataInputStream in) throws IOException {
        int numRead = 0;
        int result = 0;
        byte read;
        do {
            read = in.readByte();
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 5) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 0b10000000) != 0);
        return result;
    }

}
