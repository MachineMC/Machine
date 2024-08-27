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
import org.machinemc.barebones.key.NamespacedKey;
import org.machinemc.client.cookie.Cookie;
import org.machinemc.network.protocol.PacketFlow;
import org.machinemc.network.protocol.PacketGroups;
import org.machinemc.network.protocol.PacketIDMap;
import org.machinemc.network.protocol.cookie.CookiePacketListener;
import org.machinemc.network.protocol.cookie.CookiePackets;
import org.machinemc.paklet.Packet;
import org.machinemc.paklet.PacketID;

/**
 * Plugin message packet used to request stored cookie from a player.
 */
@Data
@Packet(
        id = Packet.DYNAMIC_PACKET,
        group = {
                PacketGroups.Login.ClientBound.NAME,
                PacketGroups.Configuration.ClientBound.NAME,
                PacketGroups.Play.ClientBound.NAME
        },
        catalogue = CookiePackets.class
)
@NoArgsConstructor
@AllArgsConstructor
public class S2CCookieRequestPacket implements org.machinemc.network.protocol.Packet<CookiePacketListener> {

    @PacketID
    private static int id() {
        return PacketIDMap.compute(
                PacketGroups.Login.ClientBound.NAME, PacketGroups.Login.ClientBound.COOKIE_REQUEST,
                PacketGroups.Configuration.ClientBound.NAME, PacketGroups.Configuration.ClientBound.COOKIE_REQUEST,
                PacketGroups.Play.ClientBound.NAME, PacketGroups.Play.ClientBound.COOKIE_REQUEST
        );
    }

    /**
     * Key of the cookie.
     */
    private NamespacedKey key;

    public S2CCookieRequestPacket(final Cookie cookie) {
        this(cookie.key());
    }

    @Override
    public void handle(final CookiePacketListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PacketFlow flow() {
        return PacketFlow.CLIENTBOUND;
    }

}
