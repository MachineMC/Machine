package org.machinemc.server.network;

import lombok.*;
import org.machinemc.api.auth.Crypt;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.server.network.packets.out.login.PacketLoginOutSetCompression;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.server.utils.Pair;
import org.machinemc.server.utils.ZLib;
import org.jetbrains.annotations.Nullable;

import javax.crypto.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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

    @Getter
    private @Nullable SecretKey secretKey;
    private EncryptionContext encryptionContext;

    private FriendlyByteBuf preReadBytes = new FriendlyByteBuf();

    /**
     * Adds new handler before the all existing ones.
     * @param key namespaced key of the handler
     * @param handler new handler
     */
    public void addHandlerBefore(NamespacedKey key, PacketHandler handler) {
        handlers.add(0, new Pair<>(key, handler));
    }

    /**
     * Adds new handler after the all existing ones.
     * @param key namespaced key of the handler
     * @param handler new handler
     */
    public void addHandlerAfter(NamespacedKey key, PacketHandler handler) {
        handlers.add(new Pair<>(key, handler));
    }

    /**
     * Removes the handler from the channel
     * @param key key of the handler to remove
     */
    public void removeHandler(NamespacedKey key) {
        handlers.removeIf(pair -> pair.first().equals(key));
    }

    /**
     * Enables the packet compression for this channel.
     * @param threshold maximum packet size without compression
     * @return true if client accepted the compression
     */
    @Synchronized
    public boolean setCompression(int threshold) throws IOException {
        if(threshold <= 0) threshold = -1;
        boolean success = writePacket(new PacketLoginOutSetCompression(threshold));
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
    @Synchronized
    public boolean disableCompression() throws IOException {
        return setCompression(-1);
    }

    /**
     * Reads not yet read packets sent by client, handles, and returns.
     * @return handled packets
     */
    @Synchronized
    protected Packet[] readPackets() throws IOException {
        if(!open) return new Packet[0];
        if(input.available() == 0) {
            /*
            Channel tries to read bytes before they're available,
            if read byte is -1, client disconnected, otherwise
            the bytes need to be stored for the next packet.
             */
            int preReadByte;
            try {
                preReadByte = connection.getClientSocket().getInputStream().read();
            } catch (Exception exception) { return new Packet[0]; }
            if(preReadByte == -1) {
                connection.disconnect();
                return new Packet[0];
            }
            preReadBytes.writeByte((byte) preReadByte);
            return new Packet[0];
        }
        FriendlyByteBuf input = new FriendlyByteBuf();
        // Writes pre-read bytes to the buffer and clears
        input.writeBytes(preReadBytes.finish());
        preReadBytes = new FriendlyByteBuf();
        // Writes available bytes to the buffer
        input.writeBytes(this.input.readNBytes(this.input.available()));
        // decryption
        if(secretKey != null && encryptionContext != null)
            input = new FriendlyByteBuf(encryptionContext.decrypt.update(input.bytes()));
        List<Packet> packets = new ArrayList<>();
        do {
            int length = input.readVarInt();
            FriendlyByteBuf buf = new FriendlyByteBuf();
            if(compressed) {
                FriendlyByteBuf compressed = new FriendlyByteBuf();
                compressed.writeBytes(input.readBytes(length));
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
                buf.writeBytes(input.readBytes(length));
            }
            final Packet.PacketState packetState = getConnection().getClientState().getIn();
            if(packetState == null) return new Packet[0];
            PacketReader read = new PacketReader(buf, packetState);
            if(read.getPacket() == null) continue;
            for(Pair<NamespacedKey, PacketHandler> pair : handlers)
                read = pair.second().read(this, read);
            if(read.getPacket() != null) {
                for(Pair<NamespacedKey, PacketHandler> pair : handlers)
                    pair.second().afterRead(this, read.getPacket().clone());
                packets.add(read.getPacket());
            }
        } while(input.readableBytes() != 0);
        return packets.toArray(new Packet[0]);
    }

    /**
     * Serializes the packet, handlers, and writes.
     * @param packet packet to write
     * @return true if packet wasn't cancelled
     */
    @Synchronized
    protected boolean writePacket(Packet packet) throws IOException {
        if(!open) return false;
        if(!Packet.PacketState.out().contains(packet.getPacketState()))
            throw new UnsupportedOperationException();
        PacketWriter write = new PacketWriter(packet);
        for(Pair<NamespacedKey, PacketHandler> pair : handlers)
            write = pair.second().write(this, write);
        if(write.getPacket() == null) return false;
        FriendlyByteBuf buf = new FriendlyByteBuf();
        // Compression
        if(compressed)
            buf.writeBytes(write.getPacket().rawCompressedSerialize(threshold));
        else
            buf.writeBytes(write.getPacket().rawSerialize());
        // Encryption
        if(connection.isDisconnected())
            return false;
        if(secretKey != null && encryptionContext != null)
            output.write(encryptionContext.encrypt.update(buf.bytes()));
        else
            output.write(buf.bytes());
        for(Pair<NamespacedKey, PacketHandler> pair : handlers)
            pair.second().afterWrite(this, write.getPacket().clone());
        return true;
    }

    /**
     * Closes the channel and disconnects the client.
     */
    @Override
    public void close() throws Exception {
        open = false;
        input.close();
        output.close();
    }

    /**
     * Sets the secret key used for encryption.
     * @param key new secret key
     */
    protected void setSecretKey(SecretKey key) {
        if(secretKey != null)
            throw new IllegalStateException("Encryption for the Channel is already enabled");
        secretKey = key;
        encryptionContext = new EncryptionContext(
                Crypt.getCipher(Cipher.ENCRYPT_MODE, secretKey),
                Crypt.getCipher(Cipher.DECRYPT_MODE, secretKey)
        );
    }

    /**
     * Encryption context containing ciphers to encrypt and decrypt
     * the packets.
     */
    record EncryptionContext(Cipher encrypt, Cipher decrypt) {
    }

}
