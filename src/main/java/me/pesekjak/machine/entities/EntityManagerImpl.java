package me.pesekjak.machine.entities;

import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.world.World;
import me.pesekjak.machine.world.WorldManager;
import org.jetbrains.annotations.NotNull;
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

    private final @NotNull WorldManager worldManager;

    private final Map<UUID, Entity> entityMap = new ConcurrentHashMap<>();

    /**
     * Creates default empty entity manager.
     * @param server server
     * @return new manager
     */
    public static @NotNull EntityManagerImpl createDefault(@NotNull Machine server) {
        return new EntityManagerImpl(server.getWorldManager());
    }

    @Override
    public @NotNull Set<Entity> getEntitiesOfType(@NotNull EntityType entityType) {
        return getEntities().stream()
                .filter(entity -> entity.getEntityType() == entityType)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public @NotNull Set<Entity> getEntitiesOfType(@NotNull EntityType entityType, @NotNull World world) {
        return getEntities(world).stream()
                .filter(entity -> entity.getEntityType() == entityType)
                .collect(Collectors.toUnmodifiableSet());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends Entity> @NotNull Set<E> getEntitiesOfClass(@NotNull Class<E> entityClass) {
        return getEntities().stream()
                .filter(entity -> entityClass.isAssignableFrom(entity.getClass()))
                .map(entity -> (E) entity)
                .collect(Collectors.toUnmodifiableSet());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends Entity> @NotNull Set<E> getEntitiesOfClass(@NotNull Class<E> entityClass, @NotNull World world) {
        return getEntities(world).stream()
                .filter(entity -> entityClass.isAssignableFrom(entity.getClass()))
                .map(entity -> (E) entity)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public @NotNull Set<Entity> getEntities() {
        Set<ServerEntity> entities = new LinkedHashSet<>();
        for(World world : worldManager.getWorlds()) {
            for(Entity entity : world.getEntities()) {
                entities.add((ServerEntity) entity);
            }
        }
        return Collections.unmodifiableSet(entities);
    }

    @Override
    public @NotNull Set<Entity> getEntities(@NotNull World world) {
        if(world.getManager() != worldManager)
            throw new IllegalStateException();
        return new LinkedHashSet<>(world.getEntities());
    }

    @Override
    public @NotNull Set<Entity> getEntities(@NotNull Predicate<Entity> predicate) {
        return getEntities().stream().filter(predicate).collect(Collectors.toSet());
    }

    @Override
    public @Nullable Entity getEntity(@NotNull UUID uuid) {
        return entityMap.get(uuid);
    }

    @Override
    public void addEntity(@NotNull Entity entity) {
        if(entity.getWorld().getManager() != worldManager || entityMap.containsKey(entity.getUuid()))
            throw new IllegalStateException();
        entity.getWorld().spawn(entity, entity.getLocation());
        entityMap.put(entity.getUuid(), entity);
    }

    @Override
    public void removeEntity(@NotNull Entity entity) {
        if(entity.getWorld().getManager() != worldManager || !entityMap.containsKey(entity.getUuid()))
            throw new IllegalStateException();
        entity.getWorld().remove(entity);
        entityMap.remove(entity.getUuid());
    }
}
