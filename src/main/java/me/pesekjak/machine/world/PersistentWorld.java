package me.pesekjak.machine.world;

import lombok.Getter;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.dimensions.DimensionType;

/**
 * Represents a world stored in a folder.
 */
public class PersistentWorld extends World {

    public final static String DEFAULT_WORLD_FOLDER = "level";

    @Getter
    private final String folderName;

    private PersistentWorld(String folderName,
            NamespacedKey name,
            DimensionType dimensionType,
            long seed,
            Difficulty difficulty,
            Location worldSpawn) {
        super(name, dimensionType, seed, difficulty, worldSpawn);
        super.setWorldSpawn(worldSpawn);
        this.folderName = folderName;
    }

    public PersistentWorld(String folderName, World world) {
        this(folderName,
                world.getName(),
                world.getDimensionType(),
                world.getSeed(),
                world.getDifficulty(),
                world.getWorldSpawn()
        );
    }

}
