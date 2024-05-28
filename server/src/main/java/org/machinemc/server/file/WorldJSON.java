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
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.Server;
import org.machinemc.api.server.ServerProperty;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.Difficulty;
import org.machinemc.api.world.EntityPosition;
import org.machinemc.api.world.World;
import org.machinemc.api.world.WorldType;
import org.machinemc.api.world.dimensions.DimensionType;
import org.machinemc.cogwheel.TypedClassInitiator;
import org.machinemc.cogwheel.annotations.Key;
import org.machinemc.cogwheel.annotations.Optional;
import org.machinemc.cogwheel.config.ConfigSerializer;
import org.machinemc.cogwheel.config.Configuration;
import org.machinemc.cogwheel.json.JSONConfigSerializer;
import org.machinemc.server.world.ServerWorld;

import java.io.File;
import java.util.Objects;
import java.util.function.Function;

/**
 * Represents a json world file of server world.
 */
@Getter
public class WorldJSON implements ServerProperty, Configuration {

    public static final String WORLD_FILE_NAME = "world.json";

    public static final Function<Server, ConfigSerializer<JsonObject>> CONFIG_SERIALIZER = server -> JSONConfigSerializer.builder()
            .gson(server.getGson())
            .registry(server.getSerializerRegistry())
            .classInitiator(new TypedClassInitiator(Server.class, server))
            .errorHandler((context, error) -> server.getConsole().severe(error.type() + ": " + error.message()))
            .build();

    private final Server server;

    private NamespacedKey name;
    @Key("dimension")
    private NamespacedKey dimensionTypeKey;
    private long seed;
    @Optional
    private @Nullable Difficulty difficulty;
    @Optional
    private @Nullable WorldType worldType;
    @Optional
    private @Nullable EntityPosition worldSpawn;

    private WorldJSON(final Server server) {
        this.server = Objects.requireNonNull(server, "Server can not be null");
        difficulty = getServer().getProperties().getDefaultDifficulty();
        worldType = getServer().getProperties().getDefaultWorldType();
    }

    private WorldJSON(final World world) {
        Objects.requireNonNull(world, "World cannot be null");
        this.server = world.getServer();
        this.name = world.getName();
        this.dimensionTypeKey = world.getDimensionType().getName();
        this.seed = world.getSeed();
        this.difficulty = world.getDifficulty();
        this.worldType = world.getWorldType();
        this.worldSpawn = world.getWorldSpawn();
    }

    /**
     * Saves the world json to the specified file
     * @param file the destination
     */
    public void save(final File file) {
        CONFIG_SERIALIZER.apply(server).save(file, this);
    }

    /**
     * Creates and registers the world to the server's WorldManager.
     * @param folder the directory of the newly created world
     * @return newly created and registered world
     */
    public World buildWorld(final File folder) {
        Objects.requireNonNull(folder, "folder");
        final DimensionType dimensionType = server.getDimensionTypeManager().getDimension(dimensionTypeKey)
                .orElseThrow(() -> new IllegalStateException("World '" + name + "' uses non existing dimension"));
        if (worldSpawn == null)
            worldSpawn = EntityPosition.of(0, dimensionType.getMinY(), 0);
        return new ServerWorld(folder, server, name, dimensionType, worldType, seed, difficulty, worldSpawn);
    }

    /**
     * Constructs a new world json object from a json file
     * @param server server instance
     * @param file world json file
     * @return newly created world json
     */
    public static WorldJSON fromFile(final Server server, final File file) {
        return CONFIG_SERIALIZER.apply(server).load(file, WorldJSON.class);
    }

    /**
     * Constructs a new world json object from an already existing world's properties
     * @param world the world
     * @return newly created world json
     */
    public static WorldJSON fromWorld(final World world) {
        return new WorldJSON(world);
    }
    
}
