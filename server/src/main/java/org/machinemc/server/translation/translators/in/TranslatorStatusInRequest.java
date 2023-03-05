package org.machinemc.server.translation.translators.in;

import org.machinemc.server.translation.PacketTranslator;
import org.machinemc.server.network.ClientConnection;
import org.machinemc.server.network.packets.in.status.PacketStatusInRequest;
import org.machinemc.server.network.packets.out.status.PacketStatusOutResponse;

import java.io.IOException;

public class TranslatorStatusInRequest extends PacketTranslator<PacketStatusInRequest> {

    @Override
    public boolean translate(ClientConnection connection, PacketStatusInRequest packet) {
        return true;
    }

    @Override
    public void translateAfter(ClientConnection connection, PacketStatusInRequest packet) {
        try {
            connection.sendPacket(new PacketStatusOutResponse(connection.getServer().statusJson()));
        } catch (IOException ignored) { }
    }

    @Override
    public Class<PacketStatusInRequest> packetClass() {
        return PacketStatusInRequest.class;
    }

}
