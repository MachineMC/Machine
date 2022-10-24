package me.pesekjak.machine.world;

import lombok.Getter;
import me.pesekjak.machine.utils.NBTUtils;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.dimensions.DimensionType;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Represents a world stored in a folder.
 */
public class PersistentWorld extends World {

    public final static String DEFAULT_WORLD_FOLDER = "level";

    @Getter
    private final String folderName;
    @Getter
    private final PlayerDataContainer playerDataContainer;

    private PersistentWorld(String folderName,
            NamespacedKey name,
            UUID uuid,
            DimensionType dimensionType,
            long seed,
            Difficulty difficulty,
            Location worldSpawn) {
        super(name, uuid, dimensionType, seed, difficulty, worldSpawn);
        setWorldSpawn(worldSpawn);
        this.folderName = folderName;
        playerDataContainer = new PlayerDataContainer(this);
    }

    public PersistentWorld(String folderName, World world) {
        this(folderName,
                world.getName(),
                world.getUuid(),
                world.getDimensionType(),
                world.getSeed(),
                world.getDifficulty(),
                new Location(0, 0, 0, world)
        );
    }


}
