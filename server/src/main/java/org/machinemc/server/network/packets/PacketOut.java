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

/**
 * Packet sent from server to client.
 */
public abstract class PacketOut extends ServerPacket {

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
        if (!PacketState.out().contains(state))
            throw new IllegalStateException("Packet of state " + state + " can not be registered as PacketOut");
        final int fullID = id | state.getMask();
        PacketFactory.OUT_MAPPING.put(packetClass, fullID);
        PacketFactory.CREATORS.put(packetClass, creator);
    }

    /**
     * @return clone of the packet
     */
    public abstract PacketOut clone();

    @Override
    public String toString() {
        return "PacketOut";
    }

}
