package me.pesekjak.machine.world;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;
import me.pesekjak.machine.chunk.Chunk;
import me.pesekjak.machine.utils.math.Vector3;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the location in the world
 */
@AllArgsConstructor(staticName = "of")
@Data
@With
public class Location implements Cloneable {

    private double x, y, z;
    private float yaw, pitch;
    private World world;

    public static Location of(double x, double y, double z, World world) {
        return new Location(x, y, z, world);
    }

    public static Location of(BlockPosition blockPosition, World world) {
        return new Location(blockPosition, world);
    }

    public Location(double x, double y, double z, World world) {
        this(x, y, z, 0, 0, world);
    }

    public Location(BlockPosition blockPosition, World world) {
        this(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ(), world);
    }

    @Override
    public Location clone() {
        try {
            return (Location) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a unit-vector pointing in the direction that this Location is
     * facing.
     *
     * @return a vector pointing the direction of this location's {@link
     *     #getPitch() pitch} and {@link #getYaw() yaw}
     */
    @NotNull
    public Vector3 getDirection() {
        Vector3 vector = new Vector3();

        double rotX = this.getYaw();
        double rotY = this.getPitch();

        vector.setY(-Math.sin(Math.toRadians(rotY)));

        double xz = Math.cos(Math.toRadians(rotY));

        vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
        vector.setZ(xz * Math.cos(Math.toRadians(rotX)));

        return vector;
    }

    /**
     * Sets the {@link #getYaw() yaw} and {@link #getPitch() pitch} to point
     * in the direction of the vector.
     *
     * @param vector the direction vector
     * @return the same location
     */
    @NotNull
    public Location setDirection(@NotNull Vector3 vector) {
        /*
         * Sin = Opp / Hyp
         * Cos = Adj / Hyp
         * Tan = Opp / Adj
         *
         * x = -Opp
         * z = Adj
         */
        final double _2PI = 2 * Math.PI;
        final double x = vector.getX();
        final double z = vector.getZ();

        if (x == 0 && z == 0) {
            pitch = vector.getY() > 0 ? -90 : 90;
            return this;
        }

        double theta = Math.atan2(-x, z);
        yaw = (float) Math.toDegrees((theta + _2PI) % _2PI);

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

    public BlockPosition toBlockPosition() {
        return new BlockPosition(getBlockX(), getBlockY(), getBlockZ());
    }

    /**
     * Writes the coordinates of the location to the {@link FriendlyByteBuf}.
     * @param buf buffer to write into
     */
    public void writePos(FriendlyByteBuf buf) {
        buf.writeDouble(x)
                .writeDouble(y)
                .writeDouble(z);
    }

    /**
     * Writes the rotation of the location to the {@link FriendlyByteBuf}.
     * @param buf buffer to write into
     */
    public void writeRot(FriendlyByteBuf buf) {
        buf.writeAngle(yaw)
                .writeAngle(pitch);
    }

    /**
     * Writes the location to the {@link FriendlyByteBuf}.
     * @param buf buffer to write into
     */
    public void write(FriendlyByteBuf buf) {
        writePos(buf);
        writeRot(buf);
    }
}
