package me.pesekjak.machine.file;

import lombok.Getter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.Difficulty;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

public class ServerProperties implements ServerFile {

    public final static String PROPERTIES_FILE_NAME = "server.properties";

    @Getter
    private final String serverIp;
    @Getter
    private final int serverPort;
    @Getter
    private final int maxPlayers;
    @Getter
    private final Component motd;
    @Getter
    private final NamespacedKey defaultWorld;
    @Getter
    private final Difficulty defaultDifficulty;

    public ServerProperties(File file) throws IOException {
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

        maxPlayers = Integer.parseInt(properties.getProperty("max-players"));

        String motdJson = properties.getProperty("motd");
        motd = motdJson.equals("") ? Component.empty() : GsonComponentSerializer.gson().deserialize(motdJson);

        NamespacedKey defaultWorldParsed = null;
        try {
            defaultWorldParsed = NamespacedKey.parse(properties.getProperty("default-world"));
        } catch (Exception ignored) { }
        defaultWorld = defaultWorldParsed;

        Difficulty difficulty = Difficulty.DEFAULT_DIFFICULTY;
        try {
            difficulty = Difficulty.valueOf(properties.getProperty("default-difficulty").toUpperCase());
        } catch (Exception ignore) { }
        defaultDifficulty = difficulty;
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
