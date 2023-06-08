/*
 * This file is part of Machine.
 *
 * Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Machine.
 * If not, see https://www.gnu.org/licenses/.
 */
package org.machinemc.server.world;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.machinemc.api.Server;
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
public class ServerWorldManager implements WorldManager {

    private final Set<World> worlds = new CopyOnWriteArraySet<>();
    @Getter
    private final Server server;

    @Override
    public void addWorld(final World world) {
        worlds.add(world);
    }

    @Override
    public boolean removeWorld(final World world) {
        return worlds.remove(world);
    }

    @Override
    public boolean isRegistered(final NamespacedKey name) {
        final World world = getWorld(name);
        if (world == null) return false;
        return isRegistered(world);
    }

    @Override
    public boolean isRegistered(final World world) {
        return worlds.contains(world);
    }

    @Override
    public World getWorld(final NamespacedKey name) {
        for (final World world : getWorlds()) {
            if (!(world.getName().equals(name))) continue;
            return world;
        }
        return null;
    }

    @Override
    public Set<World> getWorlds() {
        return Collections.unmodifiableSet(worlds);
    }

    @Override
    public String toString() {
        return "ServerWorldManager("
                + "server=" + server
                + ')';
    }

}
