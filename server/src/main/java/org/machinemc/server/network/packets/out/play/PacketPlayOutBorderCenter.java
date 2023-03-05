package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
public class PacketPlayOutBorderCenter extends PacketOut {

    private static final int ID = 0x44;

    @Getter @Setter
    private double x, z;

    static {
        register(PacketPlayOutBorderCenter.class, ID, Packet.PacketState.PLAY_OUT,
                PacketPlayOutBorderCenter::new);
    }

    public PacketPlayOutBorderCenter(@NotNull ServerBuffer buf) {
        x = buf.readDouble();
        z = buf.readDouble();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public @NotNull Packet.PacketState getPacketState() {
        return Packet.PacketState.PLAY_OUT;
    }

    @Override
    public byte @NotNull [] serialize() {
        return new FriendlyByteBuf()
                .writeDouble(x)
                .writeDouble(z)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutBorderCenter(new FriendlyByteBuf(serialize()));
    }

}
