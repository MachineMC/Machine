package org.machinemc.server.network.packets;

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
    protected static void register(Class<? extends PacketIn> packetClass, int id, PacketState state, PacketCreator<? extends PacketIn> creator) {
        id = id | state.getMask();
        PacketFactory.IN_MAPPING.put(id, packetClass);
        PacketFactory.CREATORS.put(packetClass, creator);
    }

    public abstract PacketIn clone();

}
