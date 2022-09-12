package me.pesekjak.machine.events.translations;

import me.pesekjak.machine.network.ClientConnection;
import me.pesekjak.machine.network.packets.Packet;

/**
 * Translates packet into server actions and events.
 * @param <T>
 */
public abstract class PacketTranslator<T extends Packet> {

    public abstract void translate(ClientConnection connection, T packet);

    public abstract Class<T> packetClass();

    @SuppressWarnings("unchecked")
    void rawTranslate(ClientConnection connection, Packet packet) {
        translate(connection, (T) packet);
    }

}
