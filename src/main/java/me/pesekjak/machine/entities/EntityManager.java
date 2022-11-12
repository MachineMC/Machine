package me.pesekjak.machine.entities;

import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.world.World;
import me.pesekjak.machine.world.WorldManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class EntityManager {

    private final WorldManager worldManager;

    private final Map<UUID, Entity> entityMap = new ConcurrentHashMap<>();

    public static EntityManager createDefault(Machine server) {
        return new EntityManager(server.getWorldManager());
    }

    public Set<Entity> getEntitiesOfType(EntityType entityType) {
        return getEntities().stream()
                .filter(entity -> entity.getEntityType() == entityType)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<Entity> getEntitiesOfType(EntityType entityType, World world) {
        return getEntities(world).stream()
                .filter(entity -> entity.getEntityType() == entityType)
                .collect(Collectors.toUnmodifiableSet());
    }

    @SuppressWarnings("unchecked")
    public <E extends Entity> Set<E> getEntitiesOfClass(Class<E> entityClass) {
        return getEntities().stream()
                .filter(entity -> entity.getEntityType().getEntityClass().equals(entityClass))
                .map(entity -> (E) entity)
                .collect(Collectors.toUnmodifiableSet());
    }

    @SuppressWarnings("unchecked")
    public <E extends Entity> Set<E> getEntitiesOfClass(Class<E> entityClass, World world) {
        return getEntities(world).stream()
                .filter(entity -> entity.getEntityType().getEntityClass().equals(entityClass))
                .map(entity -> (E) entity)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<Entity> getEntities() {
        Set<Entity> entities = new LinkedHashSet<>();
        for(World world : worldManager.getWorlds())
            entities.addAll(world.getEntities());
        return Collections.unmodifiableSet(entities);
    }

    public Set<Entity> getEntities(World world) {
        if(world.manager() != worldManager)
            throw new IllegalStateException();
        return new LinkedHashSet<>(world.getEntities());
    }

    public Set<Entity> getEntities(Predicate<Entity> predicate) {
        return getEntities().stream().filter(predicate).collect(Collectors.toSet());
    }

    public Entity getEntity(UUID uuid) {
        return entityMap.get(uuid);
    }

    protected void addEntity(Entity entity) {
        if(entity.getWorld().manager() != worldManager || entityMap.containsKey(entity.getUuid()))
            throw new IllegalStateException();
        entity.getWorld().spawn(entity, entity.getLocation());
        entityMap.put(entity.getUuid(), entity);
    }

    protected void removeEntity(Entity entity) {
        if(entity.getWorld().manager() != worldManager || !entityMap.containsKey(entity.getUuid()))
            throw new IllegalStateException();
        entity.getWorld().remove(entity);
        entityMap.remove(entity.getUuid());
    }
}
