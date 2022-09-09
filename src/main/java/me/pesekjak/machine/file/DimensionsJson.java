package me.pesekjak.machine.file;

import lombok.Getter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.server.ServerProperty;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.dimensions.DimensionType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class DimensionsJson implements ServerFile, ServerProperty {

    public final static String DIMENSIONS_FILE_NAME = "dimensions.json";

    @Getter
    private final Machine server;
    private final Set<DimensionType> dimensions = new HashSet<>();

    public DimensionsJson(Machine server, File file) throws IOException, ParseException {
        this.server = server;
        final JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(new FileReader(file));
        JSONObject dimensions = (JSONObject) json.get("dimensions");
        for(Object dimensionsKey : dimensions.keySet()) {
            if(!(dimensionsKey instanceof String unparsed)) continue;
            NamespacedKey key;
            try {
                key = NamespacedKey.parse(unparsed);
            } catch (Exception ignored) {
                server.getConsole().severe("Dimension '" + unparsed + "' uses illegal identifier and can't be registered");
                continue;
            }
            JSONObject dimension = (JSONObject) dimensions.get(key.toString());

            Boolean natural = (Boolean) dimension.get("natural");
            Number ambientLight = (Number) dimension.get("ambient_light");
            Boolean ceilingEnabled = (Boolean) dimension.get("has_ceiling");
            Boolean skylightEnabled = (Boolean) dimension.get("has_skylight");
            Number fixedTime = (Number) dimension.get("fixed_time");
            if(fixedTime != null && fixedTime.intValue() == -1) fixedTime = null; // nullable option
            Boolean raidCapable = (Boolean) dimension.get("has_raids");
            Boolean respawnAnchorSafe = (Boolean) dimension.get("respawn_anchor_works");
            Boolean ultrawarm = (Boolean) dimension.get("ultrawarm");
            Boolean bedSafe = (Boolean) dimension.get("bed_works");
            String effects = (String) dimension.get("effects");
            Boolean piglinSafe = (Boolean) dimension.get("piglin_safe");
            Number minY = (Number) dimension.get("min_y");
            Number height = (Number) dimension.get("height");
            Number logicalHeight = (Number) dimension.get("logical_height");
            Number coordinateScale = (Number) dimension.get("coordinate_scale");
            NamespacedKey infiniburn = null;
            try {
                infiniburn = NamespacedKey.minecraft((String) dimension.get("infiniburn"));
            } catch (Exception ignored) { }
            Number monsterSpawnBlockLightLimit = (Number) dimension.get("monster_spawn_block_light_limit");
            Number monsterSpawnLightLevel = (Number) dimension.get("monster_spawn_light_level");

            if(natural == null | ambientLight == null | ceilingEnabled == null |
            skylightEnabled == null | raidCapable == null | respawnAnchorSafe == null |
            ultrawarm == null | bedSafe == null | effects == null | piglinSafe == null |
            minY == null | height == null | logicalHeight == null | coordinateScale == null |
            infiniburn == null | monsterSpawnBlockLightLimit == null | monsterSpawnLightLevel == null) {
                server.getConsole().warning("Dimension '" + key + "' has missing properties, default values will be used instead");
            }

            this.dimensions.add(DimensionType.builder()
                    .name(key)
                    .natural(natural != null ? natural : DimensionType.OVERWORLD.isNatural())
                    .ambientLight(ambientLight != null ? ambientLight.floatValue() : DimensionType.OVERWORLD.getAmbientLight())
                    .ceilingEnabled(ceilingEnabled != null ? ceilingEnabled : DimensionType.OVERWORLD.isCeilingEnabled())
                    .skylightEnabled(skylightEnabled != null ? skylightEnabled : DimensionType.OVERWORLD.isSkylightEnabled())
                    .fixedTime(fixedTime != null ? fixedTime.longValue() : null) // nullable option
                    .raidCapable(raidCapable != null ? raidCapable : DimensionType.OVERWORLD.isRaidCapable())
                    .respawnAnchorSafe(respawnAnchorSafe != null ? respawnAnchorSafe : DimensionType.OVERWORLD.isRespawnAnchorSafe())
                    .ultrawarm(ultrawarm != null ? ultrawarm : DimensionType.OVERWORLD.isUltrawarm())
                    .bedSafe(bedSafe != null ? natural : DimensionType.OVERWORLD.isNatural())
                    .effects(effects != null ? effects : DimensionType.OVERWORLD.getEffects())
                    .piglinSafe(piglinSafe != null ? piglinSafe : DimensionType.OVERWORLD.isPiglinSafe())
                    .minY(minY != null ? minY.intValue() : DimensionType.OVERWORLD.getMinY())
                    .height(height != null ? height.intValue() : DimensionType.OVERWORLD.getHeight())
                    .logicalHeight(logicalHeight != null ? logicalHeight.intValue() : DimensionType.OVERWORLD.getLogicalHeight())
                    .coordinateScale(coordinateScale != null ? coordinateScale.intValue() : DimensionType.OVERWORLD.getCoordinateScale())
                    .infiniburn(infiniburn != null ? infiniburn : DimensionType.OVERWORLD.getInfiniburn())
                    .monsterSpawnBlockLightLimit(monsterSpawnBlockLightLimit != null ? monsterSpawnBlockLightLimit.intValue() : DimensionType.OVERWORLD.getMonsterSpawnBlockLightLimit())
                    .monsterSpawnLightLevel(monsterSpawnLightLevel != null ? monsterSpawnLightLevel.intValue() : DimensionType.OVERWORLD.getMonsterSpawnLightLevel())
                    .build()
            );
        }
    }

    public Set<DimensionType> dimensions() {
        return dimensions;
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
