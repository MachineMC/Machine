package org.machinemc.server.network.packets;

/**
 * Packet sent from server to client.
 */
public abstract class PacketOut extends PacketImpl {

    /**
     * Creates mapping and creator for the packet. Each PacketOut has to call this in static block.
     * @param packetClass class reference of the packet
     * @param id mapped id by Mojang
     * @param state state of the packet
     * @param creator PacketCreator
     */
    protected static void register(final Class<? extends PacketOut> packetClass,
                                   final int id,
                                   final PacketState state,
                                   final PacketCreator<? extends PacketOut> creator) {
        if (!PacketState.out().contains(state)) throw new IllegalStateException();
        int fullId = id | state.getMask();
        PacketFactory.OUT_MAPPING.put(packetClass, fullId);
        PacketFactory.CREATORS.put(packetClass, creator);
    }

    /**
     * @return clone of the packet
     */
    public abstract PacketOut clone();

}
