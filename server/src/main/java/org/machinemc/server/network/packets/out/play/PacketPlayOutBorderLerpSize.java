package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutBorderLerpSize extends PacketOut {

    private static final int ID = 0x45;

    private double oldDiameter, newDiameter;
    private long speed;

    static {
        register(PacketPlayOutBorderLerpSize.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutBorderLerpSize::new);
    }

    public PacketPlayOutBorderLerpSize(ServerBuffer buf) {
        oldDiameter = buf.readDouble();
        newDiameter = buf.readDouble();
        speed = buf.readVarLong();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public PacketState getPacketState() {
        return PacketState.PLAY_OUT;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeDouble(oldDiameter)
                .writeDouble(newDiameter)
                .writeVarLong(speed)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutBorderLerpSize(new FriendlyByteBuf(serialize()));
    }

}
