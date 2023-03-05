package org.machinemc.server.network.packets;

import org.machinemc.api.network.packets.Packet;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;

/**
 * Creates packet instance from given data.
 * @param <T> packet
 */
@FunctionalInterface
public interface PacketCreator<T extends Packet> {

    /**
     * Creates new packet instance from given {@link FriendlyByteBuf},
     * the buffer can't contain the packet size and ID, but only data
     * itself.
     * @param buf buffer with packet data
     * @return new packet instance
     */
    T create(ServerBuffer buf);

}
