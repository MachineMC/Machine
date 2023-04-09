package org.machinemc.server.network.packets.out.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.scriptive.components.Component;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;

@AllArgsConstructor
@ToString
public class PacketLoginOutDisconnect extends PacketOut {

    private static final int ID = 0x00;

    static {
        register(PacketLoginOutDisconnect.class, ID, PacketState.LOGIN_OUT,
                PacketLoginOutDisconnect::new
        );
    }

    @Getter @Setter
    private Component message;

    public PacketLoginOutDisconnect(ServerBuffer buf) {
        message = buf.readComponent();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public PacketState getPacketState() {
        return PacketState.LOGIN_OUT;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeComponent(message)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketLoginOutDisconnect(new FriendlyByteBuf(serialize()));
    }

}
