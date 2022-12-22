package me.pesekjak.machine.translation;

import me.pesekjak.machine.network.ClientConnection;
import me.pesekjak.machine.network.packets.Packet;
import org.jetbrains.annotations.NotNull;

/**
 * Translates packet into server actions and events.
 * @param <T>
 */
public abstract class PacketTranslator<T extends Packet> {

    /**
     * Called before the packet is sent or received from the client.
     * @param connection ClientConnection the packet is sent or received from
     * @param packet Packet that is sent or received
     * @return false if packet should be cancelled
     */
    public abstract boolean translate(@NotNull ClientConnection connection, @NotNull T packet);

    /**
     * Called after the packet is sent or received from the client.
     * @param connection ClientConnection the packet has been sent or received from
     * @param packet Packet that has been sent or received
     */
    public abstract void translateAfter(@NotNull ClientConnection connection, @NotNull T packet);

    /**
     * @return Class of the packet the translator should listen to.
     */
    @NotNull
    public abstract Class<T> packetClass();

    /**
     * Called before the packet is sent or received from the client.
     * @param connection ClientConnection the packet is sent or received from
     * @param packet Packet that is sent or received
     * @return false if packet should be cancelled
     */
    @SuppressWarnings("unchecked")
    boolean rawTranslate(@NotNull ClientConnection connection, @NotNull Packet packet) {
        return translate(connection, (T) packet);
    }

    /**
     * Called after the packet is sent or received from the client.
     * @param connection ClientConnection the packet has been sent or received from
     * @param packet Packet that has been sent or received
     */
    @SuppressWarnings("unchecked")
    void rawTranslateAfter(@NotNull ClientConnection connection, @NotNull Packet packet) {
        translateAfter(connection, (T) packet);
    }

}
