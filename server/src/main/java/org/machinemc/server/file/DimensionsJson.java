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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import org.machinemc.server.Machine;
import org.machinemc.api.file.ServerFile;
import org.machinemc.api.server.ServerProperty;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.dimensions.DimensionType;
import org.machinemc.server.world.dimensions.DimensionTypeImpl;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents dimensions json server file.
 */
public class DimensionsJson implements ServerFile, ServerProperty {

    public static final String DIMENSIONS_FILE_NAME = "dimensions.json";

    @Getter
    private final Machine server;
    private final Set<DimensionType> dimensions = new LinkedHashSet<>();

    public DimensionsJson(final Machine server, final File file) throws IOException {
        this.server = server;
        final JsonParser parser = new JsonParser();
        final JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();
        final JsonObject dimensions = json.get("dimensions").getAsJsonObject();

        final DimensionType original = DimensionTypeImpl.createDefault();

        for (final Map.Entry<String, JsonElement> dimensionKey : dimensions.entrySet()) {
            final NamespacedKey key;
            try {
                key = NamespacedKey.parse(dimensionKey.getKey());
            } catch (Exception ignored) {
                server.getConsole().severe("Dimension '" + dimensionKey.getKey()
                        + "' uses illegal identifier and can't be registered");
                continue;
            }

            final JsonObject dimension = dimensionKey.getValue().getAsJsonObject();

            Number fixedTime = dimension.has("fixed_time")
                    ? dimension.get("fixed_time").getAsNumber()
                    : original.getFixedTime();
            if (fixedTime != null && fixedTime.intValue() == -1) fixedTime = null; // nullable option

            NamespacedKey effects;
            try {
                effects = NamespacedKey.parse(dimension.get("effects").getAsString());
            } catch (Exception ignored) {
                effects = original.getEffects();
            }

            NamespacedKey infiniburn;
            try {
                infiniburn = NamespacedKey.minecraft(dimension.get("infiniburn").getAsString());
            } catch (Exception ignored) {
                infiniburn = original.getInfiniburn();
            }

            this.dimensions.add(DimensionTypeImpl.builder()
                    .name(key)
                    .natural(dimension.has("natural")
                            ? dimension.get("natural").getAsBoolean()
                            : original.isNatural())
                    .ambientLight(dimension.has("ambient_light")
                            ? dimension.get("ambient_light").getAsFloat()
                            : original.getAmbientLight())
                    .ceilingEnabled(dimension.has("has_ceiling")
                            ? dimension.get("has_ceiling").getAsBoolean()
                            : original.isCeilingEnabled())
                    .skylightEnabled(dimension.has("has_skylight")
                            ? dimension.get("has_skylight").getAsBoolean()
                            : original.isSkylightEnabled())
                    .fixedTime(fixedTime != null ? fixedTime.longValue() : null) // nullable option
                    .raidCapable(dimension.has("has_raids")
                            ? dimension.get("has_raids").getAsBoolean()
                            : original.isRaidCapable())
                    .respawnAnchorSafe(dimension.has("respawn_anchor_works")
                            ? dimension.get("respawn_anchor_works").getAsBoolean()
                            : original.isRespawnAnchorSafe())
                    .ultrawarm(dimension.has("ultrawarm")
                            ? dimension.get("ultrawarm").getAsBoolean()
                            : original.isUltrawarm())
                    .bedSafe(dimension.has("bed_works")
                            ? dimension.get("bed_works").getAsBoolean()
                            : original.isBedSafe())
                    .effects(effects)
                    .piglinSafe(dimension.has("piglin_safe")
                            ? dimension.get("piglin_safe").getAsBoolean()
                            : original.isPiglinSafe())
                    .minY(dimension.has("min_y")
                            ? dimension.get("min_y").getAsInt()
                            : original.getMinY())
                    .height(dimension.has("height")
                            ? dimension.get("height").getAsInt()
                            : original.getHeight())
                    .logicalHeight(dimension.has("logical_height")
                            ? dimension.get("logical_height").getAsInt()
                            : original.getLogicalHeight())
                    .coordinateScale(dimension.has("coordinate_scale")
                            ? dimension.get("coordinate_scale").getAsInt()
                            : original.getCoordinateScale())
                    .infiniburn(infiniburn)
                    .monsterSpawnBlockLightLimit(dimension.has("monster_spawn_block_light_limit")
                                    ? dimension.get("monster_spawn_block_light_limit").getAsInt()
                                    : original.getMonsterSpawnBlockLightLimit())
                    .monsterSpawnLightLevel(dimension.has("monster_spawn_light_level")
                                    ? dimension.get("monster_spawn_light_level").getAsInt()
                                    : original.getMonsterSpawnLightLevel())
                    .build());
        }
    }

    /**
     * @return set of all dimensions in the json file
     */
    public Set<DimensionType> dimensions() {
        return Collections.unmodifiableSet(dimensions);
    }

    @Override
    public String getName() {
        return DIMENSIONS_FILE_NAME;
    }

    @Override
    public @Nullable InputStream getOriginal() {
        return Machine.CLASS_LOADER.getResourceAsStream(DIMENSIONS_FILE_NAME);
    }

}
