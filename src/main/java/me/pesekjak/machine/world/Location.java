package me.pesekjak.machine.world;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;
import me.pesekjak.machine.utils.FriendlyByteBuf;

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
        buf.writeFloat(yaw)
                .writeFloat(pitch);
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
