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
package org.machinemc.server.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.machinemc.server.Machine;
import org.machinemc.api.entities.Entity;
import org.machinemc.api.entities.EntityManager;
import org.machinemc.api.entities.EntityType;
import org.machinemc.api.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Default entity manager implementation.
 */
@RequiredArgsConstructor
public class EntityManagerImpl implements EntityManager {

    @Getter
    private final Machine server;
    private final Map<UUID, Entity> entityMap = new ConcurrentHashMap<>();

    /**
     * Creates default empty entity manager.
     * @param server server
     * @return new manager
     */
    public static EntityManagerImpl createDefault(final Machine server) {
        return new EntityManagerImpl(server);
    }

    @Override
    public Set<Entity> getEntitiesOfType(final EntityType entityType) {
        return getEntities().stream()
                .filter(entity -> entity.getEntityType() == entityType)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<Entity> getEntitiesOfType(final EntityType entityType, final World world) {
        return getEntities(world).stream()
                .filter(entity -> entity.getEntityType() == entityType)
                .collect(Collectors.toUnmodifiableSet());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends Entity> Set<E> getEntitiesOfClass(final Class<E> entityClass) {
        return getEntities().stream()
                .filter(entity -> entityClass.isAssignableFrom(entity.getClass()))
                .map(entity -> (E) entity)
                .collect(Collectors.toUnmodifiableSet());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends Entity> Set<E> getEntitiesOfClass(final Class<E> entityClass, final World world) {
        return getEntities(world).stream()
                .filter(entity -> entityClass.isAssignableFrom(entity.getClass()))
                .map(entity -> (E) entity)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<Entity> getEntities() {
        final Set<Entity> entities = new LinkedHashSet<>(entityMap.values());
        return Collections.unmodifiableSet(entities);
    }

    @Override
    public Set<Entity> getEntities(final World world) {
        return getEntities(entity -> entity.getWorld().equals(world));
    }

    @Override
    public Set<Entity> getEntities(final Predicate<Entity> predicate) {
        return getEntities().stream().filter(predicate).collect(Collectors.toSet());
    }

    @Override
    public @Nullable Entity getEntity(final UUID uuid) {
        return entityMap.get(uuid);
    }

    @Override
    public void addEntity(final Entity entity) {
        entityMap.put(entity.getUUID(), entity);
    }

    @Override
    public void removeEntity(final Entity entity) {
        entityMap.remove(entity.getUUID());
    }
}
