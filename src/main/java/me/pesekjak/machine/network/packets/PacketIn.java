package me.pesekjak.machine.network.packets;

import org.jetbrains.annotations.NotNull;

/**
 * Packet sent from client to server.
 */
public abstract class PacketIn extends PacketImpl {

    /**
     * Creates mapping and creator for the packet. Each PacketIn has to call this in static block.
     * @param packetClass class reference of the packet
     * @param id mapped id by Mojang
     * @param creator PacketCreator
     */
    protected static void register(@NotNull Class<? extends PacketIn> packetClass, int id, @NotNull PacketState state, @NotNull PacketCreator<? extends PacketIn> creator) {
        id = id | state.getMask();
        PacketFactory.IN_MAPPING.put(id, packetClass);
        PacketFactory.CREATORS.put(packetClass, creator);
    }

    public abstract @NotNull PacketIn clone();

}
