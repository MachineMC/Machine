package org.machinemc.network.protocol.login.serverbound;

import lombok.Data;
import org.machinemc.network.protocol.PacketFlow;
import org.machinemc.network.protocol.PacketGroups;
import org.machinemc.network.protocol.login.LoginPacketListener;
import org.machinemc.paklet.Packet;

@Data
@Packet(
        id = PacketGroups.Login.ServerBound.LOGIN_ACKNOWLEDGED,
        group = PacketGroups.Login.ServerBound.NAME,
        catalogue = PacketGroups.Login.ServerBound.class
)
public class C2SLoginAcknowledgedPacket implements org.machinemc.network.protocol.Packet<LoginPacketListener> {

    @Override
    public void handle(LoginPacketListener listener) {
        listener.onLoginAcknowledged(this);
    }

    @Override
    public PacketFlow flow() {
        return PacketFlow.SERVERBOUND;
    }

}
