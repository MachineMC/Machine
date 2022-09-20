package me.pesekjak.machine.world;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
@Data
public class Location implements Cloneable {

    @Getter @Setter
    private double x, y, z;
    @Getter @Setter
    private float yaw, pitch;
    @Getter @Setter
    private World world;

    public Location(double x, double y, double z, byte yaw, byte pitch, World world) {
        this(x, y, z, (yaw * 360f) / 256f, (pitch * 360f) / 256f, world);
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

    public int getBlockX() {
        return (int) Math.floor(x);
    }

    public int getBlockY() {
        return (int) Math.floor(y);
    }

    public int getBlockZ() {
        return (int) Math.floor(z);
    }

    public byte getYawAsByte() {
        return (byte) (yaw * 256f / 360f);
    }

    public byte getPitchAsByte() {
        return (byte) (pitch * 256f / 360f);
    }

    public void writePos(FriendlyByteBuf buf) {
        buf.writeDouble(x)
                .writeDouble(y)
                .writeDouble(z);
    }

    public void writeRot(FriendlyByteBuf buf) {
        buf.writeByte(getYawAsByte())
                .writeByte(getPitchAsByte());
    }

    public void write(FriendlyByteBuf buf) {
        writePos(buf);
        writeRot(buf);
    }
}
