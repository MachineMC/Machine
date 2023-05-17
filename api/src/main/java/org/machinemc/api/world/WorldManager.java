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
package org.machinemc.api.world;

import org.machinemc.api.server.ServerProperty;
import org.machinemc.api.utils.NamespacedKey;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

/**
 * Manager for worlds.
 */
public interface WorldManager extends ServerProperty {

    /**
     * Registers new world to the manager if it's not already registered
     * in a different one.
     * @param world world to register
     */
    void addWorld(World world);

    /**
     * Removes the world from the manager if it's registered in this manager.
     * @param world world to remove
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
    default boolean isRegistered(World world) {
        return isRegistered(world.getName());
    }

    /**
     * Returns world with the given name registered in this manager.
     * @param name name of the world
     * @return world with given name in this manager
     */
    @Nullable World getWorld(NamespacedKey name);

    /**
     * @return unmodifiable set of all worlds registered in this manager
     */
    @Unmodifiable Set<World> getWorlds();

}
