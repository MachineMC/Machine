package org.machinemc.server.translation.translators.in;

import org.machinemc.server.translation.PacketTranslator;
import org.machinemc.server.network.ClientConnection;
import org.machinemc.server.network.packets.in.status.PacketStatusInRequest;
import org.machinemc.server.network.packets.out.status.PacketStatusOutResponse;

public class TranslatorStatusInRequest extends PacketTranslator<PacketStatusInRequest> {

    @Override
    public boolean translate(ClientConnection connection, PacketStatusInRequest packet) {
        return true;
    }

    @Override
    public void translateAfter(ClientConnection connection, PacketStatusInRequest packet) {
        connection.send(new PacketStatusOutResponse(connection.getServer().statusJson()));
    }

    @Override
    public Class<PacketStatusInRequest> packetClass() {
        return PacketStatusInRequest.class;
    }

}
