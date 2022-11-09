package me.pesekjak.machine.world;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.utils.NamespacedKey;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Manages multiple worlds of the server, each world
 * has to reference manager it was created for.
 */
@RequiredArgsConstructor
public class WorldManager {

    private final Set<World> worlds = new CopyOnWriteArraySet<>();
    @Getter
    private final Machine server;

    /**
     * Registers the world to this manager if it's not registered already in a different one.
     * @param world world that should be registered
     */
    public void addWorld(World world) {
        if(world.getManager().get() != null && world.getManager().get() != this)
            throw new IllegalStateException("World '" + world.getName() + "' is already registered in a different WorldManager");
        world.getManager().set(this);
        worlds.add(world);
    }

    /**
     * Removes the world from the manager if it's registered in this manager.
     * @param world world that should be removed
     * @return true if the world was removed successfully
     */
    public boolean removeWorld(World world) {
        if(world.getManager().get() != this) return false;
        if(worlds.remove(world)) {
            world.getManager().set(null);
            return true;
        }
        return false;
    }

    /**
     * Checks if the world with given name exists.
     * @param name name of the world
     * @return true if the world exists
     */
    public boolean isRegistered(NamespacedKey name) {
        return isRegistered(getWorld(name));
    }

    /**
     * Checks if the world is registered in this manager.
     * @param world world to check for
     * @return true if the world is registered in this manager
     */
    public boolean isRegistered(World world) {
        return worlds.contains(world);
    }

    /**
     * Searches for registered world with the given name in this manager.
     * @param name name of the world to search for
     * @return world with the given name
     */
    public World getWorld(NamespacedKey name) {
        for(World world : getWorlds()) {
            if(!(world.getName().equals(name))) continue;
            return world;
        }
        return null;
    }

    /**
     * Collection of all registered worlds in this manager
     * @return collection of all registered worlds
     */
    public Set<World> getWorlds() {
        return Collections.unmodifiableSet(worlds);
    }

}
