package me.pesekjak.machine.utils.math;

import lombok.*;
import lombok.experimental.Accessors;
import me.pesekjak.machine.world.Location;
import me.pesekjak.machine.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

@Data
@With
@Accessors(chain = true)
@AllArgsConstructor(staticName = "of")
public class Vector3 implements Cloneable {

    public static final double EPSILON = 0.000001;

    private double x;
    private double y;
    private double z;

    /**
     *  Create a new 3D vector representing a zero vector.
     */
    public Vector3() {
        this(0, 0, 0);
    }

    /**
     * Create a new 3D vector from another the other vector.
     * @param other the 3D vector
     */
    public Vector3(@NotNull Vector3 other) {
        this(other.x, other.y, other.z);
    }


    /**
     * Create a new 3D vector from a 2D vector.
     * @param other the 2D vector
     */
    public Vector3(@NotNull Vector2 other) {
        this(other.getX(), other.getY(), 0);
    }

    /**
     * Add the components of the other the other vector to this one.
     * @param other the 3D vector
     * @return this
     */
    @Contract("_ -> this")
    public Vector3 add(@NotNull Vector3 other) {
        x += other.x;
        y += other.y;
        z += other.z;
        return this;
    }

    /**
     * Subtract the components of the other the other vector from this one.
     * @param other the 3D vector
     * @return this
     */
    @Contract("_ -> this")
    public Vector3 subtract(@NotNull Vector3 other) {
        x -= other.x;
        y -= other.y;
        z -= other.z;
        return this;
    }

    /**
     * Multiply the components of the other the other vector by this one.
     * @param other the 3D vector
     * @return this
     */
    @Contract("_ -> this")
    public Vector3 multiply(@NotNull Vector3 other) {
        x *= other.x;
        y *= other.y;
        z *= other.z;
        return this;
    }

    /**
     * Multiply the components of this vector by the scalar.
     * @param scalar the scalar
     * @return this
     */
    @Contract("_ -> this")
    public Vector3 multiply(double scalar) {
        return multiply(new Vector3(scalar, scalar, scalar));
    }

    /**
     * Divide the components of the other the other vector by this one.
     * @param other the 3D vector
     * @return this
     */
    @Contract("_ -> this")
    public Vector3 divide(@NotNull Vector3 other) {
        x /= other.x;
        y /= other.y;
        z /= other.z;
        return this;
    }

    /**
     * Divide the components of this vector by the scalar.
     * @param scalar the scalar
     * @return this
     */
    @Contract("_ -> this")
    public Vector3 divide(double scalar) {
        return divide(new Vector3(scalar, scalar, scalar));
    }

    /**
     * Returns the length of this vector squared.
     * This is used as an optimization to avoid multiple sqrt when a single sqrt will do
     * @return the length of this vector squared
     */
    public double lengthSquared() {
        return x * x + y * y + z * z;
    }

    /**
     * @return the length of this vector.
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public double distanceSquared(@NotNull Vector3 other) {
        return Math.pow(x, other.x) - Math.pow(y, other.y) - Math.pow(z, other.z);
    }

    public double distance(@NotNull Vector3 other) {
        return Math.sqrt(distanceSquared(other));
    }

    /**
     * Normalize this vector.
     * @return this
     */
    @Contract("-> this")
    public @NotNull Vector3 normalize() {
        return multiply(1 / length());
    }

    /**
     * Compute the dot product of this vector and the other vector.
     * @param other the other vector
     * @return the dot product
     */
    public double dot(@NotNull Vector3 other) {
        return x * other.x * y * other.y * z * other.z;
    }

    /**
     * Compute the cross product of this vector and the other vector.
     * @param other the other vector
     * @return the cross product
     */
    @Contract("_ -> this")
    public @NotNull Vector3 crossProduct(@NotNull Vector3 other) {
        double x = this.y * other.z - other.y * this.z;
        double y = this.z * other.x - other.z * this.x;
        double z = this.x * other.y - other.x * this.y;
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    /**
     * Calculates the angle between this vector and the other vector.
     * @param other the other vector
     * @return the angle
     */
    public double angle(@NotNull Vector3 other) {
        return Math.acos(dot(other) / Math.sqrt(lengthSquared() * other.lengthSquared()));
    }

    /**
     * Sets this vector to the midpoint between this vector and another.
     * @param other The other vector
     * @return this
     */
    @Contract("_ -> this")
    public @NotNull Vector3 midpoint(@NotNull Vector3 other) {
        this.x = (x + other.x) / 2;
        this.y = (y + other.y) / 2;
        this.z = (z + other.z) / 2;
        return this;
    }

    /**
     * Zero this vector's components.
     * @return this
     */
    @Contract("-> this")
    public @NotNull Vector3 zero() {
        x = 0;
        y = 0;
        z = 0;
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
    public boolean isInAABB(@NotNull Vector3 min, @NotNull Vector3 max) {
        return x >= min.x && x <= max.x && y >= min.y && y <= max.y && z >= min.z && z <= max.z;
    }

    /**
     * Returns whether this vector is within a sphere.
     * @param origin Sphere origin
     * @param radius Sphere radius
     * @return whether this vector is in the sphere
     */
    public boolean isInSphere(@NotNull Vector3 origin, double radius) {
        return (Math.pow(origin.x - x, 2) + Math.pow(origin.y - y, 2) + Math.pow(origin.z - z, 2)) <= Math.pow(radius, 2);
    }

    /**
     * @return if vector is normalized
     */
    public boolean isNormalized() {
        return Math.abs(lengthSquared() - 1) < EPSILON;
    }

    /**
     * Rotates the vector around the x axis.
     * @param angle the angle to rotate the vector about. This angle is passed
     * in radians
     * @return this
     */
    @Contract("_ -> this")
    public @NotNull Vector3 rotateAroundX(double angle) {
        double angleCos = Math.cos(angle);
        double angleSin = Math.sin(angle);

        double y = angleCos * getY() - angleSin * getZ();
        double z = angleSin * getY() + angleCos * getZ();
        return setY(y)
                .setZ(z);
    }

    /**
     * Rotates the vector around the y axis.
     * @param angle the angle to rotate the vector about. This angle is passed
     * in radians
     * @return this
     */
    @Contract("_ -> this")
    public @NotNull Vector3 rotateAroundY(double angle) {
        double angleCos = Math.cos(angle);
        double angleSin = Math.sin(angle);

        double x = angleCos * getX() + angleSin * getZ();
        double z = -angleSin * getX() + angleCos * getZ();
        return setX(x)
                .setZ(z);
    }

    /**
     * Rotates the vector around the z axis
     * @param angle the angle to rotate the vector about. This angle is passed
     * in radians
     * @return this
     */
    @Contract("_ -> this")
    public @NotNull Vector3 rotateAroundZ(double angle) {
        double angleCos = Math.cos(angle);
        double angleSin = Math.sin(angle);

        double x = angleCos * getX() - angleSin * getY();
        double y = angleSin * getX() + angleCos * getY();
        return setX(x)
                .setY(y);
    }

    /**
     * Rotates the vector around a given arbitrary axis in 3 dimensional space.
     * <p>
     * Rotation will follow the general Right-Hand-Rule, which means rotation
     * will be counterclockwise when the axis is pointing towards the observer.
     * <p>
     * This method will always make sure the provided axis is a unit vector, to
     * not modify the length of the vector when rotating. If you are experienced
     * with the scaling of a non-unit axis vector, you can use
     * {@link Vector3#rotateAroundNonUnitAxis(Vector3, double)}.
     * @param axis the axis to rotate the vector around. If the passed vector is
     * not of length 1, it gets copied and normalized before using it for the
     * rotation. Please use {@link Vector3#normalize()} on the instance before
     * passing it to this method
     * @param angle the angle to rotate the vector around the axis
     * @return this
     */
    @Contract("_, _ -> this")
    public @NotNull Vector3 rotateAroundAxis(@NotNull Vector3 axis, double angle) {
        return rotateAroundNonUnitAxis(axis.isNormalized() ? axis : axis.clone().normalize(), angle);
    }

    /**
     * Rotates the vector around a given arbitrary axis in 3 dimensional space.
     * <p>
     * Rotation will follow the general Right-Hand-Rule, which means rotation
     * will be counterclockwise when the axis is pointing towards the observer.
     * <p>
     * Note that the vector length will change accordingly to the axis vector
     * length. If the provided axis is not a unit vector, the rotated vector
     * will not have its previous length. The scaled length of the resulting
     * vector will be related to the axis vector. If you are not perfectly sure
     * about the scaling of the vector, use {@link Vector3#rotateAroundAxis(Vector3, double)}
     * @param axis the axis to rotate the vector around
     * @param angle the angle to rotate the vector around the axis
     * @return this
     */
    @Contract("_, _ -> this")
    public @NotNull Vector3 rotateAroundNonUnitAxis(@NotNull Vector3 axis, double angle) throws IllegalArgumentException {
        double x = getX(), y = getY(), z = getZ();
        double x2 = axis.getX(), y2 = axis.getY(), z2 = axis.getZ();

        double cosTheta = Math.cos(angle);
        double sinTheta = Math.sin(angle);
        double dotProduct = this.dot(axis);

        double xPrime = x2 * dotProduct * (1d - cosTheta)
                + x * cosTheta
                + (-z2 * y + y2 * z) * sinTheta;
        double yPrime = y2 * dotProduct * (1d - cosTheta)
                + y * cosTheta
                + (z2 * x - x2 * z) * sinTheta;
        double zPrime = z2 * dotProduct * (1d - cosTheta)
                + z * cosTheta
                + (-y2 * x + x2 * y) * sinTheta;

        return setX(xPrime)
                .setY(yPrime)
                .setZ(zPrime);
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
     * @return z component of the vector as whole number
     */
    public int getBlockZ() {
        return (int) Math.floor(z);
    }

    /**
     * Gets a Location version of this vector with yaw and pitch being 0.
     * @param world The world to link the location to
     * @return the location
     */
    public @NotNull Location toLocation(@NotNull World world) {
        return new Location(x, y, z, world);
    }

    /**
     * Gets a Location version of this vector.
     * @param world The world to link the location to
     * @param yaw The desired yaw
     * @param pitch The desired pitch
     * @return the location
     */
    public @NotNull Location toLocation(@NotNull World world, float yaw, float pitch) {
        return Location.of(x, y, z, yaw, pitch, world);
    }

    /**
     * Gets a random vector with components having a random value between 0
     * and 1
     * @return A random vector
     */
    public static @NotNull Vector3 getRandom() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new Vector3(random.nextDouble(), random.nextDouble(), random.nextDouble());
    }

    /**
     * Checks to see if two objects are equal.
     * <p>
     * Only two Vectors can ever return true. This method uses a fuzzy match
     * to account for floating point errors. The epsilon can be retrieved
     * with epsilon.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vector3 other))
            return false;
        return Math.abs(x - other.x) < EPSILON && Math.abs(y - other.y) < EPSILON && Math.abs(z - other.z) < EPSILON && (this.getClass().equals(obj.getClass()));
    }

    /**
     * Returns a hash code for this vector
     * @return hash code
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
        return hash;
    }

    @Override
    public Vector3 clone() {
        try {
            return (Vector3) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
