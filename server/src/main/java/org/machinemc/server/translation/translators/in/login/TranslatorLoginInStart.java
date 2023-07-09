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
package org.machinemc.server.translation.translators.in.login;

import org.machinemc.api.auth.OnlineServer;
import org.machinemc.api.entities.player.PlayerProfile;
import org.machinemc.api.network.PlayerConnection;
import org.machinemc.server.entities.ServerPlayer;
import org.machinemc.server.entities.player.ServerPlayerProfile;
import org.machinemc.server.network.ClientConnection;
import org.machinemc.server.network.packets.in.login.PacketLoginInStart;
import org.machinemc.server.network.packets.out.login.PacketLoginOutEncryptionRequest;
import org.machinemc.server.network.packets.out.login.PacketLoginOutSuccess;
import org.machinemc.server.translation.PacketTranslator;

public class TranslatorLoginInStart extends PacketTranslator<PacketLoginInStart> {

    @Override
    public boolean translate(final ClientConnection connection, final PacketLoginInStart packet) {
        return true;
    }

    @Override
    public void translateAfter(final ClientConnection connection, final PacketLoginInStart packet) {
        connection.setLoginUsername(packet.getUsername());
        if (!connection.getServer().isOnline()) {
            final PlayerProfile profile = ServerPlayerProfile.offline(packet.getUsername());
            connection.send(new PacketLoginOutSuccess(
                    profile.getUUID(),
                    profile.getUsername(),
                    profile.getTextures().orElse(null)
            ));
            if (connection.getState().orElse(null) == PlayerConnection.ClientState.DISCONNECTED)
                return;
            connection.setState(PlayerConnection.ClientState.PLAY);
            ServerPlayer.spawn(connection.getServer(), profile, connection);
            return;
        }
        final OnlineServer onlineServer = connection.getServer().getOnlineServer().orElseThrow(() -> {
            connection.disconnect();
            return new IllegalArgumentException("Online server hasn't been initialized");
        });
        final byte[] publicKey = onlineServer.getKey().getPublic().getEncoded();
        final byte[] verifyToken = onlineServer.nextVerifyToken();

        connection.setPublicKeyData(packet.getPublicKeyData());
        connection.send(new PacketLoginOutEncryptionRequest(publicKey, verifyToken));
    }

    @Override
    public Class<PacketLoginInStart> packetClass() {
        return PacketLoginInStart.class;
    }

}
