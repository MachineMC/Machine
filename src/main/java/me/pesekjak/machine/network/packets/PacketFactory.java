package me.pesekjak.machine.network.packets;

import me.pesekjak.machine.utils.ClassUtils;
import me.pesekjak.machine.utils.FriendlyByteBuf;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles the creation of Packet instances.
 */
@SuppressWarnings("unchecked")
public class PacketFactory {

    final static Map<Class<? extends PacketIn>, PacketCreator<? extends PacketIn>> IN_CREATORS = new HashMap<>();
    final static Map<Class<? extends PacketOut>, PacketCreator<? extends PacketOut>> OUT_CREATORS = new HashMap<>();

    final static Map<Integer, Class<? extends PacketIn>> IN_MAPPING = new HashMap<>();
    final static Map<Class<? extends PacketOut>, Integer> OUT_MAPPING = new HashMap<>();

    static {
        try {
            ClassUtils.loadClasses(PacketFactory.class.getPackageName());
        } catch (IOException ignored) { }
    }

    /**
     * Creates new instance of a packet of provided class using the {@link FriendlyByteBuf}.
     * @param packetClass class reference of the packet
     * @param buf buffer containing the packet data
     * @return instance of the packet
     */
    public static Packet produce(final Class<? extends Packet> packetClass, FriendlyByteBuf buf) {
        if(PacketIn.class.isAssignableFrom(packetClass))
            return produceIn((Class<? extends PacketIn>) packetClass, buf);
        else if(PacketOut.class.isAssignableFrom(packetClass))
            return produceOut((Class<? extends PacketOut>) packetClass, buf);
        else
            return null;
    }

    /**
     * Creates new instance of a PacketIn of provided class using the {@link FriendlyByteBuf}.
     * @param packetClass class reference of the packet
     * @param buf buffer containing the packet data
     * @return instance of the packet
     */
    public static PacketIn produceIn(final Class<? extends PacketIn> packetClass, final FriendlyByteBuf buf) {
        PacketCreator<? extends PacketIn> creator = IN_CREATORS.get(packetClass);
        if(creator == null) return null;
        return creator.create(buf);
    }

    /**
     * Creates new instance of a PacketOut of provided class using the {@link FriendlyByteBuf}.
     * @param packetClass class reference of the packet
     * @param buf buffer containing the packet data
     * @return instance of the packet
     */
    public static PacketOut produceOut(final Class<? extends PacketOut> packetClass, FriendlyByteBuf buf) {
        PacketCreator<? extends PacketOut> creator = OUT_CREATORS.get(packetClass);
        if(creator == null) return null;
        return creator.create(buf);
    }

    /**
     * Returns class of the PacketIn from mapped packet ID.
     * @param ID ID of the packet
     * @return class of the packet
     */
    public static Class<? extends PacketIn> getPacketInById(int ID) {
        return IN_MAPPING.get(ID);
    }

    /**
     * Returns class of the PacketIn using on Mojang's mapping
     * @param ID ID of the packet
     * @param state state of the packet
     * @return class of the packet
     */
    public static Class<? extends PacketIn> getPacketByRawId(int ID, Packet.PacketState state) {
        return IN_MAPPING.get(ID | state.getMask());
    }

    /**
     * Returns ID of the PacketOut from its class reference.
     * @param packetClass class of the packet
     * @return ID of the packet
     */
    public static int getIdByPacketOut(Class<? extends PacketOut> packetClass) {
        return OUT_MAPPING.get(packetClass);
    }

    /**
     * Returns Mojang mapped ID of the PacketOut from its class reference.
     * @param packetClass class of the packet
     * @param state state of the packet
     * @return ID of the packet
     */
    public static int getRawIdByPacketOut(Class<? extends PacketOut> packetClass, Packet.PacketState state) {
        return OUT_MAPPING.get(packetClass) & ~state.getMask();
    }

    /**
     * Returns state of Packet from its class reference
     * @param packetClass class of the packet
     * @return state of the packet
     */
    public static Packet.PacketState getStateFromPacket(Class<? extends Packet> packetClass) {
        return Packet.PacketState.fromMask(OUT_MAPPING.get(packetClass) & (0b111 << Packet.PacketState.OFFSET));
    }

}
