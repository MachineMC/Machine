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
public class PacketPlayOutDisconnect extends PacketOut {

    private static final int ID = 0x19;

    @Getter @Setter
    private Component reason;

    static {
        register(PacketPlayOutDisconnect.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutDisconnect::new);
    }

    public PacketPlayOutDisconnect(final ServerBuffer buf) {
        reason = buf.readComponent();
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
                .writeComponent(reason)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutDisconnect(new FriendlyByteBuf(serialize()));
    }

}
