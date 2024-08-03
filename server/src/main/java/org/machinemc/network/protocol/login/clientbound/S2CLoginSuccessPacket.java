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
package org.machinemc.network.protocol.login.clientbound;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.machinemc.barebones.profile.GameProfile;
import org.machinemc.network.protocol.PacketFlow;
import org.machinemc.network.protocol.PacketGroups;
import org.machinemc.network.protocol.login.LoginPacketListener;
import org.machinemc.paklet.Packet;

/**
 * Packet sent by the server for client to exit the login phase.
 */
@Data
@Packet(
        id = PacketGroups.Login.ClientBound.LOGIN_SUCCESS,
        group = PacketGroups.Login.ClientBound.NAME,
        catalogue = PacketGroups.Login.ClientBound.class
)
@NoArgsConstructor
@AllArgsConstructor
public class S2CLoginSuccessPacket implements org.machinemc.network.protocol.Packet<LoginPacketListener> {

    /**
     * Game profile of the player.
     */
    private GameProfile profile;

    /**
     * Whether the client should immediately disconnect upon a packet processing error.
     * The Notchian client silently ignores them when this flag is false.
     * <p>
     * This field was temporarily added in 1.20.5 as a way to aid modded servers with
     * the transition to the new data pack & registry system,
     * allowing them to tell the client to silently ignore packets containing inconsistent data.
     * <p>
     * It will likely be removed soon.
     */
    private boolean strictErrorHandling;

    @Override
    public void handle(final LoginPacketListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PacketFlow flow() {
        return PacketFlow.CLIENTBOUND;
    }

}
