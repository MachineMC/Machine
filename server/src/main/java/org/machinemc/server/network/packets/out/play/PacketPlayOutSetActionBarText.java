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
public class PacketPlayOutSetActionBarText extends PacketOut {

    private static final int ID = 0x43;

    @Getter @Setter
    private Component actionBar;

    static {
        register(PacketPlayOutSetActionBarText.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSetActionBarText::new);
    }

    public PacketPlayOutSetActionBarText(ServerBuffer buf) {
        actionBar = buf.readComponent();
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
                .writeComponent(actionBar)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutSetActionBarText(new FriendlyByteBuf(serialize()));
    }

}
