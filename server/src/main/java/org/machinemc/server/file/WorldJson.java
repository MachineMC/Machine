package org.machinemc.server.file;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import org.machinemc.api.world.World;
import org.machinemc.server.Machine;
import org.machinemc.api.file.ServerFile;
import org.machinemc.api.server.ServerProperty;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.server.world.*;
import org.machinemc.api.world.Difficulty;
import org.machinemc.api.world.Location;
import org.machinemc.api.world.WorldType;
import org.machinemc.api.world.dimensions.DimensionType;
import org.jetbrains.annotations.Nullable;

import java.io.*;

/**
 * Represents a json world file of server world.
 */
@Getter
public class WorldJson implements ServerFile, ServerProperty {

    public static final String WORLD_FILE_NAME = "world.json";

    private final Machine server;

    private final NamespacedKey name;
    private final DimensionType dimensionType;
    private final long seed;
    private final Difficulty difficulty;
    private final WorldType worldType;

    private final File folder;

    public WorldJson(final Machine server, final File file) throws IOException {
        this.server = server;
        folder = file.getParentFile();
        final JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();

        NamespacedKey name;
        try {
            name = NamespacedKey.parse(json.get("name").getAsString());
        } catch (Exception ignored) {
            throw new IllegalStateException("World '" + file.getParentFile().getName() + "' uses illegal "
                    + "name identifier and can't be registered");
        }
        this.name = name;

        NamespacedKey dimensionKey;
        try {
            dimensionKey = NamespacedKey.parse(json.get("dimension").getAsString());
        } catch (Exception ignored) {
            throw new IllegalStateException("World '" + file.getParentFile().getName() + "' uses "
                    + "illegal dimension identifier and can't be registered");
        }

        DimensionType dimensionType = server.getDimensionTypeManager().getDimension(dimensionKey);
        if (dimensionType == null)
            throw new IllegalStateException("World '" + this.name + "' uses non existing dimension");
        this.dimensionType = dimensionType;

        long seedValue = 1;
        try {
            seedValue = json.get("seed").getAsNumber().longValue();
        } catch (Exception exception) {
            getServer().getConsole().severe("World '" + this.name + "' has not valid "
                    + "defined seed, defaulting to '1' instead");
        }
        seed = seedValue;

        Difficulty difficulty = Difficulty.getByName(json.get("difficulty").getAsString());
        if (difficulty == null) {
            difficulty = getServer().getProperties().getDefaultDifficulty();
            json.addProperty("difficulty", difficulty.name().toLowerCase());
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
    public @Nullable InputStream getOriginal() {
        return Machine.CLASS_LOADER.getResourceAsStream(WORLD_FILE_NAME);
    }

    /**
     * @return name of the world
     */
    public NamespacedKey getWorldName() {
        return name;
    }

    /**
     * Creates and registers the world to the server's WorldManager.
     * @return newly created and registered world
     */
    public World buildWorld() {
        AbstractWorld world = new ServerWorld(folder, server, name, dimensionType, worldType, seed);
        world.setWorldSpawn(new Location(0, dimensionType.getMinY(), 0, world));
        world.setDifficulty(server.getProperties().getDefaultDifficulty());
        return world;
    }

}
