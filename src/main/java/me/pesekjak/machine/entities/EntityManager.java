package me.pesekjak.machine.entities;

import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("ClassCanBeRecord")
@RequiredArgsConstructor
public class EntityManager {

    private final Machine server;

    public static EntityManager createDefault(Machine server) {
        return new EntityManager(server);
    }

    public List<Entity> getEntitiesOfType(EntityType entityType) {
        return getEntities().stream()
                .filter(entity -> entity.getEntityType() == entityType)
                .collect(Collectors.toList());
    }

    public List<Entity> getEntitiesOfType(EntityType entityType, World world) {
        return getEntities(world).stream()
                .filter(entity -> entity.getEntityType() == entityType)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public <E extends Entity> List<E> getEntitiesOfClass(Class<E> entityClass) {
        return getEntities().stream()
                .filter(entity -> entity.getEntityType().getEntityClass().equals(entityClass))
                .map(entity -> (E) entity)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public <E extends Entity> List<E> getEntitiesOfClass(Class<E> entityClass, World world) {
        return getEntities(world).stream()
                .filter(entity -> entity.getEntityType().getEntityClass().equals(entityClass))
                .map(entity -> (E) entity)
                .collect(Collectors.toList());
    }

    public List<Entity> getEntities() {
        List<Entity> entities = new ArrayList<>();
        for (World world : server.getWorldManager().getWorlds())
            entities.addAll(world.getEntityList());
        return entities;
    }

    public List<Entity> getEntities(World world) {
        return new ArrayList<>(world.getEntityList());
    }

    protected void addEntity(Entity entity) {
        entity.getWorld().getEntityList().add(entity);
    }

    protected void removeEntity(Entity entity) {
        entity.getWorld().getEntityList().remove(entity);
    }
}
