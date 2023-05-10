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

import lombok.Getter;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.server.Machine;
import org.machinemc.api.file.ServerProperties;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.Difficulty;
import org.machinemc.api.world.WorldType;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;

/**
 * Default implementation of server properties.
 */
@Getter
public class ServerPropertiesImpl implements ServerProperties {

    public static final String PROPERTIES_FILE_NAME = "server.properties";
    public static final String ICON_FILE_NAME = "icon.png";

    private final Machine server;

    private final String serverIp;
    private final @Range(from = 0, to = 65536) int serverPort;
    private final boolean online;
    private final int maxPlayers;
    private final Component motd;
    private final NamespacedKey defaultWorld;
    private final Difficulty defaultDifficulty;
    private final WorldType defaultWorldType;
    private final boolean reducedDebugScreen;
    private final int viewDistance, simulationDistance, tps, serverResponsiveness;
    private final String serverBrand;
    private final @Nullable BufferedImage icon;
    private final @Nullable String encodedIcon;

    private static final int ICON_SIZE = 64;

    public ServerPropertiesImpl(final Machine server, final File file) throws IOException {
        this.server = server;
        final Properties original = new Properties();

        final InputStream originalInputStream = getOriginal();
        if (originalInputStream == null)
            throw new IllegalStateException("Default server properties file doesn't exist in the server");

        InputStreamReader stream = new InputStreamReader(originalInputStream, StandardCharsets.UTF_8);
        original.load(stream);
        stream.close();

        final Properties properties = new Properties();
        stream = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
        properties.load(stream);
        stream.close();

        for (final Map.Entry<Object, Object> entry : original.entrySet())
            properties.putIfAbsent(entry.getKey(), entry.getValue());

        serverIp = properties.getProperty("server-ip");

        serverPort = Integer.parseInt(properties.getProperty("server-port"));

        online = Boolean.parseBoolean(properties.getProperty("online"));

        maxPlayers = Integer.parseInt(properties.getProperty("max-players"));

        final String motdJson = properties.getProperty("motd");
        motd = motdJson.equals("")
                ? TextComponent.empty()
                : getServer().getComponentSerializer().deserializeJson(motdJson);

        NamespacedKey defaultWorldParsed = null;
        try {
            defaultWorldParsed = NamespacedKey.parse(properties.getProperty("default-world"));
        } catch (Exception ignored) { }
        defaultWorld = defaultWorldParsed != null ? defaultWorldParsed : NamespacedKey.machine("main");

        Difficulty difficulty;
        try {
            difficulty = Difficulty.valueOf(properties.getProperty("default-difficulty").toUpperCase());
        } catch (Exception e) {
            difficulty = Difficulty.DEFAULT_DIFFICULTY;
        }
        defaultDifficulty = difficulty;

        WorldType worldType;
        try {
            worldType = WorldType.valueOf(properties.getProperty("default-world-type").toUpperCase());
        } catch (Exception e) {
            worldType = WorldType.NORMAL;
        }
        defaultWorldType = worldType;

        viewDistance = Integer.parseInt(properties.getProperty("view-distance"));

        simulationDistance = Integer.parseInt(properties.getProperty("simulation-distance"));

        reducedDebugScreen = Boolean.parseBoolean(properties.getProperty("reduced-debug-screen"));

        final int tps = Integer.parseInt(properties.getProperty("tps"));
        this.tps = tps <= 0 ? Machine.DEFAULT_TPS : tps;

        final int response = Integer.parseInt(properties.getProperty("server-responsiveness"));
        serverResponsiveness = Math.max(response, 0);

        serverBrand = properties.getProperty("server-brand");

        final File png = new File(ICON_FILE_NAME);
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

    @Override
    public String getName() {
        return PROPERTIES_FILE_NAME;
    }

    @Override
    public @Nullable InputStream getOriginal() {
        return Machine.CLASS_LOADER.getResourceAsStream(PROPERTIES_FILE_NAME);
    }

}
