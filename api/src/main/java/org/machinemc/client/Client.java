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

import org.machinemc.barebones.profile.GameProfile;
import org.machinemc.barebones.profile.PlayerTextures;
import org.machinemc.client.cookie.CookieHolder;
import org.machinemc.client.resourcepack.ResourcePackReceiver;
import org.machinemc.entity.player.Player;
import org.machinemc.entity.player.PlayerSettings;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.components.TranslationComponent;

import java.util.*;

/**
 * Contains shared logic for a client connected to the server.
 * <p>
 * This API is shared by both {@link LoadingPlayer} and {@link Player} and can be
 * safely used in both configuration and play phases.
 */
public interface Client extends CookieHolder, ResourcePackReceiver {

    // TODO plugin messaging

    /**
     * Returns game profile of the client.
     *
     * @return game profile
     */
    GameProfile getGameProfile();

    /**
     * Returns username of the client.
     *
     * @return username of the client
     */
    default String getUsername() {
        return getGameProfile().name();
    }

    /**
     * Returns UUID of the client.
     *
     * @return UUID of the client
     */
    default UUID getUUID() {
        return getGameProfile().uuid();
    }

    /**
     * Returns skin textures of the player or empty optional if
     * there are none textures assigned to the player's game profile.
     *
     * @return skin textures of the player
     */
    Optional<PlayerTextures> getSkinTextures();

    /**
     * Returns multiplayer settings of this player.
     *
     * @return multiplayer settings of the player
     */
    PlayerSettings getMultiplayerSettings();

    /**
     * Disconnects the client.
     */
    default void disconnect() {
        disconnect(TranslationComponent.of("disconnect.disconnected"));
    }

    /**
     * Disconnects the client with given reason.
     *
     * @param reason reason
     */
    void disconnect(Component reason);

    /**
     * Notifies the client that it should transfer to the given server.
     * Cookies previously stored are preserved between server transfers.
     *
     * @param hostname the hostname or IP of the server
     * @param port the port of the server
     */
    void transfer(String hostname, int port);

    /**
     * Updates list of key-value text entries that are included in any crash
     * or disconnection report generated during connection to the server.
     *
     * @param details new report details
     */
    void setCustomReportDetails(Map<String, String> details);

    /**
     * Updates list links client will display in the menu available
     * from the pause menu.
     * <p>
     * Link labels can be built-in or custom (i.e., any text).
     *
     * @param links new server links
     */
    default void setServerLinks(ServerLink... links) {
        setServerLinks(List.of(links));
    }

    /**
     * Updates list links client will display in the menu available
     * from the pause menu.
     * <p>
     * Link labels can be built-in or custom (i.e., any text).
     *
     * @param links new server links
     */
    void setServerLinks(Collection<ServerLink> links);

}
