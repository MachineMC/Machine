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

    public final static String DIMENSIONS_FILE_NAME = "dimensions.json";

    @Getter
    private final Machine server;
    private final Set<DimensionType> dimensions = new LinkedHashSet<>();

    public DimensionsJson(Machine server, File file) throws IOException {
        this.server = server;
        final JsonParser parser = new JsonParser();
        final JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();
        final JsonObject dimensions = json.get("dimensions").getAsJsonObject();

        final DimensionType original = DimensionTypeImpl.createDefault();

        for(Map.Entry<String, JsonElement> dimensionKey : dimensions.entrySet()) {
            final NamespacedKey key;
            try {
                key = NamespacedKey.parse(dimensionKey.getKey());
            } catch (Exception ignored) {
                server.getConsole().severe("Dimension '" + dimensionKey.getKey() + "' uses illegal identifier and can't be registered");
                continue;
            }

            final JsonObject dimension = dimensionKey.getValue().getAsJsonObject();

            Number fixedTime = dimension.get("fixed_time") != null ? dimension.get("fixed_time").getAsNumber() : original.getFixedTime();
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
                    .natural(dimension.get("natural") != null ? dimension.get("natural").getAsBoolean() : original.isNatural())
                    .ambientLight(dimension.get("ambient_light") != null ? dimension.get("ambient_light").getAsNumber().floatValue() : original.getAmbientLight())
                    .ceilingEnabled(dimension.get("has_ceiling") != null ? dimension.get("has_ceiling").getAsBoolean() : original.isCeilingEnabled())
                    .skylightEnabled(dimension.get("has_skylight") != null ? dimension.get("has_skylight").getAsBoolean() : original.isSkylightEnabled())
                    .fixedTime(fixedTime != null ? fixedTime.longValue() : null) // nullable option
                    .raidCapable(dimension.get("has_raids") != null ? dimension.get("has_raids").getAsBoolean() : original.isRaidCapable())
                    .respawnAnchorSafe(dimension.get("respawn_anchor_works") != null ? dimension.get("respawn_anchor_works").getAsBoolean() : original.isRespawnAnchorSafe())
                    .ultrawarm(dimension.get("ultrawarm") != null ? dimension.get("ultrawarm").getAsBoolean() : original.isUltrawarm())
                    .bedSafe(dimension.get("bed_works") != null ? dimension.get("bed_works").getAsBoolean() : original.isBedSafe())
                    .effects(effects)
                    .piglinSafe(dimension.get("piglin_safe") != null ? dimension.get("piglin_safe").getAsBoolean() : original.isPiglinSafe())
                    .minY(dimension.get("min_y") != null ? dimension.get("min_y").getAsNumber().intValue() : original.getMinY())
                    .height(dimension.get("height") != null ? dimension.get("height").getAsNumber().intValue() : original.getHeight())
                    .logicalHeight(dimension.get("logical_height") != null ? dimension.get("logical_height").getAsNumber().intValue() : original.getLogicalHeight())
                    .coordinateScale(dimension.get("coordinate_scale") != null ? dimension.get("coordinate_scale").getAsNumber().intValue() : original.getCoordinateScale())
                    .infiniburn(infiniburn)
                    .monsterSpawnBlockLightLimit(dimension.get("monster_spawn_block_light_limit") != null ? dimension.get("monster_spawn_block_light_limit").getAsNumber().intValue() : original.getMonsterSpawnBlockLightLimit())
                    .monsterSpawnLightLevel(dimension.get("monster_spawn_light_level") != null ? dimension.get("monster_spawn_light_level").getAsNumber().intValue() : original.getMonsterSpawnLightLevel())
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