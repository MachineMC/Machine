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

import com.google.gson.*;
import lombok.Getter;
import org.machinemc.api.Server;
import org.machinemc.api.server.ServerProperty;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.*;
import org.machinemc.api.world.dimensions.DimensionType;
import org.machinemc.server.world.AbstractWorld;
import org.machinemc.server.world.ServerWorld;

import java.io.*;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a json world file of server world.
 */
@Getter
public class WorldJSON implements ServerProperty {

    public static final String WORLD_FILE_NAME = "world.json";

    private final Server server;

    private final NamespacedKey name;
    private final DimensionType dimensionType;
    private final long seed;
    private final Difficulty difficulty;
    private final WorldType worldType;
    private final EntityPosition worldSpawn;

    private final File folder;

    public WorldJSON(final Server server, final File file) throws IOException {
        this.server = Objects.requireNonNull(server, "Server can not be null");
        folder = Objects.requireNonNull(file, "Source file can not be null").getParentFile();
        final JsonParser parser = new JsonParser();
        final JsonObject json;
        try (FileReader fileReader = new FileReader(file)) {
            json = parser.parse(fileReader).getAsJsonObject();
        }

        name = Optional.ofNullable(json.get("name"))
                .or(() -> {
                    throw new IllegalStateException("World '" + folder.getName() + "' doesn't have a 'name' key.");
                })
                .filter(JsonElement::isJsonPrimitive)
                .map(JsonElement::getAsString)
                .flatMap(NamespacedKey::parseSafe)
                .orElseThrow(() -> new IllegalStateException("World '" + folder.getName() + "' uses "
                        + "illegal name identifier and can't be registered"));

        dimensionType = Optional.ofNullable(json.get("dimension"))
                .or(() -> {
                    throw new IllegalStateException("World '" + name + "' doesn't have a 'dimension' key.");
                })
                .filter(JsonElement::isJsonPrimitive)
                .map(JsonElement::getAsString)
                .flatMap(NamespacedKey::parseSafe)
                .or(() -> {
                    throw new IllegalStateException("World '" + name + "' uses "
                            + "illegal dimension identifier and can't be registered");
                })
                .flatMap(server.getDimensionTypeManager()::getDimension)
                .orElseThrow(() -> new IllegalStateException("World '" + name + "' uses non existing dimension"));

        seed = Optional.ofNullable(json.get("seed"))
                .or(() -> {
                    throw new IllegalStateException("World '" + name + "' doesn't have a 'seed' key.");
                })
                .filter(JsonElement::isJsonPrimitive)
                .map(JsonElement::getAsJsonPrimitive)
                .filter(jsonPrimitive -> jsonPrimitive.isString() || jsonPrimitive.isNumber())
                .map(JsonElement::getAsNumber)
                .map(number -> {
                    try {
                        return number.longValue();
                    } catch (Exception e) {
                        return null;
                    }
                })
                .orElseGet(() -> {
                    getServer().getConsole().severe("World '" + name + "' has an invalid "
                            + "defined seed, defaulting to '1' instead");
                    return 1L;
                });

        final Difficulty defaultDifficulty = getServer().getProperties().getDefaultDifficulty();
        difficulty = Optional.ofNullable(json.get("difficulty"))
                .or(() -> {
                    getServer().getConsole().warning("World '" + name + "' doesn't have a 'difficulty' key, "
                            + "defaulting to '" + defaultDifficulty.name().toLowerCase(Locale.ENGLISH) + "' instead");
                    return Optional.of(new JsonPrimitive(defaultDifficulty.name()));
                })
                .filter(JsonElement::isJsonPrimitive)
                .map(JsonElement::getAsString)
                .flatMap(Difficulty::getByName)
                .orElseGet(() -> {
                    getServer().getConsole().warning("World '" + name + "' has an invalid difficulty, "
                            + "defaulting to '" + defaultDifficulty.name().toLowerCase(Locale.ENGLISH) + "' instead");
                    return defaultDifficulty;
                });

        final WorldType defaultWorldType = getServer().getProperties().getDefaultWorldType();
        worldType = Optional.ofNullable(json.get("worldType"))
                .or(() -> {
                    getServer().getConsole().warning("World '" + name + "' doesn't have a 'worldType' key, "
                            + "defaulting to '" + defaultWorldType.name().toLowerCase(Locale.ENGLISH) + "' instead");
                    return Optional.of(new JsonPrimitive(defaultWorldType.name()));
                })
                .filter(JsonElement::isJsonPrimitive)
                .map(JsonElement::getAsString)
                .flatMap(WorldType::getByName)
                .orElseGet(() -> {
                    getServer().getConsole().warning("World '" + name + "' has an invalid world type, "
                            + "defaulting to '" + defaultWorldType.name().toLowerCase(Locale.ENGLISH) + "' instead");
                    return defaultWorldType;
                });

        worldSpawn = Optional.ofNullable(json.get("worldSpawn"))
                .or(() -> {
                    throw new IllegalStateException("World '" + name + "' doesn't have a 'worldSpawn' key.");
                })
                .filter(JsonElement::isJsonObject)
                .map(JsonElement::getAsJsonObject)
                .filter(spawnJson -> spawnJson.size() == 5)
                .map(spawnJson -> {
                    final Double x = getAsNumber(spawnJson.get("x"))
                            .map(Number::doubleValue)
                            .orElse(null);
                    final Double y = getAsNumber(spawnJson.get("y"))
                            .map(Number::doubleValue)
                            .orElse(null);
                    final Double z = getAsNumber(spawnJson.get("z"))
                            .map(Number::doubleValue)
                            .orElse(null);
                    final Float yaw = getAsNumber(spawnJson.get("yaw"))
                            .map(Number::floatValue)
                            .orElse(null);
                    final Float pitch = getAsNumber(spawnJson.get("pitch"))
                            .map(Number::floatValue)
                            .orElse(null);
                    if (x == null || y == null || z == null || yaw == null || pitch == null)
                        return null;
                    return new EntityPosition(x, y, z, yaw, pitch);
                })
                .orElseThrow(() -> new IllegalStateException("World '" + name + "' has an invalid world spawn"));
    }

    private Optional<Number> getAsNumber(final JsonElement jsonElement) {
        return Optional.of(jsonElement)
                .filter(JsonElement::isJsonPrimitive)
                .map(JsonElement::getAsJsonPrimitive)
                .filter(JsonPrimitive::isNumber)
                .map(JsonPrimitive::getAsNumber);
    }

    /**
     * Creates and registers the world to the server's WorldManager.
     * @return newly created and registered world
     */
    public World buildWorld() {
        final AbstractWorld world = new ServerWorld(folder, server, name, dimensionType, worldType, seed, difficulty);
        world.setWorldSpawn(new EntityPosition(
                worldSpawn.getX(),
                worldSpawn.getY(),
                worldSpawn.getZ(),
                worldSpawn.getYaw(),
                worldSpawn.getPitch()
        ));
        return world;
    }

    @Override
    public String toString() {
        return WORLD_FILE_NAME + "(" + name + ')';
    }

}
