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
package org.machinemc.network.protocol.cookie.serverbound;

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
import org.machinemc.paklet.modifiers.Optional;

/**
 * Plugin message packet used to send data to the client.
 */
@Data
@Packet(
        id = Packet.DYNAMIC_PACKET,
        group = {
                PacketGroups.Login.ServerBound.NAME,
                PacketGroups.Configuration.ServerBound.NAME,
                PacketGroups.Play.ServerBound.NAME
        },
        catalogue = CookiePackets.class
)
@NoArgsConstructor
@AllArgsConstructor
public class C2SCookieResponsePacket implements org.machinemc.network.protocol.Packet<CookiePacketListener> {

    @PacketID
    private static int id() {
        return PacketIDMap.compute(
                PacketGroups.Login.ServerBound.NAME, PacketGroups.Login.ServerBound.COOKIE_RESPONSE,
                PacketGroups.Configuration.ServerBound.NAME, PacketGroups.Configuration.ServerBound.COOKIE_RESPONSE,
                PacketGroups.Play.ServerBound.NAME, PacketGroups.Play.ServerBound.COOKIE_RESPONSE
        );
    }

    /**
     * Name of the plugin channel used to send the data.
     */
    private NamespacedKey channel;

    /**
     * Any data sent by the plugin.
     */
    private @Optional byte[] data;

    /**
     * Returns the requested cookie or empty optional
     * if there is none stored within the client.
     *
     * @return cookie
     */
    public java.util.Optional<Cookie> getCookie() {
        return (data == null || data.length == 0) ? java.util.Optional.empty() : java.util.Optional.of(new Cookie(channel, data));
    }

    @Override
    public void handle(final CookiePacketListener listener) {
        listener.onCookieResponse(this);
    }

    @Override
    public PacketFlow flow() {
        return PacketFlow.SERVERBOUND;
    }

}
