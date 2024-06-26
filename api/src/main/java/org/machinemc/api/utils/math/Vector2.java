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
package org.machinemc.api.utils.math;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Contract;

import java.util.concurrent.ThreadLocalRandom;

@Data
@With
@Accessors(chain = true)
@AllArgsConstructor(staticName = "of")
public class Vector2 implements Cloneable {

    public static final double EPSILON = 0.000001;

    private double x;
    private double y;

    /**
     *  Create a new 2D vector representing a zero vector.
     */
    public Vector2() {
        this(0, 0);
    }

    /**
     * Create a new 2D vector from another the other vector.
     * @param other the 2D vector
     */
    public Vector2(final Vector2 other) {
        this(other.x, other.y);
    }


    /**
     * Create a new 2D vector from a 3D vector.
     * @param other the 3D vector
     */
    public Vector2(final Vector3 other) {
        this(other.getX(), other.getY());
    }

    /**
     * Add the components of the other the other vector to this one.
     * @param other the 2D vector
     * @return this
     */
    @Contract("_ -> this")
    public Vector2 add(final Vector2 other) {
        x += other.x;
        y += other.y;
        return this;
    }

    /**
     * Subtract the components of the other the other vector from this one.
     * @param other the 2D vector
     * @return this
     */
    @Contract("_ -> this")
    public Vector2 subtract(final Vector2 other) {
        x -= other.x;
        y -= other.y;
        return this;
    }

    /**
     * Multiply the components of the other the other vector by this one.
     * @param other the 2D vector
     * @return this
     */
    @Contract("_ -> this")
    public Vector2 multiply(final Vector2 other) {
        x *= other.x;
        y *= other.y;
        return this;
    }

    /**
     * Multiply the components of this vector by the scalar.
     * @param scalar the scalar
     * @return this
     */
    @Contract("_ -> this")
    public Vector2 multiply(final double scalar) {
        return multiply(new Vector2(scalar, scalar));
    }

    /**
     * Divide the components of the other the other vector by this one.
     * @param other the 2D vector
     * @return this
     */
    @Contract("_ -> this")
    public Vector2 divide(final Vector2 other) {
        x /= other.x;
        y /= other.y;
        return this;
    }

    /**
     * Divide the components of this vector by the scalar.
     * @param scalar the scalar
     * @return this
     */
    @Contract("_ -> this")
    public Vector2 divide(final double scalar) {
        return divide(new Vector2(scalar, scalar));
    }

    /**
     * Returns the length of this vector squared.
     * This is used as an optimization to avoid multiple sqrt when a single sqrt will do.
     * @return the length of this vector squared
     */
    public double lengthSquared() {
        return x * x + y * y;
    }

    /**
     * @return the length of this vector
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /**
     * Calculates squared distance between two vectors.
     * @param other other vector
     * @return squared distance
     */
    public double distanceSquared(final Vector2 other) {
        return Math.pow(x, other.x) - Math.pow(y, other.y);
    }

    /**
     * Calculates distance between two vectors.
     * @param other other vector
     * @return distance
     */
    public double distance(final Vector2 other) {
        return Math.sqrt(distanceSquared(other));
    }

    /**
     * Normalize this vector.
     * @return this
     */
    @Contract("-> this")
    public Vector2 normalize() {
        return multiply(1 / length());
    }

    /**
     * Compute the dot product of this vector and the other vector.
     * @param other the other vector
     * @return the dot product
     */
    public double dot(final Vector2 other) {
        return x * other.x * y * other.y;
    }

    /**
     * Compute the cross product of this vector and the other vector.
     * @param other the other vector
     * @return the cross product scalar
     */
    public double crossProduct(final Vector2 other) {
        return this.x * other.y - other.x * this.y;
    }


    /**
     * Calculates the angle between this vector and the other vector.
     * @param other the other vector
     * @return the angle
     */
    public double angle(final Vector2 other) {
        return Math.acos(dot(other) / Math.sqrt(lengthSquared() * other.lengthSquared()));
    }

    /**
     * Sets this vector to the midpoint between this vector and another.
     * @param other The other vector
     * @return this
     */
    @Contract("_ -> this")
    public Vector2 midpoint(final Vector2 other) {
        this.x = (x + other.x) / 2;
        this.y = (y + other.y) / 2;
        return this;
    }

    /**
     * Zero this vector's components.
     * @return this
     */
    @Contract("-> this")
    public Vector2 zero() {
        x = 0;
        y = 0;
        return this;
    }

    /**
     * Returns whether this vector is in an axis-aligned bounding box.
     * <p>
     * The minimum and maximum vectors given must be truly the minimum and
     * maximum X, Y and Z components.
     * @param min Minimum vector
     * @param max Maximum vector
     * @return whether this vector is in the AABB
     */
    public boolean isInAABB(final Vector2 min, final Vector2 max) {
        return x >= min.x && x <= max.x && y >= min.y && y <= max.y;
    }

    /**
     * Returns whether this vector is within a sphere.
     * @param origin Sphere origin.
     * @param radius Sphere radius.
     * @return whether this vector is in the sphere.
     */
    public boolean isInSphere(final Vector2 origin, final double radius) {
        return (Math.pow(origin.x - x, 2) + Math.pow(origin.y - y, 2)) <= Math.pow(radius, 2);
    }

    /**
     * @return if the vector is normalized
     */
    public boolean isNormalized() {
        return Math.abs(lengthSquared() - 1) < EPSILON;
    }

    /**
     * @return x component of the vector as whole number
     */
    public int getBlockX() {
        return (int) Math.floor(x);
    }

    /**
     * @return y component of the vector as whole number
     */
    public int getBlockY() {
        return (int) Math.floor(y);
    }

    /**
     * Gets a random vector with components having a random value between 0
     * and 1.
     * @return A random vector
     */
    public static Vector2 getRandom() {
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        return new Vector2(random.nextDouble(), random.nextDouble());
    }

    /**
     * Checks to see if two objects are equal.
     * <p>
     * Only two Vectors can ever return true. This method uses a fuzzy match
     * to account for floating point errors. The epsilon can be retrieved
     * with epsilon.
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Vector2 other))
            return false;
        return Math.abs(x - other.x) < EPSILON
                && Math.abs(y - other.y) < EPSILON
                && (this.getClass().equals(obj.getClass()));
    }

    /**
     * Returns a hash code for this vector.
     * @return hash code
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        return hash;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Vector2 clone() {
        return new Vector2(x, y);
    }

}
