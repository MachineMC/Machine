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
package org.machinemc.network.protocol.cookie.clientbound;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.machinemc.client.cookie.Cookie;
import org.machinemc.network.protocol.PacketFlow;
import org.machinemc.network.protocol.PacketGroups;
import org.machinemc.network.protocol.PacketIDMap;
import org.machinemc.network.protocol.cookie.CookiePacketListener;
import org.machinemc.network.protocol.cookie.CookiePackets;
import org.machinemc.paklet.Packet;
import org.machinemc.paklet.PacketID;

/**
 * Plugin message packet used to store cookie to a player session.
 */
@Data
@Packet(
        id = Packet.DYNAMIC_PACKET,
        group = {
                PacketGroups.Configuration.ClientBound.NAME,
                PacketGroups.Play.ClientBound.NAME
        },
        catalogue = CookiePackets.class
)
@NoArgsConstructor
@AllArgsConstructor
public class S2CStoreCookiePacket implements org.machinemc.network.protocol.Packet<CookiePacketListener> {

    @PacketID
    private static int id() {
        return PacketIDMap.compute(
                PacketGroups.Configuration.ClientBound.NAME, PacketGroups.Configuration.ClientBound.STORE_COOKIE,
                PacketGroups.Play.ClientBound.NAME, PacketGroups.Play.ClientBound.STORE_COOKIE
        );
    }

    /**
     * Cookie to store.
     */
    private Cookie cookie;

    @Override
    public void handle(final CookiePacketListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PacketFlow flow() {
        return PacketFlow.CLIENTBOUND;
    }

}
