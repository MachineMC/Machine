package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.ServerBuffer;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutInitializeWorldBorder extends PacketOut {

    private static final int ID = 0x1F;

    private double x, z, oldDiameter, newDiameter;
    private long speed;
    private int portalTeleportBoundary; // Usually 29999984
    private int warningBlocks, warningTime;

    static {
        register(PacketPlayOutInitializeWorldBorder.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutInitializeWorldBorder::new);
    }

    public PacketPlayOutInitializeWorldBorder(@NotNull ServerBuffer buf) {
        x = buf.readDouble();
        z = buf.readDouble();
        oldDiameter = buf.readDouble();
        newDiameter = buf.readDouble();
        speed = buf.readVarLong();
        portalTeleportBoundary = buf.readVarInt();
        warningBlocks = buf.readVarInt();
        warningTime = buf.readVarInt();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public @NotNull PacketState getPacketState() {
        return PacketState.PLAY_OUT;
    }

    @Override
    public byte @NotNull [] serialize() {
        return new FriendlyByteBuf()
                .writeDouble(x)
                .writeDouble(z)
                .writeDouble(oldDiameter)
                .writeDouble(newDiameter)
                .writeVarLong(speed)
                .writeVarInt(portalTeleportBoundary)
                .writeVarInt(warningBlocks)
                .writeVarInt(warningTime)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutInitializeWorldBorder(new FriendlyByteBuf(serialize()));
    }

}
