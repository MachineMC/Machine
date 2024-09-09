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
package org.machinemc.client;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;
import org.machinemc.barebones.key.NamespacedKey;
import org.machinemc.barebones.profile.GameProfile;
import org.machinemc.barebones.profile.PlayerTextures;
import org.machinemc.chat.ChatMode;
import org.machinemc.client.cookie.Cookie;
import org.machinemc.client.resourcepack.ResourcePackRequest;
import org.machinemc.entity.player.MainHand;
import org.machinemc.entity.player.Player;
import org.machinemc.entity.player.PlayerSettings;
import org.machinemc.network.ClientConnection;
import org.machinemc.network.protocol.configuration.clientbound.S2CResetChatPacket;
import org.machinemc.network.protocol.cookie.clientbound.S2CCookieRequestPacket;
import org.machinemc.network.protocol.cookie.clientbound.S2CStoreCookiePacket;
import org.machinemc.network.protocol.cookie.serverbound.C2SCookieResponsePacket;
import org.machinemc.scriptive.components.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Authenticated player connected to the server.
 * <p>
 * This instance is used for both configuration and play stages of the game and
 * is alive until the client is disconnected.
 */
@Getter
public class ServerPlayer implements Player, LoadingPlayer {

    private final ClientConnection connection;

    private ClientState state;

    private final GameProfile gameProfile;
    @Getter(AccessLevel.NONE)
    private final @Nullable PlayerTextures skinTextures;

    private PlayerSettings multiplayerSettings;

    private final CookieRequests cookieRequests = new CookieRequests();

    @SneakyThrows
    public ServerPlayer(final ClientConnection connection, final GameProfile gameProfile) {
        Preconditions.checkState(connection.getPlayer().isEmpty(), "There is another player already assigned to this client connection");

        this.connection = Preconditions.checkNotNull(connection, "Player connection can not be null");
        state = ClientState.CONFIGURATION;
        this.gameProfile = Preconditions.checkNotNull(gameProfile, "Player game profile can not be null");

        skinTextures = gameProfile.getProperty(PlayerTextures.TEXTURES).isPresent()
                ? PlayerTextures.create(gameProfile).orElse(null)
                : null;

        // default settings
        setMultiplayerSettings(new PlayerSettings(
                "en_us",
                (byte) 2,
                ChatMode.ENABLED,
                true,
                Collections.emptySet(),
                MainHand.RIGHT,
                false,
                false)
        );
    }

    @Override
    public CompletableFuture<Player> switchToGame() {
        return null;
    }

    @Override
    public void resetChat() {
        checkState(ClientState.CONFIGURATION);
        connection.sendPacket(new S2CResetChatPacket(), false);
    }

    @Override
    public CompletableFuture<LoadingPlayer> switchToConfiguration() {
        return null;
    }

    /**
     * Updates the multiplayer settings of the player.
     *
     * @param multiplayerSettings new multiplayer settings
     */
    public void setMultiplayerSettings(final PlayerSettings multiplayerSettings) {
        this.multiplayerSettings = Preconditions.checkNotNull(multiplayerSettings, "Player settings can not be null");
    }

    @Override
    public Optional<PlayerTextures> getSkinTextures() {
        return Optional.ofNullable(skinTextures);
    }

    @Override
    public void disconnect(final Component reason) {
        connection.disconnect(reason);
    }

    @Override
    public void transfer(final String hostname, final int port) {

    }

    @Override
    public void setCustomReportDetails(final Map<String, String> details) {

    }

    @Override
    public void setServerLinks(final Collection<ServerLink> links) {

    }

    @Override
    public CompletableFuture<Optional<Cookie>> requestCookie(final NamespacedKey key) {
        final CompletableFuture<Optional<Cookie>> future = cookieRequests.create(key);
        connection.sendPacket(new S2CCookieRequestPacket(key), true);
        return future;
    }

    /**
     * Called when client responds to a cookie request.
     *
     * @param response response
     */
    public void onCookieResponse(final C2SCookieResponsePacket response) {
        cookieRequests.onResponse(response);
    }

    @Override
    public CompletableFuture<Optional<Cookie>> storeCookie(final Cookie cookie) {
        final CompletableFuture<Optional<Cookie>> future = requestCookie(cookie);
        connection.sendPacket(new S2CStoreCookiePacket(cookie), false);
        return future;
    }

    @Override
    public void sendResourcePack(final ResourcePackRequest request, final ResourcePackRequest.Callback callback) {

    }

    @Override
    public void removeResourcePack(final UUID id) {

    }

    @Override
    public void removeAllResourcePacks() {

    }

    @Override
    public String toString() {
        return getUsername();
    }

    /**
     * Checks if player is in given state.
     *
     * @param state state
     */
    private void checkState(final ClientState state) {
        Preconditions.checkState(getState() == state, "Player is not in " + state + " state");
    }

}
