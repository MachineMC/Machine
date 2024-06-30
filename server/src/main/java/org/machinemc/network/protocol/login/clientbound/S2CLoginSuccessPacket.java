package org.machinemc.network.protocol.login.clientbound;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.machinemc.barebones.profile.GameProfile;
import org.machinemc.network.protocol.PacketFlow;
import org.machinemc.network.protocol.PacketGroups;
import org.machinemc.network.protocol.login.LoginPacketListener;
import org.machinemc.paklet.Packet;

@Data
@Packet(
        id = PacketGroups.Login.ClientBound.LOGIN_SUCCESS,
        group = PacketGroups.Login.ClientBound.NAME,
        catalogue = PacketGroups.Login.ClientBound.class
)
@NoArgsConstructor
@AllArgsConstructor
public class S2CLoginSuccessPacket implements org.machinemc.network.protocol.Packet<LoginPacketListener> {

    private GameProfile profile;
    private boolean strictErrorHandling;

    @Override
    public void handle(LoginPacketListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PacketFlow flow() {
        return PacketFlow.CLIENTBOUND;
    }

}
