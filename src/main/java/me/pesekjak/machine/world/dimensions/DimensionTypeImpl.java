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
 * Default implementation of the dimension type.
 */
@Builder
@Getter
public class DimensionTypeImpl implements DimensionType {

    protected final @NotNull AtomicReference<DimensionTypeManager> managerReference = new AtomicReference<>();
    protected final @NotNull AtomicInteger idReference = new AtomicInteger(-1);

    private final @NotNull NamespacedKey name;
    @Builder.Default private final boolean natural = true;
    private final float ambientLight;
    private final boolean ceilingEnabled;
    @Builder.Default private final boolean skylightEnabled = true;
    private final @Nullable Long fixedTime;
    @Builder.Default private final boolean raidCapable = true;
    private final boolean respawnAnchorSafe;
    private final boolean ultrawarm;
    @Builder.Default private final boolean bedSafe = true;
    @Builder.Default private final @NotNull NamespacedKey effects = NamespacedKey.minecraft("overworld");
    private final boolean piglinSafe;
    @Builder.Default private final @Range(from = -2032, to = 2016) int minY = -64;
    @Builder.Default private final @Range(from = 0, to = 4064) int height = 384;
    @Builder.Default private final @Range(from = 0, to = 4064) int logicalHeight = 384;
    @Builder.Default private final int coordinateScale = 1;
    @Builder.Default private final @NotNull NamespacedKey infiniburn = NamespacedKey.minecraft("infiniburn_overworld");
    @Builder.Default private final int monsterSpawnBlockLightLimit = 5;
    @Builder.Default private final int monsterSpawnLightLevel = 1;

    /**
     * Creates the default dimension type.
     * @return default dimension type
     */
    public static @NotNull DimensionType createDefault() {
        return DimensionTypeImpl.builder()
                .name(new NamespacedKey(NamespacedKey.MINECRAFT_NAMESPACE, "overworld"))
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
