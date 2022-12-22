package me.pesekjak.machine.translation.translators.in;

import me.pesekjak.machine.translation.PacketTranslator;
import me.pesekjak.machine.network.ClientConnection;
import me.pesekjak.machine.network.packets.in.status.PacketStatusInPing;
import me.pesekjak.machine.network.packets.out.status.PacketStatusOutPong;
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
