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
 * If not, see https://www.gnu.org/licenses/.
 */
package org.machinemc.server.network.packets;

import java.util.Objects;

/**
 * Packet sent from client to server.
 */
public abstract class PacketIn extends ServerPacket {

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
        Objects.requireNonNull(packetClass, "Packet class can not be null");
        Objects.requireNonNull(state, "Packet state can not be null");
        Objects.requireNonNull(creator, "Packet creator can not be null");
        if (!PacketState.in().contains(state))
            throw new IllegalStateException("Packet of state " + state + " can not be registered as PacketIn");
        final int fullID = id | state.getMask();
        PacketFactory.IN_MAPPING.put(fullID, packetClass);
        PacketFactory.CREATORS.put(packetClass, creator);
    }

    /**
     * @return clone of the packet
     */
    public abstract PacketIn clone();

    @Override
    public String toString() {
        return "PacketIn";
    }

}
