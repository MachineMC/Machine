package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

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
        register(PacketPlayOutInitializeWorldBorder.class, ID, Packet.PacketState.PLAY_OUT,
                PacketPlayOutInitializeWorldBorder::new);
    }

    public PacketPlayOutInitializeWorldBorder(final ServerBuffer buf) {
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
    public Packet.PacketState getPacketState() {
        return Packet.PacketState.PLAY_OUT;
    }

    @Override
    public byte[] serialize() {
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
    public PacketOut clone() {
        return new PacketPlayOutInitializeWorldBorder(new FriendlyByteBuf(serialize()));
    }

}
