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
public class PacketPlayOutBorderSize extends PacketOut {

    private static final int ID = 0x46;

    @Getter @Setter
    private double diameter;

    static {
        register(PacketPlayOutBorderSize.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutBorderSize::new);
    }

    public PacketPlayOutBorderSize(final ServerBuffer buf) {
        diameter = buf.readDouble();
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
                .writeDouble(diameter)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutBorderSize(new FriendlyByteBuf(serialize()));
    }

}
