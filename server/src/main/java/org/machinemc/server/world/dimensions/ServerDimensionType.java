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
package org.machinemc.server.world.dimensions;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.dimensions.DimensionType;
import org.machinemc.nbt.NBTCompound;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.Map.entry;

/**
 * Default implementation of the dimension type.
 */
@Getter
@Builder
public class ServerDimensionType implements DimensionType {

    private final NamespacedKey name;
    @Builder.Default private boolean natural = true;
    @Builder.Default private float ambientLight = 0;
    @Builder.Default private boolean ceilingEnabled = false;
    @Builder.Default private boolean skylightEnabled = true;
    @Getter(AccessLevel.NONE)
    @Builder.Default private @Nullable Long fixedTime = null;
    @Builder.Default private boolean raidCapable = true;
    @Builder.Default private boolean respawnAnchorSafe = false;
    @Builder.Default private boolean ultrawarm = false;
    @Builder.Default private boolean bedSafe = true;
    @Builder.Default private NamespacedKey effects = NamespacedKey.minecraft("overworld");
    @Builder.Default private boolean piglinSafe = false;
    @Builder.Default private @Range(from = -2032, to = 2016) int minY = -64;
    @Builder.Default private @Range(from = 0, to = 4064) int height = 384;
    @Builder.Default private @Range(from = 0, to = 4064) int logicalHeight = 384;
    @Builder.Default private int coordinateScale = 1;
    @Builder.Default private NamespacedKey infiniburn = NamespacedKey.minecraft("infiniburn_overworld");
    @Builder.Default private int monsterSpawnBlockLightLimit = 5;
    @Builder.Default private int monsterSpawnLightLevel = 1;

    /**
     * Creates the default dimension type.
     * @return default dimension type
     */
    public static DimensionType createDefault() {
        return ServerDimensionType.builder()
                .name(NamespacedKey.of(NamespacedKey.MINECRAFT_NAMESPACE, "overworld"))
                .build();
    }

    ServerDimensionType(final NamespacedKey name,
                        final boolean natural,
                        final float ambientLight,
                        final boolean ceilingEnabled,
                        final boolean skylightEnabled,
                        final @Nullable Long fixedTime,
                        final boolean raidCapable,
                        final boolean respawnAnchorSafe,
                        final boolean ultrawarm,
                        final boolean bedSafe,
                        final NamespacedKey effects,
                        final boolean piglinSafe,
                        final int minY,
                        final int height,
                        final int logicalHeight,
                        final int coordinateScale,
                        final NamespacedKey infiniburn,
                        final int monsterSpawnBlockLightLimit,
                        final int monsterSpawnLightLevel) {
        this.name = Objects.requireNonNull(name, "Dimension name can not be null");
        this.natural = natural;
        this.ambientLight = ambientLight;
        this.ceilingEnabled = ceilingEnabled;
        this.skylightEnabled = skylightEnabled;
        this.fixedTime = fixedTime;
        this.raidCapable = raidCapable;
        this.respawnAnchorSafe = respawnAnchorSafe;
        this.ultrawarm = ultrawarm;
        this.bedSafe = bedSafe;
        this.effects =  Objects.requireNonNull(effects, "Effects can not be null");
        this.piglinSafe = piglinSafe;
        this.minY = minY;
        this.height = height;
        this.logicalHeight = logicalHeight;
        this.coordinateScale = coordinateScale;
        this.infiniburn = Objects.requireNonNull(infiniburn, "Infiniburn can not be null");
        this.monsterSpawnBlockLightLimit = monsterSpawnBlockLightLimit;
        this.monsterSpawnLightLevel = monsterSpawnLightLevel;
    }

    @Override
    public NBTCompound toNBT() {
        final NBTCompound element = new NBTCompound(Map.ofEntries(
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

    @Override
    public Optional<Long> getFixedTime() {
        return Optional.ofNullable(fixedTime);
    }

    @Override
    public String toString() {
        return "ServerDimensionType("
                + "name=" + name
                + ')';
    }

}
