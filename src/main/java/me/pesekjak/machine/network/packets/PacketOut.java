package me.pesekjak.machine.network.packets;

public abstract class PacketOut extends Packet {

    /**
     * Creates mapping and creator for the packet. Each PacketOut has to call this in static block.
     * @param packetClass Class reference of the packet
     * @param id mapped ID by Mojang
     * @param creator PacketCreator
     */
    protected static void register(Class<? extends PacketOut> packetClass, int id, PacketState state, PacketCreator<? extends PacketOut> creator) {
        id = id | state.getMask();
        PacketFactory.OUT_MAPPING.put(packetClass, id);
        PacketFactory.OUT_CREATORS.put(packetClass, creator);
    }

    public abstract PacketOut clone();

}
