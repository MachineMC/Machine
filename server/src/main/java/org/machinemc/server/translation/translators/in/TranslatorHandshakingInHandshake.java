package org.machinemc.server.translation.translators.in;

import org.machinemc.api.network.PlayerConnection;
import org.machinemc.server.translation.PacketTranslator;
import org.machinemc.server.network.ClientConnection;
import org.machinemc.server.network.packets.in.handshaking.PacketHandshakingInHandshake;

public class TranslatorHandshakingInHandshake extends PacketTranslator<PacketHandshakingInHandshake> {

    @Override
    public boolean translate(ClientConnection connection, PacketHandshakingInHandshake packet) {
        return true;
    }

    @Override
    public void translateAfter(ClientConnection connection, PacketHandshakingInHandshake packet) {
        if(packet.getHandshakeType() == PacketHandshakingInHandshake.HandshakeType.STATUS) {
            connection.setState(PlayerConnection.ClientState.STATUS);
        } else if(packet.getHandshakeType() == PacketHandshakingInHandshake.HandshakeType.LOGIN) {
            connection.setState(PlayerConnection.ClientState.LOGIN);
        }
    }

    @Override
    public Class<PacketHandshakingInHandshake> packetClass() {
        return PacketHandshakingInHandshake.class;
    }

}
