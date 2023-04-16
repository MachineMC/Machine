package org.machinemc.server.translation.translators.in;

import org.machinemc.server.network.ClientConnection;
import org.machinemc.server.translation.PacketTranslator;
import org.machinemc.server.network.packets.in.status.PacketStatusInPing;
import org.machinemc.server.network.packets.out.status.PacketStatusOutPong;

public class TranslatorStatusInPing extends PacketTranslator<PacketStatusInPing> {

    @Override
    public boolean translate(ClientConnection connection, PacketStatusInPing packet) {
        return true;
    }

    @Override
    public void translateAfter(ClientConnection connection, PacketStatusInPing packet) {
        connection.send(new PacketStatusOutPong(packet.getPayload()));
        connection.disconnect();
    }

    @Override
    public Class<PacketStatusInPing> packetClass() {
        return PacketStatusInPing.class;
    }

}
