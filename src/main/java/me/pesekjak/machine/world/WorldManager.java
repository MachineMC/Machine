package me.pesekjak.machine.world;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.utils.NamespacedKey;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@RequiredArgsConstructor
public class WorldManager {

    private final Set<World> worlds = new CopyOnWriteArraySet<>();
    @Getter
    private final Machine server;

    public void addWorld(World world) {
        worlds.add(world);
    }

    public boolean removeWorld(World world) {
        return worlds.remove(world);
    }

    public boolean isRegistered(NamespacedKey name) {
        return isRegistered(getWorld(name));
    }

    public boolean isRegistered(World world) {
        return worlds.contains(world);
    }


    public World getWorld(NamespacedKey name) {
        for(World world : getWorlds()) {
            if(!(world.getName().equals(name))) continue;
            return world;
        }
        return null;
    }

    public Set<World> getWorlds() {
        return Collections.unmodifiableSet(worlds);
    }

}
