package me.pesekjak.machine.world;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.utils.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Default implementation of the world manager.
 */
@RequiredArgsConstructor
public class WorldManagerImpl implements WorldManager {

    private final @NotNull Set<World> worlds = new CopyOnWriteArraySet<>();
    @Getter
    private final @NotNull Machine server;

    @Override
    public void addWorld(@NotNull World world) {
        if(world.getManagerReference().get() != null && world.getManagerReference().get() != this)
            throw new IllegalStateException("World '" + world.getName() + "' is already registered in a different WorldManager");
        world.getManagerReference().set(this);
        worlds.add(world);
    }

    @Override
    public boolean removeWorld(@NotNull World world) {
        if(world.getManagerReference().get() != this) return false;
        if(worlds.remove(world)) {
            world.getManagerReference().set(null);
            return true;
        }
        return false;
    }

    @Override
    public boolean isRegistered(@NotNull NamespacedKey name) {
        final World world = getWorld(name);
        if(world == null) return false;
        return isRegistered(world);
    }

    @Override
    public boolean isRegistered(@NotNull World world) {
        return worlds.contains(world);
    }

    @Override
    public World getWorld(@NotNull NamespacedKey name) {
        for(World world : getWorlds()) {
            if(!(world.getName().equals(name))) continue;
            return world;
        }
        return null;
    }

    @Override
    public @NotNull Set<World> getWorlds() {
        return Collections.unmodifiableSet(worlds);
    }

}
