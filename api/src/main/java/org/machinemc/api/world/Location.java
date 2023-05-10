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
package org.machinemc.api.world;

import lombok.*;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.utils.Writable;
import org.jetbrains.annotations.Contract;

/**
 * Represents the location in the world.
 */
@ToString
@EqualsAndHashCode(callSuper = true)
public final class Location extends EntityPosition implements Cloneable, Writable {

    @Getter @Setter
    private World world;

    /**
     * Creates new location.
     * @param x x-coordinate of the location
     * @param y y-coordinate of the location
     * @param z z-coordinate of the location
     * @param yaw yaw of the location
     * @param pitch pitch of the location
     * @param world world of the location
     * @return new location
     */
    @Contract("_, _, _, _, _, _ -> new")
    public static Location of(final double x,
                              final double y,
                              final double z,
                              final float yaw,
                              final float pitch,
                              final World world) {
        return new Location(x, y, z, yaw, pitch, world);
    }

    /**
     * Creates new location.
     * @param x x-coordinate of the location
     * @param y y-coordinate of the location
     * @param z z-coordinate of the location
     * @param world world of the location
     * @return new location
     */
    @Contract("_, _, _, _ -> new")
    public static Location of(final double x, final double y, final double z, final World world) {
        return new Location(x, y, z, world);
    }

    /**
     * Creates new location from block position.
     * @param blockPosition block position of the location
     * @param world world of the location
     * @return new location
     */
    @Contract("_, _ -> new")
    public static Location of(final BlockPosition blockPosition, final World world) {
        return new Location(blockPosition, world);
    }

    /**
     * Creates new location from entity position.
     * @param entityPosition entity position of the location
     * @param world world of the location
     * @return new location
     */
    @Contract("_, _ -> new")
    public static Location of(final EntityPosition entityPosition, final World world) {
        return new Location(entityPosition, world);
    }

    /**
     * Creates new location from a server buffer.
     * @param buf buffer with encoded location
     * @param world world of the location
     * @return decoded location
     */
    @Contract("_, _ -> new")
    public static Location read(final ServerBuffer buf, final World world) {
        return new Location(buf.readDouble(), buf.readDouble(), buf.readDouble(),
                buf.readAngle(), buf.readAngle(), world);
    }

    /**
     * Location in a world.
     * @param x x-coordinate of the location
     * @param y y-coordinate of the location
     * @param z z-coordinate of the location
     * @param yaw yaw of the location
     * @param pitch pitch of the location
     * @param world world of the location
     */
    public Location(final double x,
                    final double y,
                    final double z,
                    final float yaw,
                    final float pitch,
                    final World world) {
        super(x, y, z, yaw, pitch);
        this.world = world;
    }

    /**
     * Location in the world.
     * @param x x-coordinate of the location
     * @param y y-coordinate of the location
     * @param z z-coordinate of the location
     * @param world world of the location
     */
    public Location(final double x, final double y, final double z, final World world) {
        this(x, y, z, 0, 0, world);
    }

    /**
     * Location in the world from a block position.
     * @param blockPosition block position of the location
     * @param world world of the location
     */
    public Location(final BlockPosition blockPosition, final World world) {
        this(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ(), world);
    }

    /**
     * Location in the world from a entity position.
     * @param entityPosition entity position of the location
     * @param world world of the location
     */
    public Location(final EntityPosition entityPosition, final World world) {
        this(entityPosition.getX(), entityPosition.getY(), entityPosition.getZ(),
                entityPosition.getYaw(), entityPosition.getPitch(), world);
    }

    /**
     * Returns clone of this location but with specified world.
     * @param world world of the new location
     * @return new location
     */
    public Location withWorld(final World world) {
        final Location clone = clone();
        clone.world = world;
        return clone;
    }

    @Override
    public Location clone() {
        return new Location(getX(), getY(), getZ(), getYaw(), getPitch(), world);
    }

}
