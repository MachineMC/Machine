package me.pesekjak.machine.world.dimensions;

import lombok.Builder;
import lombok.Getter;
import me.pesekjak.machine.server.NBTSerializable;
import me.pesekjak.machine.utils.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Builder
public class DimensionType implements NBTSerializable {

    private static final AtomicInteger ID_COUNTER = new AtomicInteger(0);

    public static final DimensionType OVERWORLD = DimensionType.builder()
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
            .effects("minecraft:overworld")
            .piglinSafe(false)
            .minY(-64)
            .height(384)
            .logicalHeight(384)
            .coordinateScale(1)
            .infiniburn(new NamespacedKey(NamespacedKey.MINECRAFT_NAMESPACE, "infiniburn_overworld"))
            .monsterSpawnBlockLightLimit(5)
            .monsterSpawnLightLevel(1)
            .build();

    @Getter
    private final int id = ID_COUNTER.getAndIncrement();

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
    private final String effects;
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
                    element.setString("effects", effects);
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
