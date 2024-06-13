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
import org.machinemc.api.Server;
import org.machinemc.api.entities.Entity;
import org.machinemc.api.entities.EntityManager;
import org.machinemc.api.entities.EntityType;
import org.machinemc.api.world.World;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Default entity manager implementation.
 */
public class ServerEntityManager implements EntityManager {

    @Getter
    private final Server server;
    private final Map<UUID, Entity> entityMap = new ConcurrentHashMap<>();

    public ServerEntityManager(final Server server) {
        this.server = Objects.requireNonNull(server, "Server can not be null");
    }

    /**
     * Creates default empty entity manager.
     * @param server server
     * @return new manager
     */
    public static ServerEntityManager createDefault(final Server server) {
        return new ServerEntityManager(server);
    }

    @Override
    public Set<Entity> getEntitiesOfType(final EntityType entityType) {
        Objects.requireNonNull(entityType, "Entity type can not be null");
        return getEntities().stream()
                .filter(entity -> entity.getEntityType() == entityType)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<Entity> getEntitiesOfType(final EntityType entityType, final World world) {
        Objects.requireNonNull(entityType, "Entity type can not be null");
        return getEntities(world).stream()
                .filter(entity -> entity.getEntityType() == entityType)
                .collect(Collectors.toUnmodifiableSet());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends Entity> Set<E> getEntitiesOfClass(final Class<E> entityClass) {
        Objects.requireNonNull(entityClass, "Entity class can not be null");
        return getEntities().stream()
                .filter(entity -> entityClass.isAssignableFrom(entity.getClass()))
                .map(entity -> (E) entity)
                .collect(Collectors.toUnmodifiableSet());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends Entity> Set<E> getEntitiesOfClass(final Class<E> entityClass, final World world) {
        Objects.requireNonNull(entityClass, "Entity class can not be null");
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
        Objects.requireNonNull(world, "World can not be null");
        return getEntities(entity -> entity.getWorld().equals(world));
    }

    @Override
    public Set<Entity> getEntities(final Predicate<Entity> predicate) {
        Objects.requireNonNull(predicate, "Predicate can not be null");
        return getEntities().stream().filter(predicate).collect(Collectors.toSet());
    }

    @Override
    public Optional<Entity> getEntity(final UUID uuid) {
        Objects.requireNonNull(uuid, "UUID can not be null");
        return Optional.ofNullable(entityMap.get(uuid));
    }

    @Override
    public void addEntity(final Entity entity) {
        Objects.requireNonNull(entity, "Entity can not be null");
        entityMap.put(entity.getUUID(), entity);
    }

    @Override
    public void removeEntity(final Entity entity) {
        Objects.requireNonNull(entity, "Entity can not be null");
        entityMap.remove(entity.getUUID());
    }

}
