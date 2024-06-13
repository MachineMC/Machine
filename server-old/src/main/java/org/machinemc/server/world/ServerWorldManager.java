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
import org.machinemc.api.Server;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.World;
import org.machinemc.api.world.WorldManager;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Default implementation of the world manager.
 */
public class ServerWorldManager implements WorldManager {

    private final Set<World> worlds = new CopyOnWriteArraySet<>();
    @Getter
    private final Server server;

    public ServerWorldManager(final Server server) {
        this.server = Objects.requireNonNull(server, "Server can not be null");
    }

    @Override
    public void addWorld(final World world) {
        worlds.add(Objects.requireNonNull(world, "World can not be null"));
    }

    @Override
    public boolean removeWorld(final World world) {
        return worlds.remove(Objects.requireNonNull(world, "World can not be null"));
    }

    @Override
    public boolean isRegistered(final NamespacedKey name) {
        Objects.requireNonNull(name, "Name of the world can not be null");
        return getWorld(name).map(this::isRegistered).orElse(false);
    }

    @Override
    public boolean isRegistered(final World world) {
        return worlds.contains(Objects.requireNonNull(world, "World can not be null"));
    }

    @Override
    public Optional<World> getWorld(final NamespacedKey name) {
        Objects.requireNonNull(name, "Name of the world can not be null");
        for (final World world : getWorlds()) {
            if (!(world.getName().equals(name))) continue;
            return Optional.of(world);
        }
        return Optional.empty();
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
