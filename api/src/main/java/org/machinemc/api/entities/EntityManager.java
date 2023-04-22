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
 * If not, see <https://www.gnu.org/licenses/>.
 */
package org.machinemc.api.entities;

import org.machinemc.api.server.ServerProperty;
import org.machinemc.api.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Manager of server entities in mutiple worlds.
 */
public interface EntityManager extends ServerProperty {

    /**
     * Returns set of entities of certain entity type.
     * @param entityType entity type of the entities
     * @return entities of the given type
     */
    @Unmodifiable Set<Entity> getEntitiesOfType(EntityType entityType);

    /**
     * Returns set of entities of certain entity type in given world.
     * @param entityType entity type of the entities
     * @param world world to search in
     * @return entities of the given type in given world
     */
    @Unmodifiable Set<Entity> getEntitiesOfType(EntityType entityType, World world);

    /**
     * Returns set of entities of certain class.
     * @param entityClass entity class
     * @param <E> entity class
     * @return entities of the given class
     */
    <E extends Entity> @Unmodifiable Set<E> getEntitiesOfClass(Class<E> entityClass);

    /**
     * Returns set of entities of certain class in given world.
     * @param entityClass entity class
     * @param world world to search in
     * @param <E> entity class
     * @return entities of the given class in given world
     */
    <E extends Entity> @Unmodifiable Set<E> getEntitiesOfClass(Class<E> entityClass, World world);

    /**
     * @return all entities managed by this manager
     */
    @Unmodifiable Set<Entity> getEntities();

    /**
     * Returns entities managed by this manager in given world.
     * @param world world to search in
     * @return all entities in given world
     */
    @Unmodifiable Set<Entity> getEntities(World world);

    /**
     * @param predicate predicate function
     * @return entities managed by this manager where the test was successful
     */
    @Unmodifiable Set<Entity> getEntities(Predicate<Entity> predicate);

    /**
     * Searches for entity with given uuid.
     * @param uuid uuid of the entity
     * @return entity with given uuid
     */
    @Nullable Entity getEntity(UUID uuid);

    /**
     * Adds entity into the manager.
     * @see World#spawn(Entity, org.machinemc.api.world.Location)
     * @param entity entity to add
     */
    @ApiStatus.Internal
    void addEntity(Entity entity);

    /**
     * Removes an entity from the manager.
     * @see Entity#remove()
     * @param entity entity to remove
     */
    @ApiStatus.Internal
    void removeEntity(Entity entity);

}
