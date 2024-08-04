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
package org.machinemc.network.protocol.listeners;

import com.google.common.base.Preconditions;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Blocking;
import org.machinemc.auth.AuthService;
import org.machinemc.barebones.profile.GameProfile;
import org.machinemc.file.ServerProperties;
import org.machinemc.network.ClientConnection;
import org.machinemc.auth.Crypt;
import org.machinemc.network.protocol.ConnectionState;
import org.machinemc.network.protocol.login.LoginPacketListener;
import org.machinemc.network.protocol.login.clientbound.S2CEncryptionRequestPacket;
import org.machinemc.network.protocol.login.clientbound.S2CLoginSuccessPacket;
import org.machinemc.network.protocol.login.serverbound.C2SEncryptionResponsePacket;
import org.machinemc.network.protocol.login.serverbound.C2SHelloPacket;
import org.machinemc.network.protocol.login.serverbound.C2SLoginAcknowledgedPacket;
import org.machinemc.network.protocol.pluginmessage.serverbound.C2SPluginMessagePacket;
import org.machinemc.scriptive.components.TranslationComponent;

import javax.crypto.SecretKey;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.concurrent.ExecutionException;

/**
 * Login packet listener used by the server.
 */
public class ServerLoginPacketListener implements LoginPacketListener {

    private final ClientConnection connection;

    public ServerLoginPacketListener(final ClientConnection connection) {
        this.connection = Preconditions.checkNotNull(connection, "Client connection can not be null");
    }

    @Override
    @SneakyThrows
    public void onHello(final C2SHelloPacket packet) {
        connection.setPreLoginData(packet);

        final ServerProperties properties = connection.getServer().getServerProperties();

        if (properties.alwaysEncrypt() || properties.doesAuthenticate()) {
            final byte[] publicKey = connection.getServer().getEncryptionKey().getPublic().getEncoded();
            final byte[] verifyToken = Crypt.nextVerifyToken(4); // notchian server uses 4 bytes long verify tokens
            connection.sendPacket(new S2CEncryptionRequestPacket("", publicKey, verifyToken, properties.doesAuthenticate()), true); // server ID is always empty on notchian server
            return;
        }

        // offline server without encryption
        finishLogin(GameProfile.forOfflinePlayer(packet.getUsername()));
    }

    @Override
    @SneakyThrows
    public void onEncryptionResponse(final C2SEncryptionResponsePacket packet) {
        final PrivateKey privateKey = connection.getServer().getEncryptionKey().getPrivate();
        final SecretKey secretkey = Crypt.decryptSecretKey(privateKey, packet.getSharedSecret());
        connection.enableEncryption(secretkey);

        if (!connection.getServer().getServerProperties().doesAuthenticate()) {
            finishLogin(GameProfile.forOfflinePlayer(connection.getPreLoginData().username()));
            return;
        }

        final String serverHash = Crypt.getServerHash(
                "",
                connection.getServer().getEncryptionKey().getPublic(),
                secretkey
        );
        final String username = URLEncoder.encode(connection.getPreLoginData().username(), StandardCharsets.UTF_8);
        final URL authServiceURL = connection.getServer().getServerProperties().getAuthService().orElseThrow(
                () -> new IllegalStateException("Missing auth service in server properties")
        );

        final AuthService authService = new AuthService(authServiceURL);
        final GameProfile gameProfile = authService.getGameProfile(serverHash, username).handle((result, exception) -> {
            if (exception != null) {
                connection.disconnect(TranslationComponent.of("multiplayer.disconnect.authservers_down"));
                return null;
            }
            if (result.isEmpty()) {
                connection.disconnect(TranslationComponent.of("disconnect.loginFailedInfo.invalidSession"));
                return null;
            }
            return result.get();
        }).get();

        if (gameProfile == null) return;
        finishLogin(gameProfile);
    }

    /**
     * Finishes the login sequence for the player, using the given game profile.
     *
     * @param profile game profile
     */
    @Blocking
    private void finishLogin(final GameProfile profile) throws ExecutionException, InterruptedException {
        final int compressionThreshold = connection.getServer().getServerProperties().getCompressionThreshold();
        // skips the packet if the compression is disabled
        if (compressionThreshold >= 0) connection.setCompression(compressionThreshold).get();

        connection.sendPacket(new S2CLoginSuccessPacket(profile, true), true);
    }

    @Override
    public void onLoginAcknowledged(final C2SLoginAcknowledgedPacket packet) {
        connection.setupInboundProtocol(ConnectionState.CONFIGURATION, new ServerConfigurationPacketListener(connection));
        connection.setupOutboundProtocol(ConnectionState.CONFIGURATION);
    }

    @Override
    public void onPluginMessage(final C2SPluginMessagePacket packet) {
        // TODO
    }

}
