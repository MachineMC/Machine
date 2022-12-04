package me.pesekjak.machine.world.dimensions;

import lombok.*;
import me.pesekjak.machine.utils.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents a dimension type of a world.
 */
@Builder
@Getter
public class DimensionTypeImpl implements DimensionType {

    protected final AtomicReference<DimensionTypeManager> managerReference = new AtomicReference<>();
    protected final AtomicInteger idReference = new AtomicInteger(-1);

    @NotNull
    private final NamespacedKey name;
    private final boolean natural;
    private final float ambientLight;
    private final boolean ceilingEnabled;
    private final boolean skylightEnabled;
    @Nullable
    private final Long fixedTime;
    private final boolean raidCapable;
    private final boolean respawnAnchorSafe;
    private final boolean ultrawarm;
    private final boolean bedSafe;
    @NotNull
    private final NamespacedKey effects;
    private final boolean piglinSafe;
    @Range(from = -2032, to = 2016)
    private final int minY;
    @Range(from = 0, to = 4064)
    private final int height;
    private final int logicalHeight;
    private final int coordinateScale;
    private final NamespacedKey infiniburn;
    private final int monsterSpawnBlockLightLimit;
    private final int monsterSpawnLightLevel;

    /**
     * Creates the default dimension type.
     * @return default dimension type
     */
    public static DimensionTypeImpl createDefault() {
        return DimensionTypeImpl.builder()
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
                .infiniburn(NamespacedKey.minecraft("infiniburn_overworld"))
                .monsterSpawnBlockLightLimit(5)
                .monsterSpawnLightLevel(1)
                .build();
    }

    @Override
    public @NotNull NBTCompound toNBT() {
        return NBT.Compound(Map.of(
                "name", NBT.String(name.toString()),
                "id", NBT.Int(idReference.intValue()),
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

}
