package me.pesekjak.machine.entities;

import me.pesekjak.machine.world.Location;
import me.pesekjak.machine.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Manager of server entities in mutiple worlds.
 */
public interface EntityManager {

    /**
     * Returns set of entities of certain entity type.
     * @param entityType entity type of the entities
     * @return entities of the given type
     */
    @Unmodifiable @NotNull Set<Entity> getEntitiesOfType(@NotNull EntityType entityType);

    /**
     * Returns set of entities of certain entity type in given world.
     * @param entityType entity type of the entities
     * @param world world to search in
     * @return entities of the given type in given world
     */
    @Unmodifiable @NotNull Set<Entity> getEntitiesOfType(@NotNull EntityType entityType, @NotNull World world);

    /**
     * Returns set of entities of certain class.
     * @param entityClass entity class
     * @param <E> entity class
     * @return entities of the given class
     */
    <E extends Entity> @Unmodifiable @NotNull Set<E> getEntitiesOfClass(@NotNull Class<E> entityClass);

    /**
     * Returns set of entities of certain class in given world.
     * @param entityClass entity class
     * @param world world to search in
     * @param <E> entity class
     * @return entities of the given class in given world
     */
    <E extends Entity> @Unmodifiable @NotNull Set<E> getEntitiesOfClass(@NotNull Class<E> entityClass, @NotNull World world);

    /**
     * @return all entities managed by this manager
     */
    @Unmodifiable @NotNull Set<Entity> getEntities();

    /**
     * Returns entities managed by this manager in given world.
     * @param world world to search in
     * @return all entities in given world
     */
    @Unmodifiable @NotNull Set<Entity> getEntities(@NotNull World world);

    /**
     * @param predicate predicate function
     * @return entities managed by this manager where the test was successful
     */
    @Unmodifiable @NotNull Set<Entity> getEntities(@NotNull Predicate<Entity> predicate);

    /**
     * Searches for entity with given uuid.
     * @param uuid uuid of the entity
     * @return entity with given uuid
     */
    @Nullable Entity getEntity(@NotNull UUID uuid);

    /**
     * Adds entity into the manager.
     * @see World#spawn(Entity, Location)
     * @param entity entity to add
     */
    @ApiStatus.Internal
    void addEntity(@NotNull Entity entity);

    /**
     * Removes an entity from the manager.
     * @see Entity#remove()
     * @param entity entity to remove
     */
    @ApiStatus.Internal
    void removeEntity(@NotNull Entity entity);

}
