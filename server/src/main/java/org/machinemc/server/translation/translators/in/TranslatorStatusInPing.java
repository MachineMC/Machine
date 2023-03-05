package org.machinemc.server.translation.translators.in;

import org.machinemc.server.translation.PacketTranslator;
import org.machinemc.server.network.ClientConnection;
import org.machinemc.server.network.packets.in.status.PacketStatusInPing;
import org.machinemc.server.network.packets.out.status.PacketStatusOutPong;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class TranslatorStatusInPing extends PacketTranslator<PacketStatusInPing> {

    @Override
    public boolean translate(@NotNull ClientConnection connection, @NotNull PacketStatusInPing packet) {
        return true;
    }

    @Override
    public void translateAfter(@NotNull ClientConnection connection, @NotNull PacketStatusInPing packet) {
        try {
            connection.sendPacket(new PacketStatusOutPong(packet.getPayload()));
        } catch (IOException ignored) { }
        connection.disconnect();
    }

    @Override
    public @NotNull Class<PacketStatusInPing> packetClass() {
        return PacketStatusInPing.class;
    }

}
