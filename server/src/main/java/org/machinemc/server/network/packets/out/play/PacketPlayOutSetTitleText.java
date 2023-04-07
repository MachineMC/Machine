package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.scriptive.components.Component;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
public class PacketPlayOutSetTitleText extends PacketOut {

    private static final int ID = 0x5D;

    @Getter @Setter
    private Component text;

    static {
        register(PacketPlayOutSetTitleText.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSetTitleText::new);
    }

    public PacketPlayOutSetTitleText(ServerBuffer buf) {
        text = buf.readComponent();
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
                .writeComponent(text)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutSetTitleText(new FriendlyByteBuf(serialize()));
    }

}
