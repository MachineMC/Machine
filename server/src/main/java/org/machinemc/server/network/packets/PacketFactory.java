/*
 * This file is part of Machine.
 *
 * Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Machine.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package org.machinemc.server.network.packets;

import org.machinemc.api.network.packets.Packet;
import org.machinemc.server.utils.ClassUtils;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles the creation of Packet instances.
 */
public final class PacketFactory {

    static final Map<Class<? extends Packet>, PacketCreator<? extends Packet>> CREATORS = new HashMap<>();

    static final Map<Integer, Class<? extends Packet>> IN_MAPPING = new HashMap<>();
    static final Map<Class<? extends Packet>, Integer> OUT_MAPPING = new HashMap<>();

    static {
        try {
            ClassUtils.loadClasses(PacketFactory.class.getPackageName());
        } catch (IOException ignored) { }
    }

    private PacketFactory() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates new instance of a packet of provided class using the {@link FriendlyByteBuf}.
     * @param packetClass class reference of the packet
     * @param buf buffer containing the packet data
     * @return instance of the packet
     */
    public static @Nullable Packet produce(final Class<? extends Packet> packetClass, final FriendlyByteBuf buf) {
        final PacketCreator<? extends Packet> creator = CREATORS.get(packetClass);
        if (creator == null) return null;
        return creator.create(buf);
    }

    /**
     * Returns class of the PacketIn from mapped packet id.
     * @param id id of the packet, including the mask of packet state
     * @return class of the packet
     */
    public static @Nullable Class<? extends Packet> getPacketInById(final int id) {
        final Class<? extends Packet> in = IN_MAPPING.get(id);
        if (in != null) return in;
        for (final Map.Entry<Class<? extends Packet>, Integer> entry : OUT_MAPPING.entrySet()) {
            if (entry.getValue() != id) continue;
            return entry.getKey();
        }
        return null;
    }

    /**
     * Returns class of the packet using on Mojang mapping.
     * @param id id of the packet
     * @param state state of the packet
     * @return class of the packet
     */
    public static @Nullable Class<? extends Packet> getPacketByRawId(final int id, final PacketImpl.PacketState state) {
        final Class<? extends Packet> in = IN_MAPPING.get(id | state.getMask());
        if (in != null) return in;
        for (final Map.Entry<Class<? extends Packet>, Integer> entry : OUT_MAPPING.entrySet()) {
            if (entry.getValue() != (id | state.getMask())) continue;
            return entry.getKey();
        }
        return null;
    }

    /**
     * Returns id including the mask of the packet state from its class reference.
     * @param packetClass class of the packet
     * @return id of the packet, -1 if it doesn't exist
     */
    public static int getIdByPacket(final Class<? extends Packet> packetClass) {
        final Integer out = OUT_MAPPING.get(packetClass);
        if (out != null) return out;
        for (final Map.Entry<Integer, Class<? extends Packet>> entry : IN_MAPPING.entrySet()) {
            if (entry.getValue() != packetClass) continue;
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
    public static int getRawIdByPacket(final Class<? extends Packet> packetClass, final PacketImpl.PacketState state) {
        final Integer out = OUT_MAPPING.get(packetClass);
        if (out != null) return out & ~state.getMask();
        for (final Map.Entry<Integer, Class<? extends Packet>> entry : IN_MAPPING.entrySet()) {
            if (entry.getValue() != packetClass) continue;
            return entry.getKey() & ~state.getMask();
        }
        return -1;
    }

    /**
     * Returns state of a packets with given class registered in the factory.
     * @param packetClass class of the packet
     * @return state of the packets of given class
     */
    public static @Nullable Packet.PacketState getRegisteredState(final Class<? extends Packet> packetClass) {
        final Integer out = OUT_MAPPING.get(packetClass);
        if (out != null)
            return Packet.PacketState.fromMask(out >> Packet.PacketState.OFFSET);
        for (final Map.Entry<Integer, Class<? extends Packet>> entry : IN_MAPPING.entrySet()) {
            if (entry.getValue() != packetClass) continue;
            return Packet.PacketState.fromMask(entry.getKey() & (0b111 << Packet.PacketState.OFFSET));
        }
        return null;
    }

}
