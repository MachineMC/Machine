package me.pesekjak.machine.world;

import lombok.Getter;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.dimensions.DimensionType;

public class PersistentWorld extends World {

    public final static String DEFAULT_WORLD_FOLDER = "level";

    @Getter
    private final String folderName;

    private PersistentWorld(String folderName,
            NamespacedKey name,
            DimensionType dimensionType,
            long seed) {
        super(name, dimensionType, seed);
        this.folderName = folderName;
    }

    public PersistentWorld(String folderName, World world) {
        this(folderName,
                world.getName(),
                world.getDimensionType(),
                world.getSeed()
        );
    }

}
