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
public class PacketPlayOutClearTitles extends PacketOut {

    private static final int ID = 0x0D;

    @Getter @Setter
    private boolean reset;

    static {
        register(PacketPlayOutClearTitles.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutClearTitles::new);
    }

    public PacketPlayOutClearTitles(final ServerBuffer buf) {
        reset = buf.readBoolean();
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
                .writeBoolean(reset)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutClearTitles(new FriendlyByteBuf(serialize()));
    }

}
