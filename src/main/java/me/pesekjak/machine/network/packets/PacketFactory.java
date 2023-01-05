package me.pesekjak.machine.network.packets;

import lombok.experimental.UtilityClass;
import me.pesekjak.machine.utils.ClassUtils;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles the creation of Packet instances.
 */
@UtilityClass
public class PacketFactory {

    final static Map<Class<? extends Packet>, PacketCreator<? extends Packet>> CREATORS = new HashMap<>();

    final static Map<Integer, Class<? extends Packet>> IN_MAPPING = new HashMap<>();
    final static Map<Class<? extends Packet>, Integer> OUT_MAPPING = new HashMap<>();

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
    public static @Nullable Packet produce(final @NotNull Class<? extends Packet> packetClass, @NotNull FriendlyByteBuf buf) {
        PacketCreator<? extends Packet> creator = CREATORS.get(packetClass);
        if (creator == null) return null;
        return creator.create(buf);
    }

    /**
     * Returns class of the PacketIn from mapped packet id.
     * @param id id of the packet, including the mask of packet state
     * @return class of the packet
     */
    public static @Nullable Class<? extends Packet> getPacketInById(int id) {
        Class<? extends Packet> in = IN_MAPPING.get(id);
        if(in != null) return in;
        for(Map.Entry<Class<? extends Packet>, Integer> entry : OUT_MAPPING.entrySet()) {
            if(entry.getValue() != id) continue;
            return entry.getKey();
        }
        return null;
    }

    /**
     * Returns class of the packet using on Mojang mapping
     * @param id id of the packet
     * @param state state of the packet
     * @return class of the packet
     */
    public static @Nullable Class<? extends Packet> getPacketByRawId(int id, @NotNull PacketImpl.PacketState state) {
        Class<? extends Packet> in = IN_MAPPING.get(id | state.getMask());
        if(in != null) return in;
        for(Map.Entry<Class<? extends Packet>, Integer> entry : OUT_MAPPING.entrySet()) {
            if(entry.getValue() != (id | state.getMask())) continue;
            return entry.getKey();
        }
        return null;
    }

    /**
     * Returns id including the mask of the packet state from its class reference.
     * @param packetClass class of the packet
     * @return id of the packet, -1 if it doesn't exist
     */
    public static int getIdByPacket(@NotNull Class<? extends Packet> packetClass) {
        Integer out = OUT_MAPPING.get(packetClass);
        if(out != null) return out;
        for(Map.Entry<Integer, Class<? extends Packet>> entry : IN_MAPPING.entrySet()) {
            if(entry.getValue() != packetClass) continue;
            return entry.getKey();
        }
        return -1;
    }

    /**
     * Returns Mojang mapped id of the packet from its class reference.
     * @param packetClass class of the packet
     * @param state state of the packet
     * @return id of the packet, -1 if it doesn't exist
     */
    public static int getRawIdByPacket(@NotNull Class<? extends Packet> packetClass, @NotNull PacketImpl.PacketState state) {
        Integer out = OUT_MAPPING.get(packetClass);
        if(out != null) return out & ~state.getMask();
        for(Map.Entry<Integer, Class<? extends Packet>> entry : IN_MAPPING.entrySet()) {
            if(entry.getValue() != packetClass) continue;
            return entry.getKey() & ~state.getMask();
        }
        return -1;
    }

    /**
     * Returns state of a packets with given class registered in the factory.
     * @param packetClass class of the packet
     * @return state of the packets of given class
     */
    public static @Nullable Packet.PacketState getRegisteredState(@NotNull Class<? extends Packet> packetClass) {
        Integer out = OUT_MAPPING.get(packetClass);
        if(out != null)
            return Packet.PacketState.fromMask(out >> Packet.PacketState.OFFSET);
        for(Map.Entry<Integer, Class<? extends Packet>> entry : IN_MAPPING.entrySet()) {
            if(entry.getValue() != packetClass) continue;
            return Packet.PacketState.fromMask(entry.getKey() & (0b111 << Packet.PacketState.OFFSET));
        }
        return null;
    }

}
