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
public class PacketPlayOutBorderWarningDelay extends PacketOut {

    private static final int ID = 0x47;

    @Getter @Setter
    private int warningTime;

    static {
        register(PacketPlayOutBorderWarningDelay.class, ID, Packet.PacketState.PLAY_OUT,
                PacketPlayOutBorderWarningDelay::new);
    }

    public PacketPlayOutBorderWarningDelay(final ServerBuffer buf) {
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
                .writeVarInt(warningTime)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutBorderWarningDelay(new FriendlyByteBuf(serialize()));
    }

}
