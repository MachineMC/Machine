package org.machinemc.server.entities;

import lombok.RequiredArgsConstructor;
import org.machinemc.server.Machine;
import org.machinemc.api.entities.Entity;
import org.machinemc.api.entities.EntityManager;
import org.machinemc.api.entities.EntityType;
import org.machinemc.api.world.World;
import org.machinemc.api.world.WorldManager;
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

    private final WorldManager worldManager;

    private final Map<UUID, Entity> entityMap = new ConcurrentHashMap<>();

    /**
     * Creates default empty entity manager.
     * @param server server
     * @return new manager
     */
    public static EntityManagerImpl createDefault(Machine server) {
        return new EntityManagerImpl(server.getWorldManager());
    }

    @Override
    public Set<Entity> getEntitiesOfType(EntityType entityType) {
        return getEntities().stream()
                .filter(entity -> entity.getEntityType() == entityType)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<Entity> getEntitiesOfType(EntityType entityType, World world) {
        return getEntities(world).stream()
                .filter(entity -> entity.getEntityType() == entityType)
                .collect(Collectors.toUnmodifiableSet());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends Entity> Set<E> getEntitiesOfClass(Class<E> entityClass) {
        return getEntities().stream()
                .filter(entity -> entityClass.isAssignableFrom(entity.getClass()))
                .map(entity -> (E) entity)
                .collect(Collectors.toUnmodifiableSet());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends Entity> Set<E> getEntitiesOfClass(Class<E> entityClass, World world) {
        return getEntities(world).stream()
                .filter(entity -> entityClass.isAssignableFrom(entity.getClass()))
                .map(entity -> (E) entity)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<Entity> getEntities() {
        Set<ServerEntity> entities = new LinkedHashSet<>();
        for(World world : worldManager.getWorlds()) {
            for(Entity entity : world.getEntities()) {
                entities.add((ServerEntity) entity);
            }
        }
        return Collections.unmodifiableSet(entities);
    }

    @Override
    public Set<Entity> getEntities(World world) {
        if(world.getManager() != worldManager)
            throw new IllegalStateException();
        return new LinkedHashSet<>(world.getEntities());
    }

    @Override
    public Set<Entity> getEntities(Predicate<Entity> predicate) {
        return getEntities().stream().filter(predicate).collect(Collectors.toSet());
    }

    @Override
    public @Nullable Entity getEntity(UUID uuid) {
        return entityMap.get(uuid);
    }

    @Override
    public void addEntity(Entity entity) {
        if(entity.getWorld().getManager() != worldManager || entityMap.containsKey(entity.getUuid()))
            throw new IllegalStateException();
        entity.getWorld().spawn(entity, entity.getLocation());
        entityMap.put(entity.getUuid(), entity);
    }

    @Override
    public void removeEntity(Entity entity) {
        if(entity.getWorld().getManager() != worldManager || !entityMap.containsKey(entity.getUuid()))
            throw new IllegalStateException();
        entity.getWorld().remove(entity);
        entityMap.remove(entity.getUuid());
    }
}
