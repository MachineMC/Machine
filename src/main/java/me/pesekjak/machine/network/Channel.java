package me.pesekjak.machine.network;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.network.packets.PacketIn;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.utils.Pair;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Handles the stream of the packets.
 */
@RequiredArgsConstructor
public class Channel implements AutoCloseable {

    private final List<Pair<NamespacedKey, PacketHandler>> handlers = new CopyOnWriteArrayList<>();

    protected final DataInputStream input;
    protected final DataOutputStream output;

    @Getter
    private boolean open = true;

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
     * Reads the packet, handles, and returns.
     * @return handled packet
     */
    @Nullable
    protected PacketIn readPacket(ClientConnection.ClientState state) throws IOException {
        if(!open) return null;
        FriendlyByteBuf buf = new FriendlyByteBuf();
        int length = readVarInt(input);
        buf.writeVarInt(length);
        buf.writeBytes(input.readNBytes(length));
        PacketReader read = new PacketReader(buf, state.in);
        for(Pair<NamespacedKey, PacketHandler> pair : handlers)
            read = pair.getSecond().read(read);
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
            write = pair.getSecond().write(write);
        if(write.getPacket() == null) return false;
        output.write(write.getPacket().rawSerialize());
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
