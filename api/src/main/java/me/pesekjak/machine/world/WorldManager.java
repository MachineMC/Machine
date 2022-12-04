package me.pesekjak.machine.world;

import me.pesekjak.machine.server.ServerProperty;
import me.pesekjak.machine.utils.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

/**
 * Manager for worlds.
 */
public interface WorldManager extends ServerProperty {

    /**
     * Registers new world to the manager.
     * @param world world to register
     */
    void addWorld(@NotNull World world);

    /**
     * Unregisters a world from the manager.
     * @param world world to unregister
     * @return if the world was successfully removed
     */
    boolean removeWorld(World world);

    /**
     * Checks if world with given name is registered in
     * the manager.
     * @param name name of the world
     * @return if the world with given name is registered in this manager
     */
    boolean isRegistered(NamespacedKey name);

    /**
     * Checks if the world is registered in this manager.
     * @param world world to check
     * @return if the world is registered in this manager
     */
    default boolean isRegistered(@NotNull World world) {
        return this.equals(world.getManager());
    }

    /**
     * Returns world with the given name registered in this manager.
     * @param name name of the world
     * @return world with given name in this manager
     */
    @Nullable World getWorld(@NotNull NamespacedKey name);

    /**
     * @return unmodifiable set of all worlds registered in this manager
     */
    @Unmodifiable @NotNull Set<World> getWorlds();

}
