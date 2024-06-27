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
package org.machinemc.network.protocol.login.serverbound;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.machinemc.network.protocol.PacketFlow;
import org.machinemc.network.protocol.PacketGroups;
import org.machinemc.network.protocol.login.LoginPacketListener;
import org.machinemc.paklet.Packet;
import org.machinemc.paklet.metadata.Length;

import java.util.UUID;

/**
 * Packet for starting the logging in process.
 */
@Data
@Packet(
        id = PacketGroups.Login.ServerBound.HELLO,
        group = PacketGroups.Login.ServerBound.NAME,
        catalogue = PacketGroups.Login.ServerBound.class
)
@NoArgsConstructor
@AllArgsConstructor
public class C2SHelloPacket implements org.machinemc.network.protocol.Packet<LoginPacketListener> {

    /**
     * Username of the player.
     */
    private @Length(max = 16) String username;

    /**
     * UUID of the player.
     */
    private UUID profileID;

    @Override
    public void handle(final LoginPacketListener listener) {
        listener.onHello(this);
    }

    @Override
    public PacketFlow flow() {
        return PacketFlow.SERVERBOUND;
    }

}
