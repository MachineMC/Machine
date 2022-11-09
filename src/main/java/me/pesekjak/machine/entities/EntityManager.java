package me.pesekjak.machine.entities;

import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.world.World;
import me.pesekjak.machine.world.WorldManager;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("ClassCanBeRecord")
@RequiredArgsConstructor
public class EntityManager {

    private final WorldManager worldManager;

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

    protected void addEntity(Entity entity) {
        if(entity.getWorld().manager() != worldManager)
            throw new IllegalStateException();
        entity.getWorld().spawn(entity, entity.getLocation());
    }

    protected void removeEntity(Entity entity) {
        if(entity.getWorld().manager() != worldManager)
            throw new IllegalStateException();
        entity.getWorld().remove(entity);
    }
}
