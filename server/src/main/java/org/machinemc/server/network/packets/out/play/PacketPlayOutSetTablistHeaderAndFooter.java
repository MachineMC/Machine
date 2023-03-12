package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.kyori.adventure.text.Component;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
public class PacketPlayOutSetTablistHeaderAndFooter extends PacketOut {

    private static final int ID = 0x63;

    @Getter @Setter
    private Component header, footer;

    static {
        register(PacketPlayOutSetTablistHeaderAndFooter.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSetTablistHeaderAndFooter::new);
    }

    public PacketPlayOutSetTablistHeaderAndFooter(ServerBuffer buf) {
        header = buf.readComponent();
        footer = buf.readComponent();
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
                .writeComponent(header)
                .writeComponent(footer)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutSetTablistHeaderAndFooter(new FriendlyByteBuf(serialize()));
    }

}
