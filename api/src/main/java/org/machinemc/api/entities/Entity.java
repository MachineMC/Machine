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
package org.machinemc.api.entities;

import org.machinemc.api.server.NBTSerializable;
import org.machinemc.api.server.ServerProperty;
import org.machinemc.api.world.Location;
import org.machinemc.api.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Unmodifiable;
import org.machinemc.nbt.NBTCompound;

import java.util.Set;
import java.util.UUID;

/**
 * Represents a base entity in the world.
 */
public interface Entity extends ServerProperty, NBTSerializable, Nameable {

    /**
     * @return entity type of the entity
     */
    EntityType getEntityType();

    /**
     * @return uuid of the entity
     */
    UUID getUuid();

    /**
     * @return id of the entity
     */
    int getEntityId();

    /**
     * @return true if the entity is active
     */
    boolean isActive();

    /**
     * @return set of entity's tags
     */
    @Unmodifiable Set<String> getTags();

    /**
     * Adds new tag to the entity if number of active tags is less
     * than 1024.
     * @param tag tag to add
     * @return if the operation was successful
     */
    boolean addTag(String tag);

    /**
     * Removes a tag from the entity.
     * @param tag tag to remove
     * @return if the operation was successful
     */
    boolean removeTag(String tag);

    /**
     * @return if the entity is silent
     */
    boolean isSilent();

    /**
     * @param silent new silent
     */
    void setSilent(boolean silent);

    /**
     * @return if the entity has gravity
     */
    boolean isNoGravity();

    /**
     * @param noGravity new no gravity
     */
    void setNoGravity(boolean noGravity);

    /**
     * @return if the entity is glowing
     */
    boolean isGlowing();

    /**
     * @param glowing new glowing
     */
    void setGlowing(boolean glowing);

    /**
     * @return if the entity has visual fire
     */
    boolean isHasVisualFire();

    /**
     * @param hasVisualFire new visual fire
     */
    void setHasVisualFire(boolean hasVisualFire);

    /**
     * @return frozen ticks of the entity
     */
    int getTicksFrozen();

    /**
     * @param ticksFrozen new ticks frozen
     */
    void setTicksFrozen(int ticksFrozen);

    /**
     * @return clone of the location of the entity
     */
    Location getLocation();

    /**
     * @return previous location of the entity
     */
    Location getPreviousLocation();

    /**
     * @return fall distance of the entity
     */
    float getFallDistance();

    /**
     * @param fallDistance new fall distance
     */
    void setFallDistance(float fallDistance);

    /**
     * @return remaining fire ticks of the entity
     */
    short getRemainingFireTicks();

    /**
     * @param remainingFireTicks new remaining fire ticks
     */
    void setRemainingFireTicks(short remainingFireTicks);

    /**
     * @return if the entity is on ground
     */
    boolean isOnGround();

    /**
     * @return if the entity is invulnerable
     */
    boolean isInvulnerable();

    /**
     * @param invulnerable new invulnerable
     */
    void setInvulnerable(boolean invulnerable);

    /**
     * @return portal cooldown of the entity
     */
    int getPortalCooldown();

    /**
     * @param portalCooldown new portal cooldown
     */
    void setPortalCooldown(int portalCooldown);

    /**
     * @return if the custom name of entity is visible
     */
    boolean isCustomNameVisible();

    /**
     * @param customNameVisible new custom name
     */
    void setCustomNameVisible(boolean customNameVisible);

    /**
     * @return name of the entity
     */
    String getName();

    /**
     * @return world the entity is in
     */
    World getWorld();

    /**
     * Initializes the entity.
     */
    @ApiStatus.Internal
    void init();

    /**
     * Loads entity's data from a NBT Compound.
     * @param nbtCompound nbt compound with entity data
     */
    @ApiStatus.Internal
    void load(NBTCompound nbtCompound);

    /**
     * Removes the entity from the world.
     */
    void remove();

}
