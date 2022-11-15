package me.pesekjak.machine.file;

import lombok.Getter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.server.ServerProperty;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.Difficulty;
import me.pesekjak.machine.world.WorldType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;

@Getter
public class ServerProperties implements ServerFile, ServerProperty {

    public static final String PROPERTIES_FILE_NAME = "server.properties";
    public static final String ICON_FILE_NAME = "icon.png";

    private final Machine server;

    private final String serverIp;
    private final int serverPort;
    private final boolean online;
    private final int maxPlayers;
    private final Component motd;
    private final NamespacedKey defaultWorld;
    private final Difficulty defaultDifficulty;
    private final WorldType defaultWorldType;
    private final boolean reducedDebugScreen;
    private final int viewDistance, simulationDistance, tps, serverResponsiveness;
    private final String serverBrand;
    @Nullable
    private final BufferedImage icon;
    @Nullable
    private final String encodedIcon;

    public ServerProperties(Machine server, File file) throws IOException {
        this.server = server;
        final Properties original = new Properties();
        InputStreamReader stream = new InputStreamReader(getOriginal(), StandardCharsets.UTF_8);
        original.load(stream);
        stream.close();

        final Properties properties = new Properties();
        stream = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
        properties.load(stream);
        stream.close();

        for (Map.Entry<Object, Object> entry : original.entrySet())
            properties.putIfAbsent(entry.getKey(), entry.getValue());

        serverIp = properties.getProperty("server-ip");

        serverPort = Integer.parseInt(properties.getProperty("server-port"));

        online = Boolean.parseBoolean(properties.getProperty("online"));

        maxPlayers = Integer.parseInt(properties.getProperty("max-players"));

        String motdJson = properties.getProperty("motd");
        motd = motdJson.equals("") ? Component.empty() : GsonComponentSerializer.gson().deserialize(motdJson);

        NamespacedKey defaultWorldParsed = null;
        try {
            defaultWorldParsed = NamespacedKey.parse(properties.getProperty("default-world"));
        } catch (Exception ignored) { }
        defaultWorld = defaultWorldParsed;

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

        int tps = Integer.parseInt(properties.getProperty("tps"));
        this.tps = tps <= 0 ? Machine.DEFAULT_TPS : tps;

        int response = Integer.parseInt(properties.getProperty("server-responsiveness"));
        serverResponsiveness = Math.max(response, 0);

        serverBrand = properties.getProperty("server-brand");

        File png = new File(ICON_FILE_NAME);
        BufferedImage icon = null;
        String encodedIcon = null;
        if (png.exists()) {
            try {
                BufferedImage image = ImageIO.read(png);
                if (image.getHeight() == 64 && image.getWidth() == 64) {
                    icon = image;
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    try {
                        ImageIO.write(icon, "png", out);
                    } catch (IOException ignored) {
                    }
                    encodedIcon = Base64.getEncoder().encodeToString(out.toByteArray());
                }
                else
                    server.getConsole().severe("Unable to load icon.png, the image is not 64 x 64 in size");
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
    public InputStream getOriginal() {
        return Machine.CLASS_LOADER.getResourceAsStream(PROPERTIES_FILE_NAME);
    }

}
