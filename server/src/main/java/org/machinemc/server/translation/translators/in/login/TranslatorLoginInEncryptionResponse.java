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
import org.machinemc.scriptive.components.TranslationComponent;
import org.machinemc.server.auth.MojangAuth;
import org.machinemc.server.entities.ServerPlayer;
import org.machinemc.server.entities.player.PlayerProfileImpl;
import org.machinemc.server.entities.player.PlayerTexturesImpl;
import org.machinemc.server.exception.ClientException;
import org.machinemc.server.network.ClientConnection;
import org.machinemc.server.network.packets.in.login.PacketLoginInEncryptionResponse;
import org.machinemc.server.network.packets.out.login.PacketLoginOutSuccess;
import org.machinemc.server.translation.PacketTranslator;
import org.machinemc.server.utils.UUIDUtils;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.machinemc.server.network.packets.out.login.PacketLoginOutEncryptionRequest.SERVER_ID;

public class TranslatorLoginInEncryptionResponse extends PacketTranslator<PacketLoginInEncryptionResponse> {

    @Override
    public boolean translate(final ClientConnection connection, final PacketLoginInEncryptionResponse packet) {
        return true;
    }

    @Override
    public void translateAfter(final ClientConnection connection, final PacketLoginInEncryptionResponse packet) {
        final OnlineServer onlineServer = connection.getServer().getOnlineServer();
        if (onlineServer == null) {
            connection.disconnect();
            return;
        }
        if (connection.getLoginUsername() == null) {
            connection.disconnect();
            return;
        }

        final SecretKey secretkey = onlineServer.getSecretKey(onlineServer.getKey().getPrivate(), packet.getSecret());
        connection.setSecretKey(secretkey);

        final String serverID = new BigInteger(onlineServer.digestData(
                SERVER_ID,
                onlineServer.getKey().getPublic(),
                secretkey)
        ).toString(16);
        final String username = URLEncoder.encode(connection.getLoginUsername(), StandardCharsets.UTF_8);

        MojangAuth.getAuthData(serverID, username).thenAccept(json -> {
            if (json == null) {
                try {
                    connection.disconnect(TranslationComponent.of("disconnect.loginFailedInfo.invalidSession"));
                } catch (Exception exception) {
                    throw new RuntimeException(exception);
                }
                return;
            }
            final UUID authUUID = UUIDUtils.parseUUID(json.get("id").getAsString());
            if (authUUID == null) {
                connection.disconnect();
                return;
            }
            final String authUsername = json.get("name").getAsString();
            final PlayerTexturesImpl playerTextures = PlayerTexturesImpl.buildSkin(
                    json.getAsJsonArray("properties").get(0)
            );
            final PlayerProfile profile = PlayerProfileImpl.online(authUsername, authUUID, playerTextures);
            connection.setCompression(256); // TODO should be in properties
            connection.send(new PacketLoginOutSuccess(authUUID, authUsername, profile.getTextures()));
            if (connection.getState() == ClientConnection.ClientState.DISCONNECTED)
                return;
            connection.setState(ClientConnection.ClientState.PLAY);
            ServerPlayer.spawn(connection.getServer(), profile, connection);
        }).exceptionally(exception -> {
            connection.getServer().getExceptionHandler().handle(new ClientException(connection, exception.getCause()));
            return null;
        });
    }

    @Override
    public Class<PacketLoginInEncryptionResponse> packetClass() {
        return PacketLoginInEncryptionResponse.class;
    }

}
