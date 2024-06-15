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

import lombok.Data;
import lombok.With;
import org.jetbrains.annotations.Contract;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.utils.Writable;
import org.machinemc.api.utils.math.Vector2;
import org.machinemc.api.utils.math.Vector3;

/**
 * Represent a position of an entity.
 */
@Data
@With
public sealed class EntityPosition implements Cloneable, Writable permits Location {

    private double x, y, z;
    private float yaw, pitch;

    /**
     * Creates new position.
     * @param x x-coordinate of the position
     * @param y y-coordinate of the position
     * @param z z-coordinate of the position
     * @param yaw yaw of the position
     * @param pitch pitch of the position
     * @return new position
     */
    @Contract("_, _, _, _, _ -> new")
    public static EntityPosition of(final double x,
                                    final double y,
                                    final double z,
                                    final float yaw,
                                    final float pitch) {
        return new EntityPosition(x, y, z, yaw, pitch);
    }

    /**
     * Creates new position.
     * @param x x-coordinate of the position
     * @param y y-coordinate of the position
     * @param z z-coordinate of the position
     * @return new position
     */
    @Contract("_, _, _ -> new")
    public static EntityPosition of(final double x, final double y, final double z) {
        return new EntityPosition(x, y, z, 0, 0);
    }

    /**
     * Creates new entity position from block position.
     * @param blockPosition block positioncation
     * @return new position
     */
    @Contract("_ -> new")
    public static EntityPosition of(final BlockPosition blockPosition) {
        return new EntityPosition(blockPosition);
    }

    /**
     * Creates new position from a server buffer.
     * @param buf buffer with encoded position
     * @return entity position
     */
    @Contract("_ -> new")
    public static EntityPosition read(final ServerBuffer buf) {
        return new EntityPosition(buf.readDouble(), buf.readDouble(), buf.readDouble(),
                buf.readAngle(), buf.readAngle());
    }

    /**
     * Entity Position in a world.
     * @param x x-coordinate of the position
     * @param y y-coordinate of the position
     * @param z z-coordinate of the position
     * @param yaw yaw of the position
     * @param pitch pitch of the position
     */
    public EntityPosition(final double x, final double y, final double z, final float yaw, final float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = fixYaw(yaw);
        this.pitch = pitch;
    }

    /**
     * Entity Position in a world.
     * @param x x-coordinate of the position
     * @param y y-coordinate of the position
     * @param z z-coordinate of the position
     */
    public EntityPosition(final double x, final double y, final double z) {
        this(x, y, z, 0, 0);
    }

    /**
     * Position in the world from a block position.
     * @param blockPosition block position of the entity position
     */
    public EntityPosition(final BlockPosition blockPosition) {
        this(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
    }

    /**
     * Gets a unit-vector pointing in the direction that this position is
     * facing.
     * @return a vector pointing the direction of this position's {@link #getPitch()}
     * and {@link #getYaw()}
     */
    public Vector3 getDirection() {
        final Vector3 vector = new Vector3();
        final double rotX = this.getYaw();
        final double rotY = this.getPitch();

        vector.setY(-Math.sin(Math.toRadians(rotY)));
        final double xz = Math.cos(Math.toRadians(rotY));

        vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
        vector.setZ(xz * Math.cos(Math.toRadians(rotX)));
        return vector;
    }

    /**
     * Sets the {@link #getYaw() yaw} and {@link #getPitch() pitch} to point
     * in the direction of the vector.
     * @param vector the direction vector
     * @return the same position
     */
    @Contract("_ -> this")
    public EntityPosition setDirection(final Vector3 vector) {
        final double pii = 2 * Math.PI;
        final double x = vector.getX();
        final double z = vector.getZ();

        if (x == 0 && z == 0) {
            pitch = vector.getY() > 0 ? -90 : 90;
            return this;
        }

        final double theta = Math.atan2(-x, z);
        yaw = (float) Math.toDegrees((theta + pii) % pii);

        final double x2 = Math.pow(x, 2);
        final double z2 = Math.pow(z, 2);
        final double xz = Math.sqrt(x2 + z2);
        pitch = (float) Math.toDegrees(Math.atan(-vector.getY() / xz));

        return this;
    }

    /**
     * Offsets the position by a vector.
     * @param vector the vector
     * @return this
     */
    @Contract("_ -> this")
    public EntityPosition offset(final Vector3 vector) {
        x += vector.getX();
        y += vector.getY();
        z += vector.getZ();
        return this;
    }

    /**
     * @return x-coordinate of the position as whole number
     */
    public int getBlockX() {
        return (int) Math.floor(x);
    }

    /**
     * @return y-coordinate of the position as whole number
     */
    public int getBlockY() {
        return (int) Math.floor(y);
    }

    /**
     * @return z-coordinate of the position as whole number
     */
    public int getBlockZ() {
        return (int) Math.floor(z);
    }

    /**
     * Converts the entity position to a block position.
     * @return block position of this entity position
     */
    @Contract(pure = true)
    public BlockPosition toBlockPosition() {
        return new BlockPosition(getBlockX(), getBlockY(), getBlockZ());
    }

    /**
     * Converts this position to a vector.
     * @return vector of this position
     */
    @Contract(pure = true)
    public Vector3 toVector() {
        return Vector3.of(getBlockX(), getBlockY(), getBlockZ());
    }

    /**
     * Converts rotation of the position to a vector.
     * @return vector with yaw and pitch
     */
    public Vector2 getRotationVector() {
        return Vector2.of(getYaw(), getPitch());
    }

    /**
     * Updates the rotation to value of given vector.
     * @param rotation rotation vector
     */
    public void setRotation(final Vector2 rotation) {
        setYaw((float) rotation.getX());
        setPitch((float) rotation.getY());
    }

    /**
     * Writes the coordinates of the position to the buffer.
     * @param buf buffer to write into
     */
    public void writePos(final ServerBuffer buf) {
        buf.writeDouble(x).writeDouble(y).writeDouble(z);
    }

    /**
     * Writes the rotation of the position to the buffer.
     * @param buf buffer to write into
     */
    public void writeRot(final ServerBuffer buf) {
        buf.writeAngle(yaw).writeAngle(pitch);
    }

    /**
     * Writes the position to the buffer.
     * @param buf buffer to write into
     */
    @Override
    public void write(final ServerBuffer buf) {
        writePos(buf);
        writeRot(buf);
    }

    /**
     * Fixes the yaw between -180 and 180 degrees.
     * @param yaw yaw
     * @return same yaw but between -180 and 180 degrees
     */
    private static float fixYaw(final float yaw) {
        float fixedYaw = yaw % 360;
        if (fixedYaw < -180.0F) {
            fixedYaw += 360.0F;
        } else if (fixedYaw > 180.0F) {
            fixedYaw -= 360.0F;
        }
        return fixedYaw;
    }

    /**
     * Checks whether the position is valid or not.
     * @param position position
     * @return if the position is valid
     */
    public static boolean isInvalid(final EntityPosition position) {
        final double x = position.getX(), y = position.getY(), z = position.getZ();
        if (!(Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z)))
            return true;
        return Math.max(Math.abs(x), Math.abs(z)) > 3.2e+7;
    }

    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public EntityPosition clone() {
        return new EntityPosition(x, y, z, yaw, pitch);
    }

}
