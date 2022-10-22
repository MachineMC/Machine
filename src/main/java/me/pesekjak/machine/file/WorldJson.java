package me.pesekjak.machine.file;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.server.ServerProperty;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.Difficulty;
import me.pesekjak.machine.world.World;
import me.pesekjak.machine.world.dimensions.DimensionType;

import java.io.*;
import java.util.UUID;

public class WorldJson implements ServerFile, ServerProperty {

    public final static String WORLD_FILE_NAME = "world.json";

    @Getter
    private final Machine server;
    @Getter
    private final NamespacedKey worldName;
    @Getter
    private final DimensionType dimension;
    @Getter
    private final long seed;
    @Getter
    private final Difficulty difficulty;

    public WorldJson(Machine server, File file) throws IOException {
        this.server = server;
        final JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();

        final String unparsedName = json.get("name").getAsString();
        NamespacedKey name;
        try {
            name = NamespacedKey.parse(unparsedName);
        } catch (Exception ignored) {
            server.getConsole().severe("World '" + file.getParentFile().getName() + "' uses illegal name identifier and can't be registered");
            throw new IllegalStateException("World '" + file.getParentFile().getName() + "' uses illegal name identifier and can't be registered");
        }

        worldName = name;

        final String unparsedDimensionType = json.get("dimension").getAsString();
        NamespacedKey dimensionKey;
        try {
            dimensionKey = NamespacedKey.parse(unparsedDimensionType);
        } catch (Exception ignored) {
            server.getConsole().severe("World '" + file.getParentFile().getName() + "' uses illegal dimension identifier and can't be registered");
            throw new IllegalStateException("World '" + file.getParentFile().getName() + "' uses illegal dimension identifier and can't be registered");
        }

        dimension = server.getDimensionTypeManager().getDimension(dimensionKey);
        if(dimension == null) {
            getServer().getConsole().severe("World '" + worldName + "' uses non existing dimension");
            throw new IllegalStateException("World '" + worldName + "' uses non existing dimension");
        }

        long seedValue = 1;
        try {
            seedValue = json.get("seed").getAsNumber().longValue();
        } catch (Exception exception) {
            getServer().getConsole().severe("World '" + worldName + "' has not valid defined seed, defaulting to '1' instead");
        }
        seed = seedValue;

        Difficulty difficulty = Difficulty.getByName(json.get("difficulty").getAsString());
        if (difficulty == null) {
            difficulty = getServer().getProperties().getDefaultDifficulty();
            json.addProperty("difficulty", difficulty.getName());
        }
        Writer writer = new FileWriter(file);
        getServer().getGson().toJson(json, writer);
        writer.close();
        this.difficulty = difficulty;
    }

    @Override
    public String getName() {
        return WORLD_FILE_NAME;
    }

    @Override
    public InputStream getOriginal() {
        return Machine.CLASS_LOADER.getResourceAsStream(WORLD_FILE_NAME);
    }

    public World world(UUID uuid) {
        return World.builder(server.getWorldManager())
                .name(worldName)
                .uuid(uuid)
                .dimensionType(dimension)
                .seed(seed)
                .difficulty(difficulty)
                .build();
    }

}
