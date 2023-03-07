package org.machinemc.server.network.packets;

/**
 * Packet sent from server to client.
 */
public abstract class PacketOut extends PacketImpl {

    /**
     * Creates mapping and creator for the packet. Each PacketOut has to call this in static block.
     * @param packetClass class reference of the packet
     * @param id mapped id by Mojang
     * @param creator PacketCreator
     */
    protected static void register(Class<? extends PacketOut> packetClass, int id, PacketState state, PacketCreator<? extends PacketOut> creator) {
        id = id | state.getMask();
        PacketFactory.OUT_MAPPING.put(packetClass, id);
        PacketFactory.CREATORS.put(packetClass, creator);
    }

    public abstract PacketOut clone();

}
