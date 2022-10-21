package me.pesekjak.machine.world;

import lombok.Getter;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.dimensions.DimensionType;

public class PersistentWorld extends World {

    public final static String DEFAULT_WORLD_FOLDER = "level";

    @Getter
    private final String folderName;

    private PersistentWorld(WorldManager manager,
            String folderName,
            NamespacedKey name,
            DimensionType dimensionType,
            long seed,
            Difficulty difficulty,
            Location worldSpawn) {
        super(manager, name, dimensionType, seed, difficulty);
        super.setWorldSpawn(worldSpawn);
        this.folderName = folderName;
    }

    public PersistentWorld(WorldManager manager, String folderName, World world) {
        this(manager,
                folderName,
                world.getName(),
                world.getDimensionType(),
                world.getSeed(),
                world.getDifficulty(),
                world.getWorldSpawn()
        );
    }

}
