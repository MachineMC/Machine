package org.machinemc.server.translation.translators.in;

import org.machinemc.server.network.ClientConnection;
import org.machinemc.server.translation.PacketTranslator;
import org.machinemc.server.network.packets.in.play.PacketPlayInKeepAlive;

public class TranslatorPlayInKeepAlive extends PacketTranslator<PacketPlayInKeepAlive> {

    @Override
    public boolean translate(final ClientConnection connection, final PacketPlayInKeepAlive packet) {
        if (packet.getKeepAliveId() != connection.getKeepAliveKey()) return false;
        connection.keepAlive();
        return true;
    }

    @Override
    public void translateAfter(final ClientConnection connection, final PacketPlayInKeepAlive packet) {

    }

    @Override
    public Class<PacketPlayInKeepAlive> packetClass() {
        return PacketPlayInKeepAlive.class;
    }

}
