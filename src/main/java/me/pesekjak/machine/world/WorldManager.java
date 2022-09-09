package me.pesekjak.machine.world;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.utils.NamespacedKey;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RequiredArgsConstructor
public class WorldManager {

    private final List<World> worlds = new CopyOnWriteArrayList<>();
    @Getter
    private final Machine server;

    public static WorldManager createDefault(Machine server) {
        WorldManager manager = new WorldManager(server);
        manager.addWorld(World.MAIN);
        return manager;
    }

    public void addWorld(World world) {
        if(!worlds.contains(world))
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

    public List<World> getWorlds() {
        return Collections.unmodifiableList(worlds);
    }

}
