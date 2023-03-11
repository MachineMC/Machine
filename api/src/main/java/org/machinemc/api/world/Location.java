package org.machinemc.api.world;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.utils.Writable;
import org.machinemc.api.utils.math.Vector3;
import org.jetbrains.annotations.Contract;

/**
 * Represents the location in the world
 */
@Data
@With
@AllArgsConstructor(staticName = "of")
public class Location implements Cloneable, Writable {

    private double x, y, z;
    private float yaw, pitch;
    private World world;

    /**
     * Creates new location.
     * @param x x-coordinate of the location
     * @param y y-coordinate of the location
     * @param z z-coordinate of the location
     * @param world world of the location
     * @return new location
     */
    @Contract("_, _, _, _ -> new")
    public static Location of(double x, double y, double z, World world) {
        return new Location(x, y, z, world);
    }

    /**
     * Creates new location from block position.
     * @param blockPosition block position of the location
     * @param world world of the location
     * @return new location
     */
    @Contract("_, _ -> new")
    public static Location of(BlockPosition blockPosition, World world) {
        return new Location(blockPosition, world);
    }

    /**
     * Creates new location from a server buffer.
     * @param buf buffer with encoded location
     * @param world world of the location
     * @return decoded location
     */
    @Contract("_, _ -> new")
    public static Location from(ServerBuffer buf, World world) {
        return new Location(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readAngle(), buf.readAngle(), world);
    }

    /**
     * Location in the world.
     * @param x x-coordinate of the location
     * @param y y-coordinate of the location
     * @param z z-coordinate of the location
     * @param world world of the location
     */
    public Location(double x, double y, double z, World world) {
        this(x, y, z, 0, 0, world);
    }

    /**
     * Location in the world from a block position.
     * @param blockPosition block position of the location
     * @param world world of the location
     */
    public Location(BlockPosition blockPosition, World world) {
        this(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ(), world);
    }

    /**
     * Gets a unit-vector pointing in the direction that this Location is
     * facing.
     * @return a vector pointing the direction of this location's {@link Location#getPitch()} and {@link Location#getYaw()}
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
     * @return the same location
     */
    @Contract("_ -> this")
    public Location setDirection(Vector3 vector) {
        final double PII = 2 * Math.PI;
        final double x = vector.getX();
        final double z = vector.getZ();

        if (x == 0 && z == 0) {
            pitch = vector.getY() > 0 ? -90 : 90;
            return this;
        }

        double theta = Math.atan2(-x, z);
        yaw = (float) Math.toDegrees((theta + PII) % PII);

        double x2 = Math.pow(x, 2);
        double z2 = Math.pow(z, 2);
        double xz = Math.sqrt(x2 + z2);
        pitch = (float) Math.toDegrees(Math.atan(-vector.getY() / xz));

        return this;
    }

    /**
     * Offsets the location by a vector.
     * @param vector The vector.
     * @return this.
     */
     @Contract("_ -> this")
    public Location offset(Vector3 vector) {
        x += vector.getX();
        y += vector.getY();
        z += vector.getZ();
        return this;
    }

    /**
     * @return x-coordinate of the location as whole number
     */
    public int getBlockX() {
        return (int) Math.floor(x);
    }

    /**
     * @return y-coordinate of the location as whole number
     */
    public int getBlockY() {
        return (int) Math.floor(y);
    }

    /**
     * @return z-coordinate of the location as whole number
     */
    public int getBlockZ() {
        return (int) Math.floor(z);
    }

    /**
     * Converts the location to a block position, information about
     * world of the location will be lost by this operation.
     * @return block position of this location
     */
    @Contract(pure = true)
    public BlockPosition toBlockPosition() {
        return new BlockPosition(getBlockX(), getBlockY(), getBlockZ());
    }

    @Contract(pure = true)
    public Vector3 toVector() {
        return Vector3.of(getBlockX(), getBlockY(), getBlockZ());
    }

    /**
     * Writes the coordinates of the location to the buffer.
     * @param buf buffer to write into
     */
    public void writePos(ServerBuffer buf) {
        buf.writeDouble(x).writeDouble(y).writeDouble(z);
    }

    /**
     * Writes the rotation of the location to the buffer.
     * @param buf buffer to write into
     */
    public void writeRot(ServerBuffer buf) {
        buf.writeAngle(yaw).writeAngle(pitch);
    }

    /**
     * Writes the location to the buffer.
     * @param buf buffer to write into
     */
    @Override
    public void write(ServerBuffer buf) {
        writePos(buf);
        writeRot(buf);
    }

    @Override
    public Location clone() {
        try {
            return (Location) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
