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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import org.machinemc.api.Server;
import org.machinemc.api.file.ServerFile;
import org.machinemc.api.server.ServerProperty;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.Difficulty;
import org.machinemc.api.world.Location;
import org.machinemc.api.world.World;
import org.machinemc.api.world.WorldType;
import org.machinemc.api.world.dimensions.DimensionType;
import org.machinemc.server.Machine;
import org.machinemc.server.world.AbstractWorld;
import org.machinemc.server.world.ServerWorld;

import java.io.*;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a json world file of server world.
 */
@Getter
public class WorldJSON implements ServerFile, ServerProperty {

    public static final String WORLD_FILE_NAME = "world.json";

    private final Server server;

    private final NamespacedKey name;
    private final DimensionType dimensionType;
    private final long seed;
    private final Difficulty difficulty;
    private final WorldType worldType;

    private final File folder;

    public WorldJSON(final Server server, final File file) throws IOException {
        this.server = Objects.requireNonNull(server, "Server can not be null");
        Objects.requireNonNull(file, "Source file can not be null");
        folder = file.getParentFile();
        final JsonParser parser = new JsonParser();
        final JsonObject json;
        try (FileReader fileReader = new FileReader(file)) {
            json = parser.parse(fileReader).getAsJsonObject();
        }

        final NamespacedKey name;
        try {
            name = NamespacedKey.parse(json.get("name").getAsString());
        } catch (Exception ignored) {
            throw new IllegalStateException("World '" + file.getParentFile().getName() + "' uses illegal "
                    + "name identifier and can't be registered");
        }
        this.name = name;

        final NamespacedKey dimensionKey;
        try {
            dimensionKey = NamespacedKey.parse(json.get("dimension").getAsString());
        } catch (Exception ignored) {
            throw new IllegalStateException("World '" + file.getParentFile().getName() + "' uses "
                    + "illegal dimension identifier and can't be registered");
        }

        this.dimensionType = server.getDimensionTypeManager().getDimension(dimensionKey)
                .orElseThrow(() -> new IllegalStateException("World '" + this.name + "' uses non existing dimension"));

        long seedValue = 1;
        try {
            seedValue = json.get("seed").getAsNumber().longValue();
        } catch (Exception exception) {
            getServer().getConsole().severe("World '" + this.name + "' has not valid "
                    + "defined seed, defaulting to '1' instead");
        }
        seed = seedValue;

        final Difficulty difficulty = Difficulty.getByName(json.get("difficulty").getAsString()).orElseGet(() -> {
            final Difficulty def = getServer().getProperties().getDefaultDifficulty();
            json.addProperty("difficulty", def.name().toLowerCase());
            return def;
        });

        final WorldType worldType = WorldType.getByName(json.get("worldType").getAsString()).orElseGet(() -> {
            final WorldType def = getServer().getProperties().getDefaultWorldType();
            json.addProperty("worldType", def.name().toLowerCase());
            return def;
        });

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
    public Optional<InputStream> getOriginal() {
        return Optional.ofNullable(Machine.CLASS_LOADER.getResourceAsStream(WORLD_FILE_NAME));
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
        final AbstractWorld world = new ServerWorld(folder, server, name, dimensionType, worldType, seed);
        // TODO should get calculated/from json
        world.setWorldSpawn(new Location(0, dimensionType.getMinY(), 0, world));
        world.setDifficulty(server.getProperties().getDefaultDifficulty());
        return world;
    }

    @Override
    public String toString() {
        return getName() + "(" + name + ')';
    }

}
