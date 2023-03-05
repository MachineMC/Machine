package org.machinemc.server.world;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.machinemc.server.Machine;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.World;
import org.machinemc.api.world.WorldManager;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Default implementation of the world manager.
 */
@RequiredArgsConstructor
public class WorldManagerImpl implements WorldManager {

    private final Set<World> worlds = new CopyOnWriteArraySet<>();
    @Getter
    private final Machine server;

    @Override
    public void addWorld(World world) {
        if(world.getManagerReference().get() != null && world.getManagerReference().get() != this)
            throw new IllegalStateException("World '" + world.getName() + "' is already registered in a different WorldManager");
        world.getManagerReference().set(this);
        worlds.add(world);
    }

    @Override
    public boolean removeWorld(World world) {
        if(world.getManagerReference().get() != this) return false;
        if(worlds.remove(world)) {
            world.getManagerReference().set(null);
            return true;
        }
        return false;
    }

    @Override
    public boolean isRegistered(NamespacedKey name) {
        final World world = getWorld(name);
        if(world == null) return false;
        return isRegistered(world);
    }

    @Override
    public boolean isRegistered(World world) {
        return worlds.contains(world);
    }

    @Override
    public World getWorld(NamespacedKey name) {
        for(World world : getWorlds()) {
            if(!(world.getName().equals(name))) continue;
            return world;
        }
        return null;
    }

    @Override
    public Set<World> getWorlds() {
        return Collections.unmodifiableSet(worlds);
    }

}
