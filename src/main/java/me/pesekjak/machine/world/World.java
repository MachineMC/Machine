package me.pesekjak.machine.world;

import lombok.*;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.entities.Entity;
import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.network.packets.out.PacketPlayOutChangeDifficulty;
import me.pesekjak.machine.network.packets.out.PacketPlayOutWorldSpawnPosition;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.dimensions.DimensionType;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents a playable world.
 */
@Builder
@Getter
public class World {

    @Getter(AccessLevel.PROTECTED)
    protected final AtomicReference<WorldManager> manager = new AtomicReference<>();

    private final NamespacedKey name;
    private final DimensionType dimensionType;
    private final Set<Entity> entityList = new CopyOnWriteArraySet<>();
    private final long seed;
    private Difficulty difficulty;
    private Location worldSpawn;

    /**
     * Creates the default world.
     * @param server server to take default properties from
     * @return newly created and registered world
     */
    public static World createDefault(Machine server) {
        World world = World.builder()
                .name(NamespacedKey.machine("main"))
                .dimensionType(server.getDimensionTypeManager().getDimensions().iterator().next())
                .seed(1)
                .difficulty(server.getProperties().getDefaultDifficulty())
                .build();
        world.setWorldSpawn(new Location(0, 0, 0, world));
        return world;
    }

    /**
     * Changes difficulty of the world
     * @param difficulty new difficulty
     */
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

    /**
     * Changes world spawn of the world
     * @param location new world spawn
     */
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

}
