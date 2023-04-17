package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.scriptive.components.Component;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
public class PacketPlayOutSetSubtitleText extends PacketOut {

    private static final int ID = 0x5B;

    @Getter @Setter
    private Component text;

    static {
        register(PacketPlayOutSetSubtitleText.class, ID, Packet.PacketState.PLAY_OUT,
                PacketPlayOutSetSubtitleText::new);
    }

    public PacketPlayOutSetSubtitleText(final ServerBuffer buf) {
        text = buf.readComponent();
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
                .writeComponent(text)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutSetSubtitleText(new FriendlyByteBuf(serialize()));
    }

}
