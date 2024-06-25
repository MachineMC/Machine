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
package org.machinemc.file;

import org.jetbrains.annotations.Range;
import org.machinemc.barebones.key.NamespacedKey;
import org.machinemc.scriptive.components.Component;
import org.machinemc.server.ServerStatus;

import java.net.URL;
import java.util.Locale;
import java.util.Optional;

/**
 * Represents server properties file ({@code server.properties}).
 * <p>
 * These properties allow for very basic configuration of the server.
 */
public interface ServerProperties {

    /**
     * Returns the IP address of the server.
     *
     * @return IP address of the server
     */
    String getServerIP();

    /**
     * Returns the server port.
     *
     * @return server port
     */
    @Range(from = 0, to = 65536) int getServerPort();

    /**
     * Returns whether the server authenticates players using 3rd party
     * service.
     * <p>
     * It enables the requirement for Mojang user authentication, disallowing cracked Minecraft clients
     * to join the server. Due to the lack of authentication required on an offline mode server,
     * anyone can use any username to log in. This is a big security risk and is not recommended
     * to disable outside of testing.
     *
     * @return whether the server authenticates its players
     */
    boolean doesAuthenticate();

    /**
     * Returns the URL of the service used for the authentication of players.
     *
     * @return URL of authentication service
     * @see #doesAuthenticate()
     */
    Optional<URL> getAuthService();

    /**
     * Whether the server requires sent messages being signed.
     * <p>
     * If true, message sent by players can be reported to Mojang.
     *
     * @return whether server enforces signed messages
     */
    boolean enforcesSecureChat();

    /**
     * Returns the number of allowed connected players at once.
     * <p>
     * This can be set to {@code -1} to disable the limit.
     *
     * @return player limit
     */
    int getMaxPlayers();

    /**
     * Whether the server should hide online players in the
     * multiplayer menu.
     * <p>
     * If true, {@code ???} is displayed instead the player counter.
     *
     * @return whether the server should hide online players
     */
    boolean hidePlayers();

    /**
     * Returns the message of the day that is displayed in the multiplayer
     * server menu.
     *
     * @return message of the day
     */
    Component getMOTD();

    /**
     * Returns the icon of the server that is displayed in the multiplayer
     * server menu.
     *
     * @return server icon
     */
    Optional<ServerStatus.Favicon> getIcon();

    /**
     * Returns the default server world used to spawn newly connected
     * players.
     *
     * @return default server world
     */
    /* TODO replace with World reference */ NamespacedKey getDefaultWorld();

    /**
     * Whether the players should have full access to the F3 menu.
     *
     * @return whether the players should have full access to the F3 menu
     */
    boolean hasReducedDebug();

    /**
     * Returns servers brand.
     * <p>
     * Server brand is identifier used in F3 screen and game logs.
     *
     * @return server brand
     */
    String getServerBrand();

    /**
     * Returns the view distance of the server worlds.
     *
     * @return view distance
     */
    int getViewDistance();

    /**
     * Returns the simulation distance of the server worlds.
     * <p>
     * Simulation distance defines how far away entities should tick.
     *
     * @return simulation distance
     */
    int getSimulationDistance();

    /**
     * Language used to translate in-game translation components.
     *
     * @return language used by terminal translator
     * @see org.machinemc.scriptive.components.TranslationComponent
     */
    Locale getLanguage();

}
