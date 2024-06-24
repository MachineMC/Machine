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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.machinemc.barebones.key.NamespacedKey;
import org.machinemc.cogwheel.annotations.Comment;
import org.machinemc.cogwheel.config.Configuration;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.server.ServerStatus;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Cogwheel compatible implementation of server properties.
 */
@Getter
@SuppressWarnings("FieldMayBeFinal")
public class ServerPropertiesImpl implements ServerProperties, Configuration {

    /**
     * Path to the configuration file.
     */
    public static final String PATH = "server.properties";

    @Comment("The IP address of the server.")
    private String serverIP = "localhost";

    @Comment("The server port.")
    private int serverPort = 25565;

    @Comment({"Whether the server authenticates players using 3rd party service.",
            "",
            "It enables the requirement for Mojang user authentication, disallowing cracked Minecraft clients",
            "to join the server. Due to the lack of authentication required on an offline mode server,",
            "anyone can use any username to log in. This is a big security risk and is not recommended",
            "to disable outside of testing."})
    @Accessors(fluent = true)
    private boolean doesAuthenticate = true;


    @Comment("The URL of the service used for the authentication of players.")
    @Getter(AccessLevel.NONE)
    private @org.machinemc.cogwheel.annotations.Optional URL authService;

    {
        try {
            authService = URI.create("https://sessionserver.mojang.com/").toURL();
        } catch (MalformedURLException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Comment({"Whether the server requires sent messages being signed.",
            "If true, message sent by players can be reported to Mojang."})
    @Accessors(fluent = true)
    private boolean enforcesSecureChat = false;

    @Comment({"The number of allowed connected players at once.",
            "This can be set to '-1' to disable the limit."})
    private int maxPlayers = 20;

    @Comment({"Whether the server should hide online players in the multiplayer menu.",
            "If true, '???' is displayed instead the player counter."})
    @Accessors(fluent = true)
    private boolean hidePlayers = false;

    @Comment("The message of the day that is displayed in the multiplayer server menu.")
    @Getter(AccessLevel.NONE)
    private Component motd = TextComponent.of("A Machine Minecraft Server");

    @Comment("Path to the icon of the server that is displayed in the multiplayer server menu.")
    @Getter(AccessLevel.NONE)
    private @org.machinemc.cogwheel.annotations.Optional Path favicon = Path.of("icon.png");

    @Comment("Returns the default server world used to spawn newly connected players.")
    private NamespacedKey defaultWorld = NamespacedKey.machine("main");

    @Comment("Whether the players should have full access to the F3 menu.")
    @Accessors(fluent = true)
    private boolean hasReducedDebug = false;

    @Comment({"Returns servers brand.",
            "Server brand is identifier used in F3 screen and game logs."})
    private String serverBrand = "MachineMC";

    @Comment("Returns the view distance of the server worlds.")
    private int viewDistance = 8;

    @Comment({"Returns the simulation distance of the server worlds.",
            "Simulation distance defines how far away entities should tick."})
    private int simulationDistance = 8;

    @Override
    public Optional<URL> getAuthService() {
        return Optional.ofNullable(authService);
    }

    @Override
    public Component getMOTD() {
        return motd;
    }

    @Override
    public Optional<ServerStatus.Favicon> getIcon() {
        if (favicon == null) return Optional.empty();
        if (!favicon.toFile().exists()) return Optional.empty();
        try {
            return Optional.of(ServerStatus.Favicon.create(favicon));
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
