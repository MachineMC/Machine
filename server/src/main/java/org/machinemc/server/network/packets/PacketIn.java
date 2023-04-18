package org.machinemc.server.network.packets;

/**
 * Packet sent from client to server.
 */
public abstract class PacketIn extends PacketImpl {

    /**
     * Creates mapping and creator for the packet. Each PacketIn has to call this in static block.
     * @param packetClass class reference of the packet
     * @param id mapped id by Mojang
     * @param state state of the packet
     * @param creator PacketCreator
     */
    protected static void register(final Class<? extends PacketIn> packetClass,
                                   final int id,
                                   final PacketState state,
                                   final PacketCreator<? extends PacketIn> creator) {
        if (!PacketState.in().contains(state)) throw new IllegalStateException();
        final int fullId = id | state.getMask();
        PacketFactory.IN_MAPPING.put(fullId, packetClass);
        PacketFactory.CREATORS.put(packetClass, creator);
    }

    /**
     * @return clone of the packet
     */
    public abstract PacketIn clone();

}
