package me.pesekjak.machine.world;

import lombok.*;
import me.pesekjak.machine.entities.Entity;
import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.network.packets.out.PacketPlayOutChangeDifficulty;
import me.pesekjak.machine.network.packets.out.PacketPlayOutWorldSpawnPosition;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.dimensions.DimensionType;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class World {

    @Getter
    private final WorldManager manager;

    @Getter
    private final NamespacedKey name;
    @Getter
    private final DimensionType dimensionType;
    @Getter
    private final Set<Entity> entityList = new CopyOnWriteArraySet<>();
    @Getter
    private final long seed;
    @Getter
    private Difficulty difficulty;
    @Getter
    private Location worldSpawn;

    public static World createDefault(WorldManager manager) {
        return World.builder(manager)
                .name(NamespacedKey.machine("main"))
                .dimensionType(DimensionType.createDefault(manager.getServer().getDimensionTypeManager()))
                .seed(1)
                .difficulty(Difficulty.DEFAULT_DIFFICULTY)
                .build();
    }

    public static WorldBuilder builder(WorldManager manager) {
        return new WorldBuilder(manager);
    }

    protected World(WorldManager manager, NamespacedKey name, DimensionType dimensionType, long seed, Difficulty difficulty) {
        this.manager = manager;
        this.name = name;
        this.dimensionType = dimensionType;
        this.seed = seed;
        this.difficulty = difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        if(difficulty == null) return;
        this.difficulty = difficulty;
        PacketOut packet = new PacketPlayOutChangeDifficulty(difficulty);
        for (Entity entity : entityList) {
            if (!(entity instanceof Player player))
                continue;
            player.sendPacket(packet);
        }
    }

    public void setWorldSpawn(Location location) {
        if(location == null) return;
        this.worldSpawn = location;
        PacketOut packet = new PacketPlayOutWorldSpawnPosition(location);
        for (Entity entity : entityList) {
            if (!(entity instanceof Player player))
                continue;
            player.sendPacket(packet);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    public static final class WorldBuilder {
        private final WorldManager manager;
        private NamespacedKey name;
        private DimensionType dimensionType;
        private long seed;
        private Difficulty difficulty;
        public WorldBuilder name(NamespacedKey name) {
            this.name = name;
            return this;
        }
        public WorldBuilder dimensionType(DimensionType dimensionType) {
            this.dimensionType = dimensionType;
            return this;
        }
        public WorldBuilder seed(long seed) {
            this.seed = seed;
            return this;
        }
        public WorldBuilder difficulty(Difficulty difficulty) {
            this.difficulty = difficulty;
            return this;
        }
        public World build() {
            return new World(manager, name, dimensionType, seed, difficulty);
        }
    }

}
