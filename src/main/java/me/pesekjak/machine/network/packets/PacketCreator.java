package me.pesekjak.machine.network.packets;

import me.pesekjak.machine.utils.FriendlyByteBuf;

/**
 * Creates packet instance from given data.
 * @param <T> packet
 */
@FunctionalInterface
public interface PacketCreator<T extends PacketImpl> {

    /**
     * Creates new packet instance from given {@link FriendlyByteBuf},
     * the buffer can't contain the packet size and ID, but only data
     * itself.
     * @param buf buffer with packet data
     * @return new packet instance
     */
    T create(FriendlyByteBuf buf);

}
