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
public class PacketPlayOutSetTitleAnimationTimes extends PacketOut {

    private static final int ID = 0x5E;

    @Getter @Setter
    private int fadeIn, stay, fadeOut;

    static {
        register(PacketPlayOutSetTitleAnimationTimes.class, ID, Packet.PacketState.PLAY_OUT,
                PacketPlayOutSetTitleAnimationTimes::new);
    }

    public PacketPlayOutSetTitleAnimationTimes(ServerBuffer buf) {
        fadeIn = buf.readInt();
        stay = buf.readInt();
        fadeOut = buf.readInt();
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
                .writeInt(fadeIn)
                .writeInt(stay)
                .writeInt(fadeOut)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutSetTitleAnimationTimes(new FriendlyByteBuf(serialize()));
    }

}
