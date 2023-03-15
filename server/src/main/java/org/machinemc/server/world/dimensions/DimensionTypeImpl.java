package org.machinemc.server.world.dimensions;

import lombok.*;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.api.utils.NamespacedKey;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.machinemc.api.world.dimensions.DimensionType;
import org.machinemc.nbt.NBTCompound;

import java.util.Map;

import static java.util.Map.entry;

/**
 * Default implementation of the dimension type.
 */
@Builder
@Getter
public class DimensionTypeImpl implements DimensionType {

    private final NamespacedKey name;
    @Builder.Default private final boolean natural = true;
    private final float ambientLight;
    private final boolean ceilingEnabled;
    @Builder.Default private final boolean skylightEnabled = true;
    private final @Nullable Long fixedTime;
    @Builder.Default private final boolean raidCapable = true;
    private final boolean respawnAnchorSafe;
    private final boolean ultrawarm;
    @Builder.Default private final boolean bedSafe = true;
    @Builder.Default private final NamespacedKey effects = NamespacedKey.minecraft("overworld");
    private final boolean piglinSafe;
    @Builder.Default private final @Range(from = -2032, to = 2016) int minY = -64;
    @Builder.Default private final @Range(from = 0, to = 4064) int height = 384;
    @Builder.Default private final @Range(from = 0, to = 4064) int logicalHeight = 384;
    @Builder.Default private final int coordinateScale = 1;
    @Builder.Default private final NamespacedKey infiniburn = NamespacedKey.minecraft("infiniburn_overworld");
    @Builder.Default private final int monsterSpawnBlockLightLimit = 5;
    @Builder.Default private final int monsterSpawnLightLevel = 1;

    /**
     * Creates the default dimension type.
     * @return default dimension type
     */
    public static DimensionType createDefault() {
        return DimensionTypeImpl.builder()
                .name(NamespacedKey.of(NamespacedKey.MINECRAFT_NAMESPACE, "overworld"))
                .build();
    }

    @Override
    public NBTCompound toNBT() {
        NBTCompound element = new NBTCompound(Map.ofEntries(
                entry("ambient_light", ambientLight),
                entry("monster_spawn_block_light_limit", monsterSpawnBlockLightLimit),
                entry("monster_spawn_light_level", monsterSpawnLightLevel),
                entry("infiniburn", "#" + infiniburn),
                entry("natural", (byte) (natural ? 0x01 : 0x00)),
                entry("has_ceiling", (byte) (ceilingEnabled ? 0x01 : 0x00)),
                entry("has_skylight", (byte) (skylightEnabled ? 0x01 : 0x00)),
                entry("ultrawarm", (byte) (ultrawarm ? 0x01 : 0x00)),
                entry("has_raids", (byte) (raidCapable ? 0x01 : 0x00)),
                entry("respawn_anchor_works", (byte) (respawnAnchorSafe ? 0x01 : 0x00)),
                entry("bed_works", (byte) (bedSafe ? 0x01 : 0x00)),
                entry("effects", effects.toString()),
                entry("piglin_safe", (byte) (piglinSafe ? 0x01 : 0x00)),
                entry("min_y", minY),
                entry("height", height),
                entry("logical_height", logicalHeight),
                entry("coordinate_scale", coordinateScale),
                entry("name", name.toString())
        ));
        if (fixedTime != null)
            element.set("fixed_time", fixedTime);
        return element;
    }

}
