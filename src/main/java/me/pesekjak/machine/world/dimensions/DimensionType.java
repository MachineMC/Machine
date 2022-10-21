package me.pesekjak.machine.world.dimensions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.server.NBTSerializable;
import me.pesekjak.machine.utils.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.Map;

@SuppressWarnings("ClassCanBeRecord")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class DimensionType implements NBTSerializable {

    @Getter
    private final int id;
    @Getter @NotNull
    private final NamespacedKey name;
    @Getter
    private final boolean natural;
    @Getter
    private final float ambientLight;
    @Getter
    private final boolean ceilingEnabled;
    @Getter
    private final boolean skylightEnabled;
    @Getter @Nullable
    private final Long fixedTime;
    @Getter
    private final boolean raidCapable;
    @Getter
    private final boolean respawnAnchorSafe;
    @Getter
    private final boolean ultrawarm;
    @Getter
    private final boolean bedSafe;
    @Getter @NotNull
    private final NamespacedKey effects;
    @Getter
    private final boolean piglinSafe;
    @Getter
    private final int minY;
    @Getter
    private final int height;
    @Getter
    private final int logicalHeight;
    @Getter
    private final int coordinateScale;
    @Getter
    private final NamespacedKey infiniburn;
    @Getter
    private final int monsterSpawnBlockLightLimit;
    @Getter
    private final int monsterSpawnLightLevel;

    public static DimensionType createDefault(DimensionTypeManager manager) {
        return DimensionType.builder(manager)
                .name(new NamespacedKey(NamespacedKey.MINECRAFT_NAMESPACE, "overworld"))
                .natural(true)
                .ambientLight(0)
                .ceilingEnabled(false)
                .skylightEnabled(true)
                .fixedTime(null)
                .raidCapable(true)
                .respawnAnchorSafe(false)
                .ultrawarm(false)
                .bedSafe(true)
                .effects(NamespacedKey.minecraft("overworld"))
                .piglinSafe(false)
                .minY(-64)
                .height(384)
                .logicalHeight(384)
                .coordinateScale(1)
                .infiniburn(new NamespacedKey(NamespacedKey.MINECRAFT_NAMESPACE, "infiniburn_overworld"))
                .monsterSpawnBlockLightLimit(5)
                .monsterSpawnLightLevel(1)
                .build();
    }

    public static DimensionTypeBuilder builder(DimensionTypeManager manager) {
        return new DimensionTypeBuilder(manager);
    }

    @Override
    public NBTCompound toNBT() {
        return NBT.Compound(Map.of(
                "name", NBT.String(name.toString()),
                "id", NBT.Int(id),
                "element", NBT.Compound(element -> {
                    element.setFloat("ambient_light", ambientLight);
                    element.setInt("monster_spawn_block_light_limit", monsterSpawnBlockLightLimit);
                    element.setInt("monster_spawn_light_level", monsterSpawnLightLevel);
                    element.setString("infiniburn", "#" + infiniburn.toString());
                    element.setByte("natural", (byte) (natural ? 0x01 : 0x00));
                    element.setByte("has_ceiling", (byte) (ceilingEnabled ? 0x01 : 0x00));
                    element.setByte("has_skylight", (byte) (skylightEnabled ? 0x01 : 0x00));
                    element.setByte("ultrawarm", (byte) (ultrawarm ? 0x01 : 0x00));
                    element.setByte("has_raids", (byte) (raidCapable ? 0x01 : 0x00));
                    element.setByte("respawn_anchor_works", (byte) (respawnAnchorSafe ? 0x01 : 0x00));
                    element.setByte("bed_works", (byte) (bedSafe ? 0x01 : 0x00));
                    element.setString("effects", effects.toString());
                    element.setByte("piglin_safe", (byte) (piglinSafe ? 0x01 : 0x00));
                    element.setInt("min_y", minY);
                    element.setInt("height", height);
                    element.setInt("logical_height", logicalHeight);
                    element.setInt("coordinate_scale", coordinateScale);
                    element.setString("name", name.toString());
                    if (fixedTime != null) element.setLong("fixed_time", fixedTime);
                })
        ));
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    public static final class DimensionTypeBuilder {
        private final DimensionTypeManager manager;
        private NamespacedKey name;
        private boolean natural;
        private float ambientLight;
        private boolean ceilingEnabled;
        private boolean skylightEnabled;
        private Long fixedTime;
        private boolean raidCapable;
        private boolean respawnAnchorSafe;
        private boolean ultrawarm;
        private boolean bedSafe;
        private NamespacedKey effects;
        private boolean piglinSafe;
        private int minY;
        private int height;
        private int logicalHeight;
        private int coordinateScale;
        private NamespacedKey infiniburn;
        private int monsterSpawnBlockLightLimit;
        private int monsterSpawnLightLevel;
        public DimensionTypeBuilder name(NamespacedKey name) {
            this.name = name;
            return this;
        }
        public DimensionTypeBuilder natural(boolean natural) {
            this.natural = natural;
            return this;
        }
        public DimensionTypeBuilder ambientLight(float ambientLight) {
            this.ambientLight = ambientLight;
            return this;
        }
        public DimensionTypeBuilder ceilingEnabled(boolean ceilingEnabled) {
            this.ceilingEnabled = ceilingEnabled;
            return this;
        }
        public DimensionTypeBuilder skylightEnabled(boolean skylightEnabled) {
            this.skylightEnabled = skylightEnabled;
            return this;
        }
        public DimensionTypeBuilder fixedTime(Long fixedTime) {
            this.fixedTime = fixedTime;
            return this;
        }
        public DimensionTypeBuilder raidCapable(boolean raidCapable) {
            this.raidCapable = raidCapable;
            return this;
        }
        public DimensionTypeBuilder respawnAnchorSafe(boolean respawnAnchorSafe) {
            this.respawnAnchorSafe = respawnAnchorSafe;
            return this;
        }
        public DimensionTypeBuilder ultrawarm(boolean ultrawarm) {
            this.ultrawarm = ultrawarm;
            return this;
        }
        public DimensionTypeBuilder bedSafe(boolean bedSafe) {
            this.bedSafe = bedSafe;
            return this;
        }
        public DimensionTypeBuilder effects(NamespacedKey effects) {
            this.effects = effects;
            return this;
        }
        public DimensionTypeBuilder piglinSafe(boolean piglinSafe) {
            this.piglinSafe = piglinSafe;
            return this;
        }
        public DimensionTypeBuilder minY(int minY) {
            this.minY = minY;
            return this;
        }
        public DimensionTypeBuilder height(int height) {
            this.height = height;
            return this;
        }
        public DimensionTypeBuilder logicalHeight(int logicalHeight) {
            this.logicalHeight = logicalHeight;
            return this;
        }
        public DimensionTypeBuilder coordinateScale(int coordinateScale) {
            this.coordinateScale = coordinateScale;
            return this;
        }
        public DimensionTypeBuilder infiniburn(NamespacedKey infiniburn) {
            this.infiniburn = infiniburn;
            return this;
        }
        public DimensionTypeBuilder monsterSpawnBlockLightLimit(int monsterSpawnBlockLightLimit) {
            this.monsterSpawnBlockLightLimit = monsterSpawnBlockLightLimit;
            return this;
        }
        public DimensionTypeBuilder monsterSpawnLightLevel(int monsterSpawnLightLevel) {
            this.monsterSpawnLightLevel = monsterSpawnLightLevel;
            return this;
        }
        public DimensionType build() {
            return new DimensionType(
                    manager.ID_COUNTER.getAndIncrement(),
                    name,
                    natural,
                    ambientLight,
                    ceilingEnabled,
                    skylightEnabled,
                    fixedTime,
                    raidCapable,
                    respawnAnchorSafe,
                    ultrawarm,
                    bedSafe,
                    effects,
                    piglinSafe,
                    minY,
                    height,
                    logicalHeight,
                    coordinateScale,
                    infiniburn,
                    monsterSpawnBlockLightLimit,
                    monsterSpawnLightLevel
            );
        }
    }

}
