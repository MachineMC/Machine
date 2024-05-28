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
package org.machinemc.server.file;

import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.machinemc.api.Server;
import org.machinemc.api.file.ServerProperties;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.Difficulty;
import org.machinemc.api.world.WorldType;
import org.machinemc.cogwheel.TypedClassInitiator;
import org.machinemc.cogwheel.annotations.Comment;
import org.machinemc.cogwheel.config.ConfigSerializer;
import org.machinemc.cogwheel.config.Configuration;
import org.machinemc.cogwheel.properties.CommentedProperties;
import org.machinemc.cogwheel.properties.PropertiesConfigSerializer;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.server.Machine;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Default implementation of server properties.
 */
@Getter
public class ServerPropertiesImpl implements ServerProperties, Configuration {

    public static final String PROPERTIES_FILE_NAME = "server.properties";
    public static final String ICON_FILE_NAME = "icon.png";

    public static final Function<Server, ConfigSerializer<CommentedProperties>> CONFIG_SERIALIZER = server -> PropertiesConfigSerializer.builder()
            .registry(server.getSerializerRegistry())
            .classInitiator(new TypedClassInitiator(Server.class, server))
            .errorHandler((context, error) -> server.getConsole().severe(error.type() + ": " + error.message()))
            .emptyLineBetweenEntries(true)
            .build();

    private final Server server;

    @Comment("Server ip")
    private String serverIP = "localhost";
    @Comment("Server port")
    private @Range(from = 0, to = 65536) int serverPort = 25565;
    @Comment("Online mode")
    private boolean online = true;
    @Comment("Server max players, -1 for not limit")
    private int maxPlayers = -1;
    @Getter(AccessLevel.NONE)
    @Comment("Server list message in json chat format")
    private Component motd = TextComponent.of("A Machine Minecraft Server");
    @Comment("World where players spawn in if not specified differently")
    private NamespacedKey defaultWorld = NamespacedKey.machine("main");
    @Comment("Default difficulty when creating a new world")
    private Difficulty defaultDifficulty = Difficulty.DEFAULT_DIFFICULTY;
    @Comment("The world type of the default world")
    private WorldType defaultWorldType = WorldType.NORMAL;
    @Comment("If true, the client will show reduces information on the debug screen")
    private boolean reducedDebugScreen = false;
    @Comment("The render distance (2-32)")
    private @Range(from = 2, to = 32) int viewDistance = 8;
    @Comment("The distance that the client will process specific things, such as entities")
    private int simulationDistance = 8;
    @Comment({
            "How often the server reads incoming packets in milliseconds",
            "If the value is 0 then the server will read the packets once every tick"
    })
    private int serverResponsiveness = 0;
    @Getter(AccessLevel.NONE)
    @Comment("How many ticks per second the server is run on")
    private int tps = 20;
    private String serverBrand = "Machine server";

    @Getter(AccessLevel.NONE)
    private final @Nullable BufferedImage icon;
    @Getter(AccessLevel.NONE)
    private final @Nullable String encodedIcon;

    private static final int ICON_SIZE = 64;

    public ServerPropertiesImpl(final Server server) {
        this.server = Objects.requireNonNull(server, "Server can not be null");
        final File png = new File(server.getDirectory(), ICON_FILE_NAME);
        BufferedImage icon = null;
        String encodedIcon = null;
        if (png.exists()) {
            try {
                icon = new BufferedImage(ICON_SIZE, ICON_SIZE, BufferedImage.TYPE_INT_RGB);
                icon.createGraphics().drawImage(ImageIO.read(png), 0, 0, ICON_SIZE, ICON_SIZE, null);
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                ImageIO.write(icon, "png", out);
                encodedIcon = Base64.getEncoder().encodeToString(out.toByteArray());
            } catch (Exception e) {
                server.getConsole().severe("Unable to load server-icon.png! Is it a png image?");
            }
        }
        this.icon = icon;
        this.encodedIcon = encodedIcon;
    }

    /**
     * Saves the server properties to the specified file.
     *
     * @param file the destination
     */
    public void save(final File file) {
        CONFIG_SERIALIZER.apply(server).save(file, this);
    }

    @Override
    public String getName() {
        return PROPERTIES_FILE_NAME;
    }

    @Override
    public Optional<InputStream> getOriginal() {
        return Optional.ofNullable(Machine.CLASS_LOADER.getResourceAsStream(PROPERTIES_FILE_NAME));
    }

    @Override
    public Component getMOTD() {
        return motd;
    }

    @Override
    public int getTPS() {
        return tps;
    }

    @Override
    public Optional<BufferedImage> getIcon() {
        return Optional.ofNullable(icon);
    }

    @Override
    public Optional<String> getEncodedIcon() {
        return Optional.ofNullable(encodedIcon);
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Constructs a new server properties object from a properties file.
     *
     * @param server server instance
     * @param file server properties file
     * @return newly created server properties
     */
    public static ServerProperties load(final Server server, final File file) {
        return CONFIG_SERIALIZER.apply(server).load(file, ServerPropertiesImpl.class);
    }

}
