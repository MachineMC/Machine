package me.pesekjak.machine.events.translations.translators.in;

import me.pesekjak.machine.events.translations.PacketTranslator;
import me.pesekjak.machine.network.ClientConnection;
import me.pesekjak.machine.network.packets.in.PacketStatusInPing;
import me.pesekjak.machine.network.packets.out.PacketStatusOutPong;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class TranslatorStatusInPing extends PacketTranslator<PacketStatusInPing> {

    @Override
    public boolean translate(ClientConnection connection, PacketStatusInPing packet) {
        return true;
    }

    @Override
    public void translateAfter(ClientConnection connection, PacketStatusInPing packet) {
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
