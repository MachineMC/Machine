package me.pesekjak.machine.file;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.server.ServerProperty;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.*;
import me.pesekjak.machine.world.dimensions.DimensionType;

import java.io.*;
import java.util.UUID;

@Getter
public class WorldJson implements ServerFile, ServerProperty {

    public final static String WORLD_FILE_NAME = "world.json";

    private final Machine server;

    private final NamespacedKey name;
    private final DimensionType dimensionType;
    private final long seed;
    private final Difficulty difficulty;
    private final WorldType worldType;

    private final File folder;

    public WorldJson(Machine server, File file) throws IOException {
        this.server = server;
        folder = file.getParentFile();
        final JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();

        final String unparsedName = json.get("name").getAsString();
        NamespacedKey name;
        try {
            name = NamespacedKey.parse(unparsedName);
        } catch (Exception ignored) {
            throw new IllegalStateException("World '" + file.getParentFile().getName() + "' uses illegal name identifier and can't be registered");
        }

        this.name = name;

        final String unparsedDimensionType = json.get("dimension").getAsString();
        NamespacedKey dimensionKey;
        try {
            dimensionKey = NamespacedKey.parse(unparsedDimensionType);
        } catch (Exception ignored) {
            throw new IllegalStateException("World '" + file.getParentFile().getName() + "' uses illegal dimension identifier and can't be registered");
        }

        dimensionType = server.getDimensionTypeManager().getDimension(dimensionKey);
        if(dimensionType == null) {
            throw new IllegalStateException("World '" + this.name + "' uses non existing dimension");
        }

        long seedValue = 1;
        try {
            seedValue = json.get("seed").getAsNumber().longValue();
        } catch (Exception exception) {
            getServer().getConsole().severe("World '" + this.name + "' has not valid defined seed, defaulting to '1' instead");
        }
        seed = seedValue;

        Difficulty difficulty = Difficulty.getByName(json.get("difficulty").getAsString());
        if (difficulty == null) {
            difficulty = getServer().getProperties().getDefaultDifficulty();
            json.addProperty("difficulty", difficulty.getName());
        }

        WorldType worldType = WorldType.getByName(json.get("worldType").getAsString());
        if (worldType == null) {
            worldType = getServer().getProperties().getDefaultWorldType();
            json.addProperty("worldType", worldType.name().toLowerCase());
        }
        try (Writer writer = new FileWriter(file)) {
            getServer().getGson().toJson(json, writer);
        }
        this.difficulty = difficulty;
        this.worldType = worldType;
    }

    @Override
    public String getName() {
        return WORLD_FILE_NAME;
    }

    @Override
    public InputStream getOriginal() {
        return Machine.CLASS_LOADER.getResourceAsStream(WORLD_FILE_NAME);
    }

    public NamespacedKey getWorldName() {
        return name;
    }

    /**
     * Creates and registers the world to the server's WorldManager.
     * @return newly created and registered world
     */
    public World buildWorld() {
        World world = new ServerWorld(folder, server, name, dimensionType, worldType, seed);
        world.setWorldSpawn(new Location(0, dimensionType.getMinY(), 0, world));
        world.setDifficulty(server.getProperties().getDefaultDifficulty());
        return world;
    }

}
