package me.pesekjak.machine.file;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.server.ServerProperty;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.dimensions.DimensionType;

import java.io.*;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class DimensionsJson implements ServerFile, ServerProperty {

    public final static String DIMENSIONS_FILE_NAME = "dimensions.json";

    @Getter
    private final Machine server;
    private final Set<DimensionType> dimensions = new LinkedHashSet<>();

    private static final boolean defaultNatural = true;
    private static final float defaultAmbientLight = 0;
    private static final boolean defaultCeilingEnabled = false;
    private static final boolean defaultSkylightEnabled = true;
    private static final Long defaultFixedTime = null;
    private static final boolean defaultRaidCapable = true;
    private static final boolean defaultRespawnAnchorSafe = false;
    private static final boolean defaultUltrawarm = false;
    private static final boolean defaultBedSafe = true;
    private static final NamespacedKey defaultEffects = NamespacedKey.minecraft("overworld");
    private static final boolean defaultPiglinSafe = false;
    private static final int defaultMinY = -64;
    private static final int defaultHeight = 384;
    private static final int defaultLogicalHeight = 384;
    private static final int defaultCoordinateScale = 1;
    private static final NamespacedKey defaultInfiniburn = NamespacedKey.minecraft("infiniburn_overworld");
    private static final int defaultMonsterSpawnBlockLightLimit = 5;
    private static final int defaultMonsterSpawnLightLevel = 1;

    public DimensionsJson(Machine server, File file) throws IOException {
        this.server = server;
        final JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();
        JsonObject dimensions = json.get("dimensions").getAsJsonObject();
        for(Map.Entry<String, JsonElement> dimensionKey : dimensions.entrySet()) {
            String unparsed = dimensionKey.getKey();
            NamespacedKey key;
            try {
                key = NamespacedKey.parse(unparsed);
            } catch (Exception ignored) {
                server.getConsole().severe("Dimension '" + unparsed + "' uses illegal identifier and can't be registered");
                continue;
            }
            JsonObject dimension = dimensionKey.getValue().getAsJsonObject();
            try {
                boolean natural = dimension.get("natural") != null ? dimension.get("natural").getAsBoolean() : defaultNatural;
                float ambientLight = dimension.get("ambient_light") != null ? dimension.get("ambient_light").getAsNumber().floatValue() : defaultAmbientLight;
                boolean ceilingEnabled = dimension.get("has_ceiling") != null ? dimension.get("has_ceiling").getAsBoolean() : defaultCeilingEnabled;
                boolean skylightEnabled = dimension.get("has_skylight") != null ? dimension.get("has_skylight").getAsBoolean() : defaultSkylightEnabled;
                Number fixedTime = dimension.get("fixed_time") != null ? dimension.get("fixed_time").getAsNumber() : defaultFixedTime;
                if (fixedTime != null && fixedTime.intValue() == -1) fixedTime = null; // nullable option
                boolean raidCapable = dimension.get("has_raids") != null ? dimension.get("has_raids").getAsBoolean() : defaultRaidCapable;
                boolean respawnAnchorSafe = dimension.get("respawn_anchor_works") != null ? dimension.get("respawn_anchor_works").getAsBoolean() : defaultRespawnAnchorSafe;
                boolean ultrawarm = dimension.get("ultrawarm") != null ? dimension.get("ultrawarm").getAsBoolean() : defaultUltrawarm;
                boolean bedSafe = dimension.get("bed_works") != null ? dimension.get("bed_works").getAsBoolean() : defaultBedSafe;
                NamespacedKey effects;
                try {
                    effects = NamespacedKey.parse(dimension.get("effects").getAsString());
                } catch (Exception ignored) {
                    effects = defaultEffects;
                }
                boolean piglinSafe = dimension.get("piglin_safe") != null ? dimension.get("piglin_safe").getAsBoolean() : defaultPiglinSafe;
                int minY = dimension.get("min_y") != null ? dimension.get("min_y").getAsNumber().intValue() : defaultMinY;
                int height = dimension.get("height") != null ? dimension.get("height").getAsNumber().intValue() : defaultHeight;
                int logicalHeight = dimension.get("logical_height") != null ? dimension.get("logical_height").getAsNumber().intValue() : defaultLogicalHeight;
                int coordinateScale = dimension.get("coordinate_scale") != null ? dimension.get("coordinate_scale").getAsNumber().intValue() : defaultCoordinateScale;
                NamespacedKey infiniburn;
                try {
                    infiniburn = NamespacedKey.minecraft(dimension.get("infiniburn").getAsString());
                } catch (Exception ignored) {
                    infiniburn = defaultInfiniburn;
                }
                int monsterSpawnBlockLightLimit = dimension.get("monster_spawn_block_light_limit") != null ? dimension.get("monster_spawn_block_light_limit").getAsNumber().intValue() : defaultMonsterSpawnBlockLightLimit;
                int monsterSpawnLightLevel = dimension.get("monster_spawn_light_level") != null ? dimension.get("monster_spawn_light_level").getAsNumber().intValue() : defaultMonsterSpawnLightLevel;

                this.dimensions.add(DimensionType.builder()
                        .name(key)
                        .natural(natural)
                        .ambientLight(ambientLight)
                        .ceilingEnabled(ceilingEnabled)
                        .skylightEnabled(skylightEnabled)
                        .fixedTime(fixedTime != null ? fixedTime.longValue() : null) // nullable option
                        .raidCapable(raidCapable)
                        .respawnAnchorSafe(respawnAnchorSafe)
                        .ultrawarm(ultrawarm)
                        .bedSafe(bedSafe)
                        .effects(effects)
                        .piglinSafe(piglinSafe)
                        .minY(minY)
                        .height(height)
                        .logicalHeight(logicalHeight)
                        .coordinateScale(coordinateScale)
                        .infiniburn(infiniburn)
                        .monsterSpawnBlockLightLimit(monsterSpawnBlockLightLimit)
                        .monsterSpawnLightLevel(monsterSpawnLightLevel)
                        .build()
                );
            } catch (Exception exception) {
                exception.printStackTrace();
                server.getConsole().severe("Failed to register '" + key + "' dimension");
            }
        }
    }

    public Set<DimensionType> dimensions() {
        return Collections.unmodifiableSet(dimensions);
    }

    @Override
    public String getName() {
        return DIMENSIONS_FILE_NAME;
    }

    @Override
    public InputStream getOriginal() {
        return Machine.CLASS_LOADER.getResourceAsStream(DIMENSIONS_FILE_NAME);
    }

}
