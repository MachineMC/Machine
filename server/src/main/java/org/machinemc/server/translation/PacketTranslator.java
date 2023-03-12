package org.machinemc.server.translation;

import org.machinemc.server.network.ClientConnection;
import org.machinemc.api.network.packets.Packet;

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
    public abstract boolean translate(ClientConnection connection, T packet);

    /**
     * Called after the packet is sent or received from the client.
     * @param connection ClientConnection the packet has been sent or received from
     * @param packet Packet that has been sent or received
     */
    public abstract void translateAfter(ClientConnection connection, T packet);

    /**
     * @return Class of the packet the translator should listen to.
     */
    public abstract Class<T> packetClass();

    /**
     * Called before the packet is sent or received from the client.
     * @param connection ClientConnection the packet is sent or received from
     * @param packet Packet that is sent or received
     * @return false if packet should be cancelled
     */
    @SuppressWarnings("unchecked")
    boolean rawTranslate(ClientConnection connection, Packet packet) {
        return translate(connection, (T) packet);
    }

    /**
     * Called after the packet is sent or received from the client.
     * @param connection ClientConnection the packet has been sent or received from
     * @param packet Packet that has been sent or received
     */
    @SuppressWarnings("unchecked")
    void rawTranslateAfter(ClientConnection connection, Packet packet) {
        translateAfter(connection, (T) packet);
    }

}
